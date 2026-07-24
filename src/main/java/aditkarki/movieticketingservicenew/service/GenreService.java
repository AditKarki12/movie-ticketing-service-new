package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.GenreRequest;
import aditkarki.movieticketingservicenew.dto.responses.GenreResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.mapper.GenreMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreMapper genreMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final AggregationHelper aggregationHelper;
    private final String genreNameConstant  = "genres";
    private final QueryFilterHelper queryFilterHelper;

    public TableResponse genreSearch(GenreRequest genreRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size){

        // Query Builder
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, genreNameConstant, genreRequest.getGenre());

        // Aggreation Builder
        Map<String, Aggregation> genresAggregations = aggregationHelper.movieAggregationBuilder(customSorting, sortBy, pageNumber, size, genreNameConstant, genreRequest.getAverageRating(), genreRequest.getAverageDuration());

        //SearchRequest Builder
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(genresAggregations)
                .size(0));

        log.info("Genre Search request {}", searchRequest);

        //SearchResponse Builder
        SearchResponse<Void> response;
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e) {
            log.error("Error while searching genre", e);
            throw new SearchException("Cannot search genre aggregations");
        }

        LinkedList<GenreResponse> genreResponses = aggregationHelper.parseAggregationBuckets(response, genreMapper::toGenreResponse, genreNameConstant + "Aggregations");

        Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, genreResponses);

        return TableResponse.builder()
                .data(genreResponses)
                .page(pageBuilder)
                .build();
    }
}
