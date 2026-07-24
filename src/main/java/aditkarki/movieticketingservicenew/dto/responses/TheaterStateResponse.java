package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class TheaterStateResponse {
    private List<String> theaterState;
    private Long theaterCount;
    private Double averageScreensTotal;
}