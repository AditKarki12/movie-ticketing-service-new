package aditkarki.movieticketingservicenew.dto.responses;

import aditkarki.movieticketingservicenew.enums.SeatType;
import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowtimeSeatResponse {
    private Long showtimeSeatId;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
    private ShowtimeSeatStatus status;
    private LocalDateTime holdExpiresAt;
}
