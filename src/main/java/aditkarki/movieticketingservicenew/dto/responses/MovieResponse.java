package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieResponse {
    private String id;
    private String heading;
    private String genres;
    private String language;
    private Integer duration;
    private String description;
    private String director;
    private Double rating;
    private LocalDate releaseDate;
    private Boolean isActive;
    private String tags;
}
