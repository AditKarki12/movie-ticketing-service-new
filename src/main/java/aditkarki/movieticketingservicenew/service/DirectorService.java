package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.DirectorRequest;
import aditkarki.movieticketingservicenew.dto.responses.DirectorResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SearchRequestHelper;
import aditkarki.movieticketingservicenew.mapper.DirectorMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorMapper directorMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final QueryFilterHelper queryFilterHelper;
    private final SearchRequestHelper searchRequestHelper;
    private final AggregationHelper aggregationHelper;
    private final String directorNameConstant = "director";

    public TableResponse directorSearch(DirectorRequest directorRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size) {

        // Query Build
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, directorNameConstant, directorRequest.getDirector());

        // Aggregation Build
        Map<String, Aggregation> directorAggregations = directorAggregationBuilder(directorRequest, customSorting, sortBy, pageNumber, size);

        // SearchRequest Build
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(directorAggregations)
                .size(0));

        log.info("Director Search request {}", searchRequest);

        // SearchResponse Build
        SearchResponse<Void> response = null; // We do a null SearchResponse because we aren't returning any actual docs
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e){
            log.error(e.getMessage());
            // Add exception here
        }

        // Response Parse
        if(response != null){
            LinkedList<DirectorResponse> directorResponses = aggregationHelper.parseAggregationBuckets(response, directorMapper::toDirectorResponse, "directorAggregations");

            Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, directorResponses);

            return TableResponse.builder()
                    .data(directorResponses)
                    .page(pageBuilder)
                    .build();
        } else {
            throw new ResourceNotFoundException("No director found"); // Change this exception
        }
    }

    private Map<String, Aggregation> directorAggregationBuilder(DirectorRequest directorRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size) {
        Map<String, Aggregation> aggregationMap = aggregationHelper.buildAverageAggregations(ElasticSearchConstants.DIRECTOR_AGGREGATION_FIELDS);

        Map<String, RangeDto> filters = new HashMap<>();
        filters.put("averageRating", directorRequest.getAverageRating());
        filters.put("averageDuration", directorRequest.getAverageDuration());

        for (Map.Entry<String, RangeDto> entry : filters.entrySet()) {
            aggregationHelper.applyAggRangeFilter(aggregationMap, entry.getValue(), entry.getKey());
        }

        Page pagination = PageUtils.pagination(pageNumber, size);

        aggregationHelper.addAggregationSortFilter(aggregationMap, customSorting, sortBy, pagination);

        Aggregation directorAggregation = Aggregation.of(a -> a // Creates the buckets and aggregates based on aggregationMap
                .terms(t -> t.field(directorNameConstant).size(5)).aggregations(aggregationMap));

        Map<String, Aggregation> directorAggregations = new HashMap<>();
        directorAggregations.put("directorAggregations", directorAggregation);
        return directorAggregations;
    }

}