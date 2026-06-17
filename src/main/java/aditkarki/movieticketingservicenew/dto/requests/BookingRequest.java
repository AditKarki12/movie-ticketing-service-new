package aditkarki.movieticketingservicenew.dto.requests;


import lombok.Data;

@Data
public class BookingRequest {
    private Long showtimeId;
    private Integer seatCount;
    private Long userId;
}
