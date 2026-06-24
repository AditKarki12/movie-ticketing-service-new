package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.CustomOperator;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.manager.MovieManager;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieManager movieManager;
    private final MovieMapper movieMapper;
    private final ElasticsearchClient elasticsearchClient;

    public MovieResponse createMovie(MovieRequest movieRequest) {
        if(movieManager.existsByTitle(movieRequest.getTitle())) {
            throw new DuplicateResourceException(movieRequest.getTitle());
        }
        Movie movie = movieMapper.toEntity(movieRequest);
        Movie savedMovie = movieManager.saveMovie(movie);
        return movieMapper.toResponse(savedMovie);
    }

    public MovieResponse getMovieById(Long movieId) {
        Movie movie = movieManager.findById(movieId);
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieManager.findAll().stream().map(movieMapper::toResponse).toList();
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest movieRequest) {
        Movie existingMovie = movieManager.findById(movieId);
        movieMapper.updateEntityFromRequest(movieRequest, existingMovie);
        movieManager.saveMovie(existingMovie);
        return movieMapper.toResponse(existingMovie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        movieManager.deleteMovie(movieId);
    }

    public List<MovieResponse> getMovie(MovieSearchRequest movieSearchRequest) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        for(Field field: movieSearchRequest.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Gives us access to private fields
            String fieldName = field.getName();

            if(List.class.isAssignableFrom(field.getType())) {
                try{
                    @SuppressWarnings("unchecked")
                    List<String> values = (List<String>) field.get(movieSearchRequest);
                    handleListField(boolQuery, fieldName, values);
                } catch (IllegalAccessException e){
                    log.error(e.getMessage());
                    throw new ResourceNotFoundException(fieldName);
                }
            } else if(String.class.isAssignableFrom(field.getType())) {
                try{
                    String value = (String)field.get(movieSearchRequest);
                    if(field.getName().equals("title") || field.getName().equals("description")) {
                        addMatchFilter(boolQuery, fieldName, value);
                    } else{
                        addTermsFilter(boolQuery, fieldName, value);
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } else if (Boolean.class.isAssignableFrom(field.getType())) {
                try{
                    Boolean value = (Boolean) field.get(movieSearchRequest);
                    addTermsFilter(boolQuery, fieldName, value);
                } catch (IllegalAccessException e){
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } else if(RangeDto.class.isAssignableFrom(field.getType())) {
                try{
                    RangeDto value = (RangeDto) field.get(movieSearchRequest);
                    if (value == null || value.getValue1() == null || value.getOperator() == null)
                        continue;
                    if(value.getOperator() == CustomOperator.EQ) {
                        addTermsFilter(boolQuery, fieldName, value);
                    } else{
                        if(field.getName().equals("releaseDate")) {
                            addDateRangeFilter(boolQuery, fieldName, value);
                        } else {
                            addRangeFilter(boolQuery, fieldName, value);
                        }
                    }
                } catch (IllegalAccessException e){
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX) // DONT HARD CODE MAKE GLOBAL CONSTANT HERE
                .query(boolQuery.build()._toQuery())
                .size(10)
        );

        log.info("Movie Search request {}", searchRequest);

        SearchResponse<MovieDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, MovieDocument.class);
        } catch (IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }

        return response.hits().hits()
                .stream()
                .map(Hit::source) // Turns from Hit to MovieDocument
                .map(movieMapper::toResponse) //Turns from MovieDocument to MovieResponse
                .toList();
    }

    // Private Fields

    private void addTermsFilter(BoolQuery.Builder boolQuery, String fieldName, Object value) {
        if (value == null) return;
        if (value instanceof String str && str.isBlank()) return;

        if (value instanceof Boolean bool) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field(fieldName)
                            .value(bool)
                    )
            );
        } else if (value instanceof RangeDto rangeDto){
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field(fieldName)
                            .value(rangeDto.getValue1())
                    )
            );
        } else {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field(fieldName)
                            .value(v -> v.anyValue(JsonData.of(value)))
                            .caseInsensitive(true)
                    )
            );
        }
    }

    private void addRangeFilter(BoolQuery.Builder boolQuery, String fieldName, RangeDto value) {
        if (value == null || value.getValue1() == null || value.getOperator() == null)
            return;

        switch (value.getOperator()) {
            case BETWEEN:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gte(Double.parseDouble(value.getValue1())).lte(Double.parseDouble(value.getValue2())))));
                break;
            case GT:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gt(Double.parseDouble(value.getValue1())))));
                break;
            case LT:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).lt(Double.parseDouble(value.getValue1())))));
                break;
            case GTE: boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gte(Double.parseDouble(value.getValue1())))));
                break;
            case LTE: boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).lte(Double.parseDouble(value.getValue1())))));
                break;


        }


    }

    private void addDateRangeFilter(BoolQuery.Builder boolQuery, String fieldName, RangeDto value) {
        if (value == null || value.getValue1() == null || value.getOperator() == null)
            return;

        switch (value.getOperator()) {
            case BETWEEN:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gte(value.getValue1()).lte(value.getValue2()))));
                break;
            case GT:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gt(value.getValue1()))));
                break;
            case LT:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).lt((value.getValue1())))));
                break;
            case GTE:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gte((value.getValue1())))));
                break;
            case LTE:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).lte((value.getValue1())))));
                break;
        }
    }

    private void handleListField(BoolQuery.Builder boolQuery, String fieldName, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        // This converts our list into of type FieldValue which allows ES to read the list
        List<FieldValue> fieldValues = values.stream()
                .filter(f -> f != null && !f.isBlank())
                .map(FieldValue::of)
                .toList();


        boolQuery.filter(f -> f
                .terms(t -> t
                        .field(fieldName)
                        .terms(tf -> tf.value(fieldValues))
                )
        );
    }

    private void addMatchFilter(BoolQuery.Builder boolQuery, String fieldName, String value) {
        if (value == null || value.isEmpty())
            return;

        boolQuery.filter(f -> f
                .match(t -> t
                        .field(fieldName)
                        .query(value)
                )
        );
    }

}
