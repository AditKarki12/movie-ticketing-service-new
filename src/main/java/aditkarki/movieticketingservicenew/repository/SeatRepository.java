package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.entity.Screen;
import aditkarki.movieticketingservicenew.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreen(Screen screen);
    List<Seat> findByScreenId(Long screenId);
}
