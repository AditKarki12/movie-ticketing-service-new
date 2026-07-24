package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

import java.util.List;

@Data
public class TheaterStateRequest {
    private List<String> theaterState;
    private NumericRangeDto theaterCount;
    private NumericRangeDto averageScreensTotal;
}
