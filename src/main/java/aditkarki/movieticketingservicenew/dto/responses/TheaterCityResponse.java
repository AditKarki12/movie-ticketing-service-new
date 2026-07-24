package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class TheaterCityResponse {
    private List<String> theaterCity;
    private Long theaterCount;
    private Double averageScreensTotal;
}