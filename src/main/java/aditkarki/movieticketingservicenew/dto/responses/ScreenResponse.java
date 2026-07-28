package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Data;

@Data
public class ScreenResponse {
    private Long id;
    private String screenName;
    private Long theaterId;
}