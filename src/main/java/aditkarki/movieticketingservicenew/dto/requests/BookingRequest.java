package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.entity.User;
import lombok.Data;

@Data
public class BookingRequest {
    private Long showtimeId;
    private Integer seatCount;
    private Long userId;
}
