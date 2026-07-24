package aditkarki.movieticketingservicenew.dto.responses;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import lombok.Data;

import java.util.List;

@Data
public class BookingStatusResponse {
    private List<BookingStatus> status;
    private Long bookingCount;
    private Double averageTotalPrice;
}