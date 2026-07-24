package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.TheaterStateRequest;
import aditkarki.movieticketingservicenew.dto.responses.TheaterStateResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.mapper.TheaterStateMapper;
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
public class TheaterStateService {
    private final TheaterStateMapper theaterStateMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final AggregationHelper aggregationHelper;
    private final String theaterStateNameConstant  = "theaterState";
    private final QueryFilterHelper queryFilterHelper;

    public TableResponse theaterStateSearch(TheaterStateRequest theaterStateRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size){

        // Query Builder
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, theaterStateNameConstant, theaterStateRequest.getTheaterState());

        // Aggreation Builder
        Map<String, Aggregation> theaterStatesAggregations = aggregationHelper.theaterAggregationBuilder(customSorting, sortBy, pageNumber, size, theaterStateNameConstant, theaterStateRequest.getAverageScreensTotal());

        //SearchRequest Builder
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.THEATERS_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(theaterStatesAggregations)
                .size(0));

        log.info("TheaterState Search request {}", searchRequest);

        //SearchResponse Builder
        SearchResponse<Void> response;
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e) {
            log.error("Error while searching theaterState", e);
            throw new SearchException("Cannot search theaterState aggregations");
        }

        LinkedList<TheaterStateResponse> theaterStateResponses = aggregationHelper.parseAggregationBuckets(response, theaterStateMapper::toTheaterStateResponse, theaterStateNameConstant + "Aggregations");

        Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, theaterStateResponses);

        return TableResponse.builder()
                .data(theaterStateResponses)
                .page(pageBuilder)
                .build();
    }
}
