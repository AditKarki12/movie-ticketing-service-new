package aditkarki.movieticketingservicenew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="theater")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long theaterId;
    private String theaterName;
    private String theaterAddress;
    private String theaterCity;
    private String theaterState;
    private Integer screensTotal;

    @OneToMany(mappedBy = "theater")
    private List<Showtime> showtimes;
}
