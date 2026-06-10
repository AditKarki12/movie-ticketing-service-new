package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.TheaterRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.TheaterResponse;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TheaterMapper {

    TheaterResponse toResponse(Theater theater);

    @Mapping(target = "theaterId", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    Theater toEntity(TheaterRequest request);

    void updateEntityFromRequest(TheaterRequest request, @MappingTarget Theater theater);
}
