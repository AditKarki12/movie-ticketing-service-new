package aditkarki.movieticketingservicenew.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TheaterUpdatedEvent {
    private final Long theaterId;
    private final String theaterName;
}