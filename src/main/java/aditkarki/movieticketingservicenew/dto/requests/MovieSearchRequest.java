package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
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
    private NumericRangeDto duration;
    private NumericRangeDto rating;
    private DateRangeDto releaseDate;
    private Boolean isActive;
}