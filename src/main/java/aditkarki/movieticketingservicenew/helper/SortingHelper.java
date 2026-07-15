package aditkarki.movieticketingservicenew.helper;

import aditkarki.movieticketingservicenew.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.exception.InvalidSortByException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import org.springframework.stereotype.Component;

@Component
public class SortingHelper {

    public static String validSortBy(String sortBy) {
        if(ElasticSearchConstants.MATCH_FIELDS.contains(sortBy)) {
            throw new InvalidSortByException("Invalid sort by field");
        }
        return sortBy;
    }

    public static SortOrder validSortOrder(CustomSorting sortOrder) {
        return switch (sortOrder) {
            case ASC -> SortOrder.Asc;
            case DESC -> SortOrder.Desc;
            default -> throw new InvalidSortByException("Invalid sort order option");
        };
    }

}