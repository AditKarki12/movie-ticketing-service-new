package aditkarki.movieticketingservicenew.auth;

import aditkarki.movieticketingservicenew.dto.responses.UserResponse;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.UserMapper;
import aditkarki.movieticketingservicenew.repository.UserRepository;
import aditkarki.movieticketingservicenew.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import aditkarki.movieticketingservicenew.enums.CustomRole;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserAuthManager userAuthManager;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(SignUpRequest signUpRequest) {
        if(userAuthManager.findByEmail(signUpRequest.getUserEmail()).isPresent()) {
            throw new DuplicateResourceException("User already exists");
        }

        User user = new User();
        user.setUserEmail(signUpRequest.getUserEmail());
        user.setUserPassword(passwordEncoder.encode(signUpRequest.getUserPassword()));
        user.setUserFirstName(signUpRequest.getUserFirstName());
        user.setUserLastName(signUpRequest.getUserLastName());
        user.setRole(CustomRole.USER);

        return userMapper.toResponse(userAuthManager.save(user));
    }

    public JwtResponse authentication(LoginRequest loginRequest){

        // Checks against the database to see if wee put in the right stufff
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserEmail(), loginRequest.getUserPassword()));

        // Basically just sets who is currently logged in for access
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateToken(loginRequest.getUserEmail());

        // Lets us see what is stored in UsernamePasswordAuthenticationToken
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        String role = userDetails.getAuthorities().toString();

        return new JwtResponse(jwtToken, email, role);
    }
}
