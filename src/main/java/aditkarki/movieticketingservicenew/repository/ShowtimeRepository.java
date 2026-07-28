package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Showtime s SET s.availableSeats = s.availableSeats - :seatCount WHERE s.showtimeId = :id AND s.availableSeats >= :seatCount")
    int reserveSeats(@Param("id") Long id, @Param("seatCount") int seatCount);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Showtime s SET s.availableSeats = s.availableSeats + :seatCount WHERE s.showtimeId = :id AND s.availableSeats + :seatCount <= s.totalSeats")
    int releaseSeats(@Param("id") Long id, @Param("seatCount") int seatCount);

    BigDecimal findTicketPriceByShowtimeId(Long showtimeId);

    boolean existsByTheater_TheaterId(Long theaterId);

    boolean existsByScreenId(Long screenId);

    List<Showtime> findShowtimesByTheater_TheaterId(Long theaterId);
    List<Showtime> findShowtimesByMovieId(Long movieId);
}
