package aditkarki.movieticketingservicenew.constants;

import java.util.Map;
import java.util.Set;

public final class ElasticSearchConstants {

    private ElasticSearchConstants() {} // prevent instantiation

    public static final String MOVIES_INDEX = "movies";

    public static final Set<String> MATCH_FIELDS =
            Set.of("title", "description");

    public static final Map<String, String> DIRECTOR_AGGREGATION_FIELDS = Map.of(
            "averageRating", "rating",
            "averageDuration", "duration"
    );
}
