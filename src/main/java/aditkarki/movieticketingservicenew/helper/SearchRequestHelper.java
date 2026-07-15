package aditkarki.movieticketingservicenew.helper;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.Page;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SearchRequestHelper {

    public SearchRequest.Builder searchRequestBuilder(BoolQuery.Builder boolQuery, Page pagination, String sortingField, SortOrder sortOrder) {
        return new SearchRequest.Builder()
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true));
    }
}