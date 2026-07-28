package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowtimeRequest {
    private Long movieId;
    private Long theaterId;
    private Long screenId;
    private LocalDate localDate;
    private LocalTime localTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal ticketPrice;
}
