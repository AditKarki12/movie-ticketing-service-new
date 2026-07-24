package aditkarki.movieticketingservicenew.auth;

import aditkarki.movieticketingservicenew.dto.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Registration
    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authService.registerUser(signUpRequest), HttpStatus.CREATED);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.authentication(loginRequest), HttpStatus.ACCEPTED);
    }
 }


