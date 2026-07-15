package aditkarki.movieticketingservicenew.auth;

import aditkarki.movieticketingservicenew.CustomRole;
import lombok.Data;

@Data
public class LoginRequest {
    private String userEmail;
    private String userPassword;
    private CustomRole customRole;
}
