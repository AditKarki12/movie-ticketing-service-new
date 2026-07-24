package aditkarki.movieticketingservicenew.helper;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.enums.CustomOperator;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AggregationHelper {


    public Map<String, Aggregation> buildAverageAggregations(Map<String, String> aggregationItems) {
        Map<String, Aggregation> aggregationMap = new HashMap<>();

        for (Map.Entry<String, String> item : aggregationItems.entrySet()) {
            String aggregationName = item.getKey();
            String fieldName = item.getValue();
            Aggregation averageAgg = Aggregation.of(a -> a.avg(avg -> avg.field(fieldName)));
            aggregationMap.put(aggregationName, averageAgg);
        }

        return aggregationMap;
    }

    public void applyAggRangeFilter(Map<String, Aggregation> aggregationMap, RangeDto filters, String fieldName) {
        if (filters != null) {
            addRangeAggregationFilter(aggregationMap, filters, fieldName);
        }
    }

    public void addRangeAggregationFilter(Map<String, Aggregation> aggregationMap, RangeDto filter, String fieldName) {
        if (filter == null || filter.getOperator() == null || filter.getValue1() == null)
            // Add logging and/or exception here
            return;

        if (filter.getOperator() == CustomOperator.BETWEEN) {
            if (filter.getValue2() == null)
                // Add logging and/or exception here
                return;
            Double min = Double.parseDouble(filter.getValue1()); // Parse into double since we inputing numbers
            Double max = Double.parseDouble(filter.getValue2());

            aggregationMap.put(fieldName + "Filter", Aggregation.of(a -> a // We create a Aggregation object
                    .bucketSelector(bs -> bs // This aggregation object is a bucket selector which filters buckets after they have been computed
                            .bucketsPath(bp -> bp.dict(Map.of(fieldName, fieldName))) // Connects to our average Rating/Duration objects which we stored in AggregationMap, where we can also choose to give it a new var name.
                            .script(s -> s
                                    .source("params." + fieldName + " >= params.min && params." + fieldName + " <= params.max") // Runs a script to ES that compares the director averages to inputted between
                                    .params(Map.of("min", JsonData.of(min), "max", JsonData.of(max))) // Allows us to dynamically set between operations min and max in our source
                            )
                    )
            ));
        } else {
            String symbol = customOperatortoString(filter.getOperator());
            Double compareNumber = Double.parseDouble(filter.getValue1());

            aggregationMap.put(fieldName + "Filter", Aggregation.of(a -> a
                    .bucketSelector(bs -> bs
                            .bucketsPath(bp -> bp.dict(Map.of(fieldName, fieldName)))
                            .script(s -> s
                                    .source("params." + fieldName + " " + symbol + " params.compareNumber")
                                    .params(Map.of("compareNumber", JsonData.of(compareNumber)))
                            )
                    )
            ));
        }
    }

    public void addAggregationSortFilter(Map<String, Aggregation> aggregationMap, CustomSorting customSorting, String fieldName, Page pagination) {
        int from = pagination.getPageNumber() * pagination.getSize();
        int size = pagination.getSize();
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        if(fieldName.equals("director")){
            aggregationMap.put("sort", Aggregation.of(a -> a
                            .bucketSort(bs -> bs // Decides the order and the way they return
                                    .sort(so -> so // Decides order of the buckets
                                            .field(f -> f
                                                    .field("_key") // ES keyword for alphabetic sorting
                                                    .order(sortOrder)
                                            )
                                    )
                                    .from(from)
                                    .size(size)
                            )
                    )
            );
        } else{
            aggregationMap.put("sort", Aggregation.of(a -> a
                            .bucketSort(bs -> bs
                                    .sort(so -> so
                                            .field(f -> f
                                                    .field(fieldName)
                                                    .order(sortOrder)
                                            )
                                    )
                                    .from(from)
                                    .size(size)
                            )
                    )
            );
        }
    }

    public <T> LinkedList<T> parseAggregationBuckets(SearchResponse<?> response, Function<StringTermsBucket, T> mapper, String aggregationName) {

        return response.aggregations()
                .get(aggregationName)
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(mapper)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public Map<String, Aggregation> movieAggregationBuilder(CustomSorting customSorting, String sortBy, int pageNumber, int size, String index, NumericRangeDto averageRating, NumericRangeDto averageDuration) {
        Map<String, Aggregation> aggregationMap = buildAverageAggregations(ElasticSearchConstants.MOVIE_AGGREGATION_FIELDS);

        Map<String, RangeDto> filters = new HashMap<>();
        filters.put("averageRating", averageRating);
        filters.put("averageDuration", averageDuration);

        for (Map.Entry<String, RangeDto> entry : filters.entrySet()) {
            applyAggRangeFilter(aggregationMap, entry.getValue(), entry.getKey());
        }

        Page pagination = PageUtils.pagination(pageNumber, size);

        addAggregationSortFilter(aggregationMap, customSorting, sortBy, pagination);

        Aggregation aggregation = Aggregation.of(a -> a // Creates the buckets and aggregates based on aggregationMap
                .terms(t -> t.field(index).size(5)).aggregations(aggregationMap));

        Map<String, Aggregation> aggregations = new HashMap<>();
        aggregations.put(index + "Aggregations", aggregation);
        return aggregations;
    }

    public Map<String, Aggregation> theaterAggregationBuilder(CustomSorting customSorting, String sortBy, int pageNumber, int size, String index, NumericRangeDto averageScreensTotal) {
        Map<String, Aggregation> aggregationMap = buildAverageAggregations(ElasticSearchConstants.THEATER_AGGREGATION_FIELDS);

        Map<String, RangeDto> filters = new HashMap<>();
        filters.put("averageScreensTotal", averageScreensTotal);

        for (Map.Entry<String, RangeDto> entry : filters.entrySet()) {
            applyAggRangeFilter(aggregationMap, entry.getValue(), entry.getKey());
        }

        Page pagination = PageUtils.pagination(pageNumber, size);

        addAggregationSortFilter(aggregationMap, customSorting, sortBy, pagination);

        Aggregation aggregation = Aggregation.of(a -> a // Creates the buckets and aggregates based on aggregationMap
                .terms(t -> t.field(index).size(5)).aggregations(aggregationMap));

        Map<String, Aggregation> aggregations = new HashMap<>();
        aggregations.put(index + "Aggregations", aggregation);
        return aggregations;
    }

    private String customOperatortoString(CustomOperator operator) {
        return switch (operator) {
            case GT -> ">";
            case LT -> "<";
            case GTE -> ">=";
            case LTE -> "<=";
            case EQ -> "==";
            default -> throw new IllegalArgumentException("Unsupported operator for bucket selector: " + operator);
        };
    }

}