package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class GenreResponse {
    private List<String> genre;
    private Long movieCount;
    private Double averageMovieRating;
    private Double averageMovieDuration;
}