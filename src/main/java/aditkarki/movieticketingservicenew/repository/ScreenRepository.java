package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.entity.Screen;
import aditkarki.movieticketingservicenew.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTheater_TheaterId(Long theaterId);
    boolean existsByTheaterAndScreenName(Theater theater, String screenName);
}
