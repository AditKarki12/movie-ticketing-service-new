package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieSearchRequest {
    private String title;
    private List<String> genres;
    private List<String> language;
    private List<String> tags;
    private String director;
    private Integer minDuration;
    private Integer maxDuration;
    private Double minRating;
    private Double maxRating;
    private LocalDate startReleaseDate;
    private LocalDate endReleaseDate;
    private Boolean isActive;
}