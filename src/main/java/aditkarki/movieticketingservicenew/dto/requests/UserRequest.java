package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

@Data
public class UserRequest {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
}
