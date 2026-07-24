package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class LanguageResponse {
    private List<String> language;
    private Long movieCount;
    private Double averageMovieRating;
    private Double averageMovieDuration;
}