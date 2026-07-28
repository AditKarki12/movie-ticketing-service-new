package aditkarki.movieticketingservicenew.entity;

import aditkarki.movieticketingservicenew.enums.SeatType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"screen_id", "rowLabel", "seatNumber"}))
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    private String rowLabel;
    private Long seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;
}
