package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.enums.CustomRole;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.UserDocument;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.dto.responses.UserResponse;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SortingHelper;
import aditkarki.movieticketingservicenew.manager.UserManager;
import aditkarki.movieticketingservicenew.mapper.UserMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserManager userManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final QueryFilterHelper queryFilterHelper;
    private final ElasticsearchClient elasticsearchClient;

    public UserResponse registerUser(UserRequest userRequest) {
        if (userManager.existsByUserEmail(userRequest.getUserEmail())) {
            throw new DuplicateResourceException(userRequest.getUserEmail());
        }
        userRequest.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
        User user = userMapper.toEntity(userRequest);
        user.setRole(CustomRole.USER);
        User savedUser = userManager.saveUser(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {
        User user = userManager.findUserById(id);
        return userMapper.toResponse(user);
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        User user = userManager.findUserByEmail(authentication.getName());
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userManager.findAllUsers().stream().map(userMapper::toResponse).toList();
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userManager.findUserById(id);
        if (userRequest.getUserPassword() != null) {
            userRequest.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
        }
        userMapper.updateEntityFromRequest(userRequest, existingUser);
        User updatedUser = userManager.saveUser(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        userManager.deleteUser(id);
    }

    public TableResponse getUsers(UserSearchRequest userSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting){
        // Query Builder
        BoolQuery.Builder boolquery = new BoolQuery.Builder();
        userQueryFiltering(userSearchRequest, boolquery);

        // Pagination
        Page pagination = PageUtils.pagination(pageNumber, size);

        // Sort
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        String sortingField = SortingHelper.validSortBy(sortBy);

        // Request Buildr
        SearchRequest request = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.USER_INDEX)
                .query(boolquery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true)));

        log.info("User Search request: {}", request);

        // Response
        SearchResponse<UserDocument> response;
        try{
            response = elasticsearchClient.search(request, UserDocument.class);
        } catch (IOException e){
            throw new ReflectionAccessException(e.getMessage());
        }

        LinkedList<UserResponse> data = response.hits().hits()
                .stream()
                .map(Hit::source)
                .map(userMapper::toResponse)
                .collect(Collectors.toCollection(LinkedList::new));

        Page pageBuilder = PageUtils.pageBuilder(pagination.getPageNumber(), pagination.getSize(), response);

        return TableResponse.builder()
                .data(data)
                .page(pageBuilder)
                .build();
    }

    private void userQueryFiltering(UserSearchRequest userSearchRequest, BoolQuery.Builder boolquery) {
        for(Field field: userSearchRequest.getClass().getDeclaredFields()){
            field.setAccessible(true);
            String fieldName = field.getName();

            Object fieldValue;
            try{
                fieldValue = field.get(userSearchRequest);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new ReflectionAccessException(e.getMessage());
            }

            if(String.class.isAssignableFrom(field.getType())){
                String value = (String) fieldValue;

                if(value == null || value.isEmpty()){
                    continue;
                }
                queryFilterHelper.addTermsFilter(boolquery, fieldName, value);
            } else if(CustomRole.class.isAssignableFrom(field.getType())){
                CustomRole value = (CustomRole) fieldValue;
                if(value == null){
                    continue;
                }
                queryFilterHelper.addTermsFilter(boolquery, fieldName, value.toString());
            }
        }
    }
}
