package aditkarki.movieticketingservicenew.listener;

import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.event.MovieUpdatedEvent;
import aditkarki.movieticketingservicenew.event.TheaterUpdatedEvent;
import aditkarki.movieticketingservicenew.manager.ShowtimeManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShowtimeSyncListener {
    private final ShowtimeManager showtimeManager;

    @EventListener
    public void onMovieUpdatedEvent(MovieUpdatedEvent event) {
        List<Showtime> affectedShowtimes = showtimeManager.findShowtimesByMovieId(event.getMovieId());
        showtimeManager.resyncToES(affectedShowtimes);
    }

    @EventListener
    public void onTheaterUpdatedEvent(TheaterUpdatedEvent event) {
        List<Showtime> affectedShowtimes = showtimeManager.findShowtimeByTheaterId(event.getTheaterId());
        showtimeManager.resyncToES(affectedShowtimes);
    }
}
