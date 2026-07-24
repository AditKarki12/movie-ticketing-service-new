package aditkarki.movieticketingservicenew.dto.responses;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long bookingId;
    private String movieTitle;
    private String theaterName;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private LocalDateTime bookingTime;
    private BookingStatus bookingStatus;
    private String userEmail;
    private Long showtimeId;
}
