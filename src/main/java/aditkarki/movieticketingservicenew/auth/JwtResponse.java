package aditkarki.movieticketingservicenew.auth;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String roles;

    public JwtResponse(String accessToken, String email, String roles) {
        this.token = accessToken;
        this.email = email;
        this.roles = roles;
    }
}
