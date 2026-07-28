package aditkarki.movieticketingservicenew.repository;


import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.ShowtimeSeat;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, Long> {
    List<ShowtimeSeat> findByShowtime(Showtime showtime);
    List<ShowtimeSeat> findByShowtime_ShowtimeId(Long showtimeId);

    Optional<ShowtimeSeat> findByShowtime_ShowtimeIdAndSeatId(Long showtimeId, Long seatId);

    long countByShowtime_ShowtimeIdAndStatus(Long showtimeId, ShowtimeSeatStatus status);

    List<ShowtimeSeat> findByStatusAndHoldExpiresAtBefore(
            ShowtimeSeatStatus status, LocalDateTime now);

    List<ShowtimeSeat> findByBooking_BookingId(Long bookingId);

    @Modifying
    @Transactional
    void deleteByShowtime_ShowtimeId(Long showtimeId);

    @Modifying
    @Transactional
    @Query("UPDATE ShowtimeSeat showtimeSeat SET showtimeSeat.status = :newStatus, showtimeSeat.holdExpiresAt = :expiry, showtimeSeat.heldBy = :heldBy " +
    "WHERE showtimeSeat.id = :id AND showtimeSeat.status = :expectedStatus")
    int conditionalUpdateStatus(@Param("id") Long id, @Param("expectedStatus") ShowtimeSeatStatus expectedStatus, @Param("newStatus") ShowtimeSeatStatus newStatus, @Param("expiry") LocalDateTime expiry, @Param("heldBy") User heldBy);

    @Modifying
    @Transactional
    @Query("UPDATE ShowtimeSeat showtimeSeat SET showtimeSeat.status = :newStatus, " +
    "showtimeSeat.booking = :booking, showtimeSeat.holdExpiresAt = null " +
    "WHERE showtimeSeat.id = :id AND showtimeSeat.status = :expectedStatus")
    int conditionalBookForBooking(@Param("id") Long id, @Param("expectedStatus") ShowtimeSeatStatus expectedStatus, @Param("newStatus") ShowtimeSeatStatus newStatus, @Param("booking") Booking booking);

    @Modifying
    @Transactional
    @Query("UPDATE ShowtimeSeat showtimeSeat SET showtimeSeat.status = :newStatus, showtimeSeat.booking = null, showtimeSeat.holdExpiresAt = null, showtimeSeat.heldBy = null " +
    "WHERE showtimeSeat.booking.bookingId = :bookingId")
    int releaseByBooking(@Param("bookingId") Long bookingId, @Param("newStatus") ShowtimeSeatStatus newStatus);
}
