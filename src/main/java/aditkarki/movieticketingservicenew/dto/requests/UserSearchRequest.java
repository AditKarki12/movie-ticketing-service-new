package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.enums.CustomRole;
import lombok.Data;

@Data
public class UserSearchRequest {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private CustomRole role;
}
