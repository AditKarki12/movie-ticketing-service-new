package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.responses.ShowtimeSeatResponse;
import aditkarki.movieticketingservicenew.entity.ShowtimeSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowtimeSeatMapper {

    @Mapping(target = "showtimeSeatId", source = "id")
    @Mapping(target = "rowLabel", source = "seat.rowLabel")
    @Mapping(target = "seatNumber", source = "seat.seatNumber")
    @Mapping(target = "seatType", source = "seat.seatType")
    ShowtimeSeatResponse toResponse(ShowtimeSeat showtimeSeat);
}
