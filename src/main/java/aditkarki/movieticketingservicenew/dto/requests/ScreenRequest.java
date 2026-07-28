package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

@Data
public class ScreenRequest {
    private String screenName;
    private Long theaterId;
}
