package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

@Data
public class TheaterSearchRequest {
    private NumericRangeDto theaterId;
    private String theaterName;
    private String theaterAddress;
    private String theaterCity;
    private String theaterState;
    private NumericRangeDto screensTotal;
}
