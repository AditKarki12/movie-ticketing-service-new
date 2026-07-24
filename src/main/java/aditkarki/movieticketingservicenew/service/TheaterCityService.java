package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.TheaterCityRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.dto.responses.TheaterCityResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.mapper.TheaterCityMapper;
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
public class TheaterCityService {
    private final TheaterCityMapper theaterCityMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final AggregationHelper aggregationHelper;
    private final String theaterCityNameConstant  = "theaterCity";
    private final QueryFilterHelper queryFilterHelper;

    public TableResponse theaterCitySearch(TheaterCityRequest theaterCityRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size){

        // Query Builder
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, theaterCityNameConstant, theaterCityRequest.getTheaterCity());

        // Aggreation Builder
        Map<String, Aggregation> theaterCitysAggregations = aggregationHelper.theaterAggregationBuilder(customSorting, sortBy, pageNumber, size, theaterCityNameConstant, theaterCityRequest.getAverageScreensTotal());

        //SearchRequest Builder
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.THEATERS_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(theaterCitysAggregations)
                .size(0));

        log.info("TheaterCity Search request {}", searchRequest);

        //SearchResponse Builder
        SearchResponse<Void> response;
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e) {
            log.error("Error while searching theaterCity", e);
            throw new SearchException("Cannot search theaterCity aggregations");
        }

        LinkedList<TheaterCityResponse> theaterCityResponses = aggregationHelper.parseAggregationBuckets(response, theaterCityMapper::toTheaterCityResponse, theaterCityNameConstant + "Aggregations");

        Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, theaterCityResponses);

        return TableResponse.builder()
                .data(theaterCityResponses)
                .page(pageBuilder)
                .build();
    }
}
