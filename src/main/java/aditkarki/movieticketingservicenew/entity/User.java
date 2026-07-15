package aditkarki.movieticketingservicenew.entity;

import aditkarki.movieticketingservicenew.CustomRole;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;

    @Enumerated(EnumType.STRING)
    private CustomRole role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}
