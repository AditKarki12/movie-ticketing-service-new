package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

@Data
public class BookingSearchRequest {
    private NumericRangeDto bookingId;
    private String movieTitle;
    private String theaterName;
    private NumericRangeDto seatCount;
    private NumericRangeDto totalPrice;
    private DateRangeDto bookingTime;
    private BookingStatus status;
    private String userEmail;
    private NumericRangeDto showtimeId;
}
