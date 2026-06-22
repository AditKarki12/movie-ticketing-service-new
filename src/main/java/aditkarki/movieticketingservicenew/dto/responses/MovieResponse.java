package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieResponse {
    private String id;
    private String heading;
    private List<String> genres;
    private List<String> language;
    private Integer duration;
    private String description;
    private String director;
    private Double rating;
    private LocalDate releaseDate;
    private Boolean isActive;
    private List<String> tags;
}
