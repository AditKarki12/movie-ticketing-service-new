package aditkarki.movieticketingservicenew.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name="showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;
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

    @OneToMany(mappedBy = "showtime")
    private List<Booking> bookings;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
