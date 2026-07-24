package aditkarki.movieticketingservicenew.auth;

import lombok.Data;

@Data
public class SignUpRequest {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
}
