package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieRequest {
    private String title;
    private List<String> genres;
    private List<String> language;
    private Integer duration;
    private String description;
    private String director;
    private Double rating;
    private LocalDate releaseDate;
    private Boolean isActive;
    private String imageUrl;
    private String videoUrl;
    private List<String> tags;
}
