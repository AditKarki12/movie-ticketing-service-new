package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.UserDocument;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.UserMapper;
import aditkarki.movieticketingservicenew.repository.UserRepository;
import aditkarki.movieticketingservicenew.event.UserUpdatedEvent;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final ApplicationEventPublisher eventPublisher;

    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        syncToES(savedUser);
        eventPublisher.publishEvent(new UserUpdatedEvent(savedUser.getUserId(), savedUser.getUserEmail()));

        return savedUser;
    }

    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findUserByEmail(String email){
        return userRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
        deleteFromES(userId);
    }

    public boolean existsByUserEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    private void syncToES(User user){
        UserDocument document = userMapper.toDocument(user);
        document.setUserId(String.valueOf(user.getUserId()));

        try{
            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.USER_INDEX)
                    .id(String.valueOf(user.getUserId()))
                    .document(document)
            );

        } catch (IOException e){
            log.error(e.getMessage());
        }

    }

    private void deleteFromES(Long id){
        try{
            elasticsearchClient.delete(d -> d
                    .index(ElasticSearchConstants.USER_INDEX)
                    .id(String.valueOf(id))
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
