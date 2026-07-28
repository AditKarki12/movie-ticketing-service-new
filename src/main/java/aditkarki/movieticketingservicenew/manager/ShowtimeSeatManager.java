package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.entity.ShowtimeSeat;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.repository.ShowtimeSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowtimeSeatManager {
    private final ShowtimeSeatRepository showtimeSeatRepository;

    public List<ShowtimeSeat> saveAll(List<ShowtimeSeat> showtimeSeats) {
        return showtimeSeatRepository.saveAll(showtimeSeats);
    }

    public List<ShowtimeSeat> findByShowtimeId(Long showtimeId) {
        return showtimeSeatRepository.findByShowtime_ShowtimeId(showtimeId);
    }

    public ShowtimeSeat findByShowtimeIdAndSeatId(Long showtimeId, Long seatId) {
        return showtimeSeatRepository.findByShowtime_ShowtimeIdAndSeatId(showtimeId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("ShowtimeSeat"));
    }

    public long countByShowtimeIdAndStatus(Long showtimeId, ShowtimeSeatStatus status) {
        return showtimeSeatRepository.countByShowtime_ShowtimeIdAndStatus(showtimeId, status);
    }

    public List<ShowtimeSeat> findExpiredHolds(LocalDateTime now) {
        return showtimeSeatRepository.findByStatusAndHoldExpiresAtBefore(ShowtimeSeatStatus.HELD, now);
    }

    public int conditionalUpdateStatus(Long id, ShowtimeSeatStatus expectedStatus, ShowtimeSeatStatus newStatus, LocalDateTime expiry, User heldBy) {
        return showtimeSeatRepository.conditionalUpdateStatus(id, expectedStatus, newStatus, expiry, heldBy);
    }

    public List<ShowtimeSeat> findByBookingId(Long bookingId) {
        return showtimeSeatRepository.findByBooking_BookingId(bookingId);
    }

    public int bookSeatForBooking(Long showtimeSeatId, Booking booking) {
        return showtimeSeatRepository.conditionalBookForBooking(showtimeSeatId, ShowtimeSeatStatus.HELD, ShowtimeSeatStatus.BOOKED, booking);
    }

    public void releaseBookingSeats(Long bookingId) {
        showtimeSeatRepository.releaseByBooking(bookingId, ShowtimeSeatStatus.AVAILABLE);
    }

    public void deleteByShowtimeId(Long showtimeId) {
        showtimeSeatRepository.deleteByShowtime_ShowtimeId(showtimeId);
    }
}