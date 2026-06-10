package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
