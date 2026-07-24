package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.document.BookingDocument;
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
    @Mapping(target = "userEmail", source = "user.userEmail")
    @Mapping(target = "bookingStatus", source = "status")
    @Mapping(target = "showtimeId", source = "showtime.showtimeId")
    BookingResponse toResponse(Booking booking);

    @Mapping(target = "bookingStatus", source = "status")
    BookingResponse toResponse(BookingDocument bookingDocument);

    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "bookingTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "showtime", ignore = true)
    Booking toEntity(BookingRequest request);

    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userEmail", source = "user.userEmail")
    @Mapping(target = "showtimeId", source = "showtime.showtimeId")
    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "theaterName", source = "showtime.theater.theaterName")
    BookingDocument toDocument(Booking booking);

    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "bookingTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "movieTitle", ignore = true)
    @Mapping(target = "theaterName", ignore = true)
    BookingDocument toDocument(BookingRequest request);
}
