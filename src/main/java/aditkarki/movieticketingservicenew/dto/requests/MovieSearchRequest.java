package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.RangeDto;
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


    private RangeDto duration;
    private RangeDto rating;


    private RangeDto releaseDate;

    private Boolean isActive;
}