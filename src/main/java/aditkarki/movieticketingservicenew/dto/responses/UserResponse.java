package aditkarki.movieticketingservicenew.dto.responses;

import aditkarki.movieticketingservicenew.enums.CustomRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private CustomRole role;
}
