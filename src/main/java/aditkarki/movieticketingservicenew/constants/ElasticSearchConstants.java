package aditkarki.movieticketingservicenew.constants;

import java.util.Map;
import java.util.Set;

public final class ElasticSearchConstants {

    private ElasticSearchConstants() {} // prevent instantiation

    public static final String MOVIES_INDEX = "movies";

    public static final String BOOKINGS_INDEX = "bookings";

    public static final String USER_INDEX = "users";

    public static final String SHOWTIMES_INDEX = "showtimes";

    public static final String THEATERS_INDEX = "theaters";

    public static final Set<String> MATCH_FIELDS =
            Set.of("title", "description");

    public static final Map<String, String> MOVIE_AGGREGATION_FIELDS = Map.of(
            "averageRating", "rating",
            "averageDuration", "duration"
    );

    public static final Map<String, String> THEATER_AGGREGATION_FIELDS = Map.of(
            "averageScreensTotal", "screensTotal"
    );

}
