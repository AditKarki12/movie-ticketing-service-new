package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.document.ShowtimeDocument;
import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "theaterName", source = "theater.theaterName")
    ShowtimeResponse toResponse(Showtime showtime);

    ShowtimeResponse toResponse(ShowtimeDocument showtimeDocument);

    @Mapping(target = "showtimeId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "theater", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Showtime toEntity(ShowtimeRequest request);

    @Mapping(target = "showtimeId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "theater", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ShowtimeRequest request, @MappingTarget Showtime showtime);

    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "theaterName", source = "theater.theaterName")
    ShowtimeDocument toDocument(Showtime showtime);

    @Mapping(target = "showtimeId", ignore = true)
    @Mapping(target = "movieTitle", ignore = true)
    @Mapping(target = "theaterName", ignore = true)
    ShowtimeDocument toDocument(ShowtimeRequest request);
}
