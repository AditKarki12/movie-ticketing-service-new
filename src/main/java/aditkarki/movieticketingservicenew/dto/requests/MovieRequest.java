package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieRequest {
    private String title;
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
