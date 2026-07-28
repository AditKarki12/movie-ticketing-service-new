package aditkarki.movieticketingservicenew.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne
    @JoinColumn(name = "screenId")
    private Screen screen;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Booking> bookings;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ShowtimeSeat> showtimeSeats;
}
