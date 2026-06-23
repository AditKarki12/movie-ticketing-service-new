package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.client.MovieElasticSearchClient;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import aditkarki.movieticketingservicenew.repository.MovieRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ElasticsearchClient elasticsearchClient;

    public MovieResponse createMovie(MovieRequest movieRequest) {
        if(movieRepository.existsByTitle(movieRequest.getTitle())) {
            throw new DuplicateResourceException(movieRequest.getTitle());
        }
        Movie movie = movieMapper.toEntity(movieRequest);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(savedMovie);
    }

    public MovieResponse getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream().map(movieMapper::toResponse).toList();
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest movieRequest) {
        return movieRepository.findById(movieId).map(existingMovie -> {
            movieMapper.updateEntityFromRequest(movieRequest, existingMovie);
            movieRepository.save(existingMovie);
            return movieMapper.toResponse(existingMovie);
        }).orElseThrow(() -> new ResourceNotFoundException("Movie", movieId));
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        movieRepository.delete(movie);
    }

    public List<MovieResponse> getMovie(MovieSearchRequest movieSearchRequest) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Can slim these 3 blocks down with just the original addRangeFilters using Ternary as an input
        if(movieSearchRequest.getMinDuration() != null && movieSearchRequest.getMaxDuration() != null) {
            addRangeFilter(boolQuery, "duration", (double) movieSearchRequest.getMinDuration(), (double)movieSearchRequest.getMaxDuration());
        } else if(movieSearchRequest.getMinDuration() != null){
            addRangeGTEFilter(boolQuery, "duration", (double) movieSearchRequest.getMinDuration());
        } else if(movieSearchRequest.getMaxDuration() != null){
            addRangeLTEFilter(boolQuery, "duration", (double) movieSearchRequest.getMaxDuration());
        }

        if(movieSearchRequest.getMinRating() != null && movieSearchRequest.getMaxRating() != null) {
            addRangeFilter(boolQuery, "rating", movieSearchRequest.getMinRating(), movieSearchRequest.getMaxRating());
        } else if(movieSearchRequest.getMinRating() != null){
            addRangeGTEFilter(boolQuery, "rating",  movieSearchRequest.getMinRating());
        } else if(movieSearchRequest.getMaxRating() != null){
            addRangeLTEFilter(boolQuery, "rating", movieSearchRequest.getMaxRating());
        }

        if(movieSearchRequest.getStartReleaseDate() != null && movieSearchRequest.getEndReleaseDate() != null) {
            addDateRangeFilter(boolQuery, "releaseDate", movieSearchRequest.getStartReleaseDate(), movieSearchRequest.getEndReleaseDate());
        } else if(movieSearchRequest.getStartReleaseDate() != null){
            addDateRangeGTEFilter(boolQuery, "releaseDate", movieSearchRequest.getStartReleaseDate());
        } else if(movieSearchRequest.getEndReleaseDate() != null){
            addDateRangeLTEFilter(boolQuery, "releaseDate", movieSearchRequest.getEndReleaseDate());
        }

        for(Field field: movieSearchRequest.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Gives us access to private fields
            String fieldName = field.getName();

            if(List.class.isAssignableFrom(field.getType())) {
                try{
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
            }
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("movies")
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

        if (value instanceof Boolean b) {
            boolQuery.filter(f -> f.term(t -> t.field(fieldName).value(b)));
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

    private void addRangeFilter(BoolQuery.Builder boolQuery, String fieldName, Double min, Double max) {
        if (min == null && max == null)
            return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .number(n -> {
                            n.field(fieldName);
                            if (min != null)
                                n.gte(min);
                            if (max != null)
                                n.lte(max);
                            return n;
                        })
                )
        );
    }

    private void addRangeGTEFilter(BoolQuery.Builder boolQuery, String fieldName, Double min) {
        if (min == null)
            return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .number(n -> {
                            n.field(fieldName);
                            if (min != null)
                                n.gte(min);
                            return n;
                        })
                )
        );
    }

    private void addRangeLTEFilter(BoolQuery.Builder boolQuery, String fieldName, Double max) {
        if (max == null)
            return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .number(n -> {
                            n.field(fieldName);
                            if (max != null)
                                n.lte(max);
                            return n;
                        })
                )
        );
    }

    private void addDateRangeFilter(BoolQuery.Builder boolQuery, String fieldName, LocalDate start, LocalDate end) {
        if (start == null && end == null) return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .date(d -> {
                            d.field(fieldName);
                            if (start != null)
                                d.gte(start.format(DateTimeFormatter.ISO_LOCAL_DATE));
                            if (end != null)
                                d.lte(end.format(DateTimeFormatter.ISO_LOCAL_DATE));
                            return d;
                        })
                )
        );
    }

    private void addDateRangeGTEFilter(BoolQuery.Builder boolQuery, String fieldName, LocalDate start) {
        if (start == null) return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .date(d -> {
                            d.field(fieldName);
                                d.gte(start.format(DateTimeFormatter.ISO_LOCAL_DATE));
                            return d;
                        })
                )
        );
    }

    private void addDateRangeLTEFilter(BoolQuery.Builder boolQuery, String fieldName, LocalDate end) {
        if (end == null)
            return;
        boolQuery.filter(f -> f
                .range(r -> r
                        .date(d -> {
                            d.field(fieldName);
                                d.lte(end.format(DateTimeFormatter.ISO_LOCAL_DATE));
                            return d;
                        })
                )
        );
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
