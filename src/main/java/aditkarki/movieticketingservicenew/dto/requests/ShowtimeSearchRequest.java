package aditkarki.movieticketingservicenew.dto.requests;

import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import lombok.Data;

@Data
public class ShowtimeSearchRequest {
    private NumericRangeDto showtimeId;
    private String movieTitle;
    private String theaterName;
    private DateRangeDto localDate;
    private DateRangeDto localTime;
    private NumericRangeDto availableSeats;
    private NumericRangeDto totalSeats;
    private NumericRangeDto ticketPrice;
}
