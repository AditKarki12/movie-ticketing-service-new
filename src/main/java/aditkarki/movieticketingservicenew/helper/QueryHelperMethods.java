package aditkarki.movieticketingservicenew.helper;

import aditkarki.movieticketingservicenew.dto.RangeDto;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class QueryHelperMethods {

    public void addTermsFilter(BoolQuery.Builder boolQuery, String fieldName, Object value) {
        if (value == null)
            return;
        if (value instanceof String str && str.isBlank())
            return;

        if (value instanceof Boolean bool) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field(fieldName)
                            .value(bool)
                    )
            );
        } else if (value instanceof RangeDto rangeDto){
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field(fieldName)
                            .value(rangeDto.getValue1())
                    )
            );
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

    public void addRangeFilter(BoolQuery.Builder boolQuery, String fieldName, RangeDto value) {
        if (value == null || value.getValue1() == null || value.getOperator() == null)
            return;

        switch (value.getOperator()) {
            case BETWEEN:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gte(Double.parseDouble(value.getValue1())).lte(Double.parseDouble(value.getValue2())))));
                break;
            case GT:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gt(Double.parseDouble(value.getValue1())))));
                break;
            case LT:
                boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).lt(Double.parseDouble(value.getValue1())))));
                break;
            case GTE: boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).gte(Double.parseDouble(value.getValue1())))));
                break;
            case LTE: boolQuery.filter(f -> f.range(r -> r.number(n -> n.field(fieldName).lte(Double.parseDouble(value.getValue1())))));
                break;


        }


    }

    public void addDateRangeFilter(BoolQuery.Builder boolQuery, String fieldName, RangeDto value) {
        if (value == null || value.getValue1() == null || value.getOperator() == null)
            return;

        switch (value.getOperator()) {
            case BETWEEN:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gte(value.getValue1()).lte(value.getValue2()))));
                break;
            case GT:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gt(value.getValue1()))));
                break;
            case LT:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).lt((value.getValue1())))));
                break;
            case GTE:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).gte((value.getValue1())))));
                break;
            case LTE:
                boolQuery.filter(f -> f.range(r -> r.date(n -> n.field(fieldName).lte((value.getValue1())))));
                break;
        }
    }

    public void handleListField(BoolQuery.Builder boolQuery, String fieldName, List<String> values) {
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

    public void addMatchFilter(BoolQuery.Builder boolQuery, String fieldName, String value) {
        if (value == null || value.isEmpty())
            return;

        boolQuery.filter(f -> f
                .match(t -> t
                        .field(fieldName)
                        .query(value)
                )
        );
    }

}
