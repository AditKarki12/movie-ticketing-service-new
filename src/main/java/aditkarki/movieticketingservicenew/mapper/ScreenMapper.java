package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.ScreenRequest;
import aditkarki.movieticketingservicenew.dto.responses.ScreenResponse;
import aditkarki.movieticketingservicenew.entity.Screen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ScreenMapper {

    @Mapping(target = "theaterId", source = "theater.theaterId")
    ScreenResponse toResponse(Screen screen);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theater", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Screen toEntity(ScreenRequest screenRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theater", ignore = true)
    @Mapping(target = "seats", ignore = true)
    void updateEntityfromRequest(ScreenRequest screenRequest, @MappingTarget Screen screen);
}
