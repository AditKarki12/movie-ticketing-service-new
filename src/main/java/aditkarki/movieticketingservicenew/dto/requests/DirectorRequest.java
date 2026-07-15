package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.RangeDto;
import lombok.Data;

import java.util.List;

@Data
public class DirectorRequest {
    private List<String> director;
    private RangeDto movieCount;
    private RangeDto averageRating;
    private RangeDto averageDuration;
}
