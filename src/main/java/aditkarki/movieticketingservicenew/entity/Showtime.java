package aditkarki.movieticketingservicenew.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name="showtimes")
@Data
@NoArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate localDate;
    private LocalTime localTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal ticketPrice;

    @ManyToOne
    @JoinColumn(name = "theaterId")
    private Theater theater;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
