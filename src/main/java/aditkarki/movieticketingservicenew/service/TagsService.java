package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.TagsRequest;
import aditkarki.movieticketingservicenew.dto.responses.TagResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.mapper.TagMapper;
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
public class TagsService {
    private final TagMapper tagMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final AggregationHelper aggregationHelper;
    private final String tagNameConstant  = "tags";
    private final QueryFilterHelper queryFilterHelper;

    public TableResponse tagsSearch(TagsRequest tagsRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size){

        // Query Builder
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, tagNameConstant, tagsRequest.getTag());

        // Aggreation Builder
        Map<String, Aggregation> tagsAggregations = aggregationHelper.movieAggregationBuilder(customSorting, sortBy, pageNumber, size, tagNameConstant, tagsRequest.getAverageRating(), tagsRequest.getAverageDuration());

        //SearchRequest Builder
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(tagsAggregations)
                .size(0));

        log.info("Tags Search request {}", searchRequest);

        //SearchResponse Builder
        SearchResponse<Void> response;
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e) {
            log.error("Error while searching tags", e);
            throw new SearchException("Cannot search tags aggregations");
        }

        LinkedList<TagResponse> tagsResponses = aggregationHelper.parseAggregationBuckets(response, tagMapper::toTagResponse, tagNameConstant + "Aggregations");

        Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, tagsResponses);

        return TableResponse.builder()
                .data(tagsResponses)
                .page(pageBuilder)
                .build();
    }
}
