package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheaterResponse {
    private Long theaterId;
    private String theaterName;
    private String theaterAddress;
    private String theaterCity;
    private String theaterState;
    private Integer screensTotal;
}
