package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.client.MovieElasticSearchClient;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import aditkarki.movieticketingservicenew.repository.MovieRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieElasticSearchClient movieElasticSearchClient;
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

    public List<MovieResponse> getMovie(MovieRequest movieRequest) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        for(Field field: movieRequest.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if(fieldName.equalsIgnoreCase("title")) {
                movieElasticSearchClient.addTermsFilter(boolQueryBuilder, fieldName, movieRequest.getTitle());
            }
        }
        SearchRequest searchRequest = SearchRequest
                .of(s -> s
                        .index("movies")
                        .query(boolQueryBuilder.build())
                        .size(10)
                );

        log.info("Searching for movies"+searchRequest);

        SearchResponse<ObjectNode> searchResponse;
        try {
            searchResponse  = elasticsearchClient.search(searchRequest, ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(searchResponse);

        return new ArrayList<>();
//        try{
//            return movieElasticSearchClient.searchByName(query)
//                    .stream()
//                    .map(movieMapper::toResponse)
//                    .toList();
//        } catch (IOException e) {
//            log.error("Elasticsearch search error: {}", e.getMessage());
//            return List.of();
//        }
    }

    public List<MovieResponse> searchByReleaseDate(LocalDate startDate, LocalDate endDate) {
        try{
            return movieElasticSearchClient.searchByYear(startDate, endDate)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Release Date search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByGenre(String genre) {
        try{
            return movieElasticSearchClient.searchByGenre(genre)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Search Genre search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByTag(String tag) {
        try {
            return movieElasticSearchClient.searchByTag(tag)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Search Tag search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByLanguage(String language) {
        try {
            return movieElasticSearchClient.searchByLanguage(language)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Search Language search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByDuration(int min, int max) {
        try {
            return movieElasticSearchClient.searchByDuration(min, max)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Search Duration search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByRating(double min, double max) {
        try {
            return movieElasticSearchClient.searchByRating(min, max)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch Search Rating search error: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieResponse> searchByIsActive(Boolean isActive) {
        try {
            return movieElasticSearchClient.searchByIsActive(isActive)
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch isActive error: {}", e.getMessage());
            return List.of();
        }
    }

}
