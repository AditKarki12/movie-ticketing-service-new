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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public List<MovieResponse> getMovie(MovieSearchRequest request) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        addTermsFilter(boolQuery, "title.keyword", request.getTitle());
        addTermsFilter(boolQuery, "director", request.getDirector());
        addTermsFilter(boolQuery, "isActive", request.getIsActive());
        handleListField(boolQuery, "genres", request.getGenres());
        handleListField(boolQuery, "language", request.getLanguage());
        handleListField(boolQuery, "tags", request.getTags());
        addRangeFilter(boolQuery, "duration",
                request.getMinDuration() != null ? request.getMinDuration().doubleValue() : null,
                request.getMaxDuration() != null ? request.getMaxDuration().doubleValue() : null);
        addRangeFilter(boolQuery, "rating", request.getMinRating(), request.getMaxRating());
        addDateRangeFilter(boolQuery, "releaseDate", request.getStartReleaseDate(), request.getEndReleaseDate());

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
                .map(Hit::source)
                .map(movieMapper::toResponse)
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
        if (min == null && max == null) return;
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

}
