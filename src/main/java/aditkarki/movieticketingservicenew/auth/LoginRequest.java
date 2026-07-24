package aditkarki.movieticketingservicenew.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String userEmail;
    private String userPassword;
}
