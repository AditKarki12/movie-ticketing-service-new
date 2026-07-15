package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class DirectorResponse {
    private List<String> director;
    private Long movieCount;
    private Double averageMovieRating;
    private Double averageMovieDuration;
}
