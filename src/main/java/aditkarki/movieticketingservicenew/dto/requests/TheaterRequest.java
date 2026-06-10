package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

@Data
public class TheaterRequest {
    private String theaterName;
    private String theaterAddress;
    private String theaterCity;
    private String theaterState;
    private Integer screensTotal;
}
