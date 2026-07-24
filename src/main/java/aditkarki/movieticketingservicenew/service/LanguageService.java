package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.LanguageRequest;
import aditkarki.movieticketingservicenew.dto.responses.LanguageResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.AggregationHelper;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.mapper.LanguageMapper;
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
public class LanguageService {
    private final LanguageMapper languageMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final AggregationHelper aggregationHelper;
    private final String languageNameConstant  = "language";
    private final QueryFilterHelper queryFilterHelper;

    public TableResponse languageSearch(LanguageRequest languageRequest, CustomSorting customSorting, String sortBy, int pageNumber, int size){

        // Query Builder
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        queryFilterHelper.handleListField(boolQuery, languageNameConstant, languageRequest.getLanguage());

        // Aggreation Builder
        Map<String, Aggregation> languagesAggregations = aggregationHelper.movieAggregationBuilder(customSorting, sortBy, pageNumber, size, languageNameConstant, languageRequest.getAverageRating(), languageRequest.getAverageDuration());

        //SearchRequest Builder
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .aggregations(languagesAggregations)
                .size(0));

        log.info("Language Search request {}", searchRequest);

        //SearchResponse Builder
        SearchResponse<Void> response;
        try{
            response = elasticsearchClient.search(searchRequest, Void.class);
        } catch (IOException e) {
            log.error("Error while searching language", e);
            throw new SearchException("Cannot search language aggregations");
        }

        LinkedList<LanguageResponse> languageResponses = aggregationHelper.parseAggregationBuckets(response, languageMapper::toLanguageResponse, languageNameConstant + "Aggregations");

        Page pageBuilder = PageUtils.pageBuilder(pageNumber, size, languageResponses);

        return TableResponse.builder()
                .data(languageResponses)
                .page(pageBuilder)
                .build();
    }

}




