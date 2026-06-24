package aditkarki.movieticketingservicenew.client;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieElasticSearchClient {
    private final ElasticsearchClient elasticsearchClient;
    public List<MovieDocument> searchByMovie(String query) throws IOException {
        // Searches for names of movies and director
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .queryString(qs -> qs
                                .fields("title", "director")
                                .query(query)
                                .analyzeWildcard(true)
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByYear(LocalDate startDate, LocalDate endDate) throws IOException {
        // Searches between a Start and End date for movies
        String startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .range(r -> r
                                .date(d -> d
                                        .field("releaseDate")
                                        .gte(startDateString)
                                        .lte(endDateString)
                                )
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByGenre(String query) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
            .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                .term(t -> t
                        .field("genres")
                        .value(query)
                        .caseInsensitive(true)
                )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByTag(String query) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .term(t -> t
                                .field("tags")
                                .value(query)
                                .caseInsensitive(true)
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByLanguage(String query) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .term(t -> t
                                .field("language")
                                .value(query)
                                .caseInsensitive(true)
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByDuration(Integer minDuration, Integer maxDuration) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .range(r -> r
                                .number(n -> n
                                        .field("duration")
                                        .gte((double)minDuration)
                                        .lte((double)maxDuration)
                                )
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByRating(Double minRating, Double maxRating) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .range(r -> r
                                .number(n -> n
                                        .field("rating")
                                        .gte(minRating)
                                        .lte(maxRating)
                                )
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<MovieDocument> searchByIsActive(Boolean isActive) throws IOException {
        SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(q -> q
                        .term(t -> t
                                .field("isActive")
                                .value(isActive)
                        )
                ), MovieDocument.class);

        return response.hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

}
