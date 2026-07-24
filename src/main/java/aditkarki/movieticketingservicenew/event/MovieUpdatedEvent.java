package aditkarki.movieticketingservicenew.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class MovieUpdatedEvent {
    private final Long movieId;
    private final String movieTitle;
}