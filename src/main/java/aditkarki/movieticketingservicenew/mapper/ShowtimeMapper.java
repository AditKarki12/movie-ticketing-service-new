package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "theaterId", source = "theater.theaterId")
    ShowtimeResponse toResponse(Showtime showtime);

    @Mapping(target = "showtimeId", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "theater", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Showtime toEntity(ShowtimeRequest request);

    void updateEntityFromRequest(ShowtimeRequest request, @MappingTarget Showtime showtime);
}
