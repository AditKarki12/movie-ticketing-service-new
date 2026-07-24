package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.responses.BookingStatusResponse;
import aditkarki.movieticketingservicenew.enums.BookingStatus;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingStatusMapper {

    default BookingStatusResponse toBookingStatusResponse(StringTermsBucket bucket) {
        BookingStatusResponse response = new BookingStatusResponse();
        response.setStatus(List.of(BookingStatus.valueOf(bucket.key().stringValue())));
        response.setBookingCount(bucket.docCount());
        response.setAverageTotalPrice(round(bucket.aggregations().get("averageTotalPrice").avg().value()));
        return response;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}