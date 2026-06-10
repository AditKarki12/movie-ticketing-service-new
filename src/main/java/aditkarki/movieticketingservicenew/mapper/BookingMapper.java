package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.BookingRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.BookingResponse;
import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "theaterName", source = "showtime.theater.theaterName")
    BookingResponse toResponse(Booking booking);

    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "bookingTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "showtime", ignore = true)
    Booking toEntity(BookingRequest request);

    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);
}
