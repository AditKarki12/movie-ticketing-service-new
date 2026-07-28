package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

import java.util.List;

@Data
public class ShowtimeSeatSelectionRequest {
    private Long showtimeId;
    private List<Long> seatIds;
}
