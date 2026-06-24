package aditkarki.movieticketingservicenew.constants;

import java.util.Set;

public final class ElasticSearchConstants {

    private ElasticSearchConstants() {} // prevent instantiation

    public static final String MOVIES_INDEX = "movies";

    public static final Set<String> MATCH_FIELDS =
            Set.of("title", "description");
}
