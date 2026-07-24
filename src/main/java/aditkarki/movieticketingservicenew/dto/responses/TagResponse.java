package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class TagResponse {
    private List<String> tag;
    private Long movieCount;
    private Double averageMovieRating;
    private Double averageMovieDuration;
}