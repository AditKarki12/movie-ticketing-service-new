package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.document.UserDocument;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.UserResponse;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    UserResponse toResponse(UserDocument userDocument);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    User toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);

    UserDocument toDocument(User user);

    @Mapping(target = "userId", ignore = true)
    UserDocument toDocument(UserRequest request);
}
