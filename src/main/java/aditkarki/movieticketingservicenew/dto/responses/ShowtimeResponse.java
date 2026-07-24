package aditkarki.movieticketingservicenew.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ShowtimeResponse {
    private Long showtimeId;
    private String movieTitle;
    private String theaterName;
    private LocalDate localDate;
    private LocalTime localTime;
    private Integer availableSeats;
    private Integer totalSeats;
    private BigDecimal ticketPrice;
}
