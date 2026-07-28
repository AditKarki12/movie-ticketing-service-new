package aditkarki.movieticketingservicenew.entity;

import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "showtimeSeats", uniqueConstraints = @UniqueConstraint(columnNames = {"showtimeId", "seat_id"}))
@NoArgsConstructor
public class ShowtimeSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "showtimeId")
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private ShowtimeSeatStatus status;

    private LocalDateTime holdExpiresAt;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "heldByUserId")
    private User heldBy;

    @Version
    private Long version;
}
