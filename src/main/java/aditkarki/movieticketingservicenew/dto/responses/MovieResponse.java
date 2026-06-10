package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieResponse {
    private String id;
    private String title;
    private String genre;
    private String language;
    private Integer duration;
    private String description;
    private String director;
    private Double rating;
}
