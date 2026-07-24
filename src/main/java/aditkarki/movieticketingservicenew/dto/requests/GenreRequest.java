package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

import java.util.List;

@Data
public class GenreRequest {
    private List<String> genre;
    private NumericRangeDto movieCount;
    private NumericRangeDto averageRating;
    private NumericRangeDto averageDuration;
}
