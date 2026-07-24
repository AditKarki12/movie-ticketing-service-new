package aditkarki.movieticketingservicenew.listener;

import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.event.MovieUpdatedEvent;
import aditkarki.movieticketingservicenew.event.TheaterUpdatedEvent;
import aditkarki.movieticketingservicenew.event.UserUpdatedEvent;
import aditkarki.movieticketingservicenew.manager.BookingManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingSyncListener {
    private final BookingManager bookingManager;

    @EventListener
    public void onUserUpdatedEvent(UserUpdatedEvent event) {
        List<Booking> affectedBookings = bookingManager.findBookingsByUserId(event.getUserId());
        bookingManager.resyncToES(affectedBookings);
    }

    @EventListener
    public void onMovieUpdatedEvent(MovieUpdatedEvent event) {
        List<Booking> affectedBookings = bookingManager.findBookingsByMovieId(event.getMovieId());
        bookingManager.resyncToES(affectedBookings);
    }

    @EventListener
    public void onTheaterUpdatedEvent(TheaterUpdatedEvent event) {
        List<Booking> affectedBookings = bookingManager.findBookingsByTheaterId(event.getTheaterId());
        bookingManager.resyncToES(affectedBookings);
    }
}