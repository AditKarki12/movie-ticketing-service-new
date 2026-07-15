package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.auth.SignUpRequest;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthManager {
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
