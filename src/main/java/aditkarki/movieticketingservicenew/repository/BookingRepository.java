package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import aditkarki.movieticketingservicenew.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_UserId(Long userId);

    List<Booking> findByShowtime_Movie_Id(Long movieId);

    List<Booking> findByShowtime_Theater_TheaterId(Long theaterId);

    boolean existsByShowtime_ShowtimeIdAndStatusNot(Long showtimeId, BookingStatus status);
}
