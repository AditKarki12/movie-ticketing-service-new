package aditkarki.movieticketingservicenew.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> genres;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> language;

    private Integer duration;
    private String description;
    private String director;
    private Double rating;
    private LocalDate releaseDate;
    private Boolean isActive;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Showtime> showtimes;
}
