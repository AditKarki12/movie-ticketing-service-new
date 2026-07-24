package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

import java.util.List;

@Data
public class TheaterCityRequest {
    private List<String> theaterCity;
    private NumericRangeDto theaterCount;
    private NumericRangeDto averageScreensTotal;
}
