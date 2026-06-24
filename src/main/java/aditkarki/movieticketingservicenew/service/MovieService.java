package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.CustomOperator;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.exception.MovieSearchException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.helper.QueryHelperMethods;
import aditkarki.movieticketingservicenew.manager.MovieManager;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService implements MovieServiceInterface{
    private final MovieManager movieManager;
    private final MovieMapper movieMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final QueryHelperMethods queryHelperMethods;

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

            Object fieldValue;
            try{
                fieldValue = field.get(movieSearchRequest);
            } catch(IllegalAccessException e){
                log.error(e.getMessage());
                throw new ReflectionAccessException("Cannot access field " + fieldName);
            }

            if(List.class.isAssignableFrom(field.getType())) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) fieldValue;
                queryHelperMethods.handleListField(boolQuery, fieldName, values);

            } else if(String.class.isAssignableFrom(field.getType())) {
                String value = (String) fieldValue;
                if(ElasticSearchConstants.MATCH_FIELDS.contains(fieldName)) {
                    queryHelperMethods.addMatchFilter(boolQuery, fieldName, value);
                } else{
                    queryHelperMethods.addTermsFilter(boolQuery, fieldName, value);
                }

            } else if (Boolean.class.isAssignableFrom(field.getType())) {
                Boolean value = (Boolean) fieldValue;
                queryHelperMethods.addTermsFilter(boolQuery, fieldName, value);

            } else if(RangeDto.class.isAssignableFrom(field.getType())) {
                RangeDto value = (RangeDto) fieldValue;
                if (value == null || value.getValue1() == null || value.getOperator() == null)
                    continue;

                if(value.getOperator() == CustomOperator.EQ) {
                    queryHelperMethods.addTermsFilter(boolQuery, fieldName, value);
                } else if (value instanceof DateRangeDto) {
                    queryHelperMethods.addDateRangeFilter(boolQuery, fieldName, value);
                } else if (value instanceof NumericRangeDto) {
                    queryHelperMethods.addRangeFilter(boolQuery, fieldName, value);
                }

            }
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .size(10)
        );

        log.info("Movie Search request {}", searchRequest);

        SearchResponse<MovieDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, MovieDocument.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new MovieSearchException("Cannot search movie documents");
        }

        return response.hits().hits()
                .stream()
                .map(Hit::source) // Turns from Hit to MovieDocument
                .map(movieMapper::toResponse) //Turns from MovieDocument to MovieResponse
                .toList();
    }

}
