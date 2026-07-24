package aditkarki.movieticketingservicenew.config.seed;

import aditkarki.movieticketingservicenew.entity.*;
import aditkarki.movieticketingservicenew.enums.CustomRole;
import aditkarki.movieticketingservicenew.manager.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    private final MovieManager movieManager;
    private final TheaterManager theaterManager;
    private final UserManager userManager;
    private final ShowtimeManager showtimeManager;
    private final BookingManager bookingManager;
    private final PasswordEncoder passwordEncoder;

    List<String> directors = List.of("Christopher Nolan", "Steven Spielberg", "Martin Scorsese", "Quentin Tarantino", "James Cameron", "Ridley Scott", "Denis Villeneuve", "David Fincher", "Peter Jackson", "Tim Burton");
    List<String> tags = List.of("Action", "Adventure", "Award Winning", "Based on a True Story", "Blockbuster", "Classic", "Coming of Age", "Cult Classic", "Family Friendly", "Fantasy");
    List<String> languages = List.of("English", "Spanish", "French", "Japanese", "Hindi", "German");
    List<String> genres = List.of("Action", "Comedy", "Drama", "Sci-Fi", "Thriller", "Horror", "Fantasy", "Crime");

    List<String> titleWords = List.of("Shadow", "Last", "Dark", "Lost", "Silent", "Broken", "Crimson", "Golden", "Hidden", "Forgotten", "King", "Queen", "Empire", "Legacy", "Destiny", "Storm", "Fire", "Ice", "Moon", "Sun", "Night", "Dawn", "Twilight", "Dream", "Echo", "Whisper", "Horizon", "Journey", "Odyssey", "Rise", "Fall", "Revenge", "Redemption", "Escape", "Return", "Hunter", "Warrior", "Guardian", "Phantom", "Dragon", "Wolf", "Lion", "Phoenix", "Titan", "Infinity", "Chaos", "Secret", "Beyond", "Heart", "Soul");
    List<String> movieTitles = generateNames(100, titleWords);

    List<String> theaterWords = List.of("Grand", "Royal", "Majestic", "Regal", "Crown", "Imperial", "Star", "Galaxy", "Oasis", "Plaza", "Vista", "Summit", "Aurora", "Central", "Palace");
    List<String> theaterAddressesWords = List.of("Main", "Oak", "Maple", "Pine", "Cedar", "Elm", "Park", "Lake", "River", "Hill", "Sunset", "Highland", "Washington", "Lincoln", "Broadway");
    List<String> theaterCities = List.of("Springfield", "Riverton", "Fairview", "Brookside", "Oakwood", "Lakeside", "Hillcrest", "Georgetown", "Madison", "Kingston", "Franklin", "Arlington", "Greenville", "Ashland", "Clinton");
    List<String> theaterAddressesWords2 = List.of("St", "Ave", "Blvd", "Rd", "Dr");
    List<String> theaterState = List.of("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY");
    List<String> theaterAddresses = generateNames(50, theaterAddressesWords);
    List<String> theaterNames = generateNames(50, theaterWords);

    List<String> firstNames = List.of("James", "Emma", "Liam", "Olivia", "Noah", "Ava", "William", "Sophia", "Benjamin", "Isabella", "Lucas", "Mia", "Henry", "Charlotte", "Alexander", "Amelia", "Mason", "Harper", "Ethan", "Evelyn");
    List<String> lastNames = List.of("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Young", "Allen", "King");
    List<String> emails = emailGenerator(firstNames, lastNames, 50);

    @Override
    public void run(String... args) {
        if(movieManager.findAll().isEmpty()){
            log.info("Seeding Movies");
            List<Movie> movies = seedMovie();
            log.info("Movies successfully loaded");

            log.info("Seeding Theaters");
            List<Theater> theaters = seedTheater();
            log.info("Theaters successfully loaded");

            log.info("Seeding Users");
            List<User> users = seedUsers();
            log.info("Users successfully loaded");

            log.info("Seeding Showtimes");
            List<Showtime> showtimes = seedShowtimes(movies, theaters);
            log.info("Showtimes successfully loaded");

            log.info("Seeding Bookings");
            seedBookings(users, showtimes);
            log.info("Bookings successfully loaded");

        } else{
            log.info("Movies already loaded");
        }
    }

    private List<Movie> seedMovie() {
        Random random = new Random();
        List<Movie> saved = new ArrayList<>();

        for(int i = 0; i < movieTitles.size(); i++){
            Movie movie = new Movie();
            movie.setTitle(movieTitles.get(i));
            movie.setGenres(randomSample(genres, random.nextInt(1, 3)));
            movie.setLanguage(randomSample(languages, random.nextInt(1, 2)));
            movie.setDuration(random.nextInt(100, 180));
            movie.setDescription("Movie about: " + movie.getTitle().toLowerCase());
            movie.setDirector(directors.get(i % directors.size()));
            movie.setRating(random.nextDouble(1, 5));
            movie.setReleaseDate(LocalDate.of(2000, 1, 1).plusDays(random.nextInt(0, 9000)));
            movie.setIsActive(random.nextBoolean());
            movie.setTags(randomSample(tags, random.nextInt(1, 3)));

            try {
                saved.add(movieManager.saveMovie(movie));
            } catch (Exception e) {
                log.error("Failed to seed movie {}: {}", movieTitles.get(i), e.getMessage());
            }
        }
        return saved;
    }

    public List<Theater> seedTheater() {
        Random random = new Random();
        List<Theater> saved = new ArrayList<>();

        for(int i = 0; i < theaterNames.size(); i++){
            Theater theater = new Theater();
            theater.setTheaterName(theaterNames.get(i));
            theater.setTheaterAddress(random.nextInt(10, 999) + " " + theaterAddresses.get(i) + " " + theaterAddressesWords2.get(i % theaterAddressesWords2.size()));
            theater.setTheaterCity(theaterCities.get(i % theaterCities.size()));
            theater.setTheaterState(theaterState.get(i % theaterState.size()));
            theater.setScreensTotal(random.nextInt(8, 15));

            try {
                saved.add(theaterManager.saveTheater(theater));
            } catch (Exception e) {
                log.error("Failed to seed theater {}: {}", theaterNames.get(i), e.getMessage());
            }
        }
        return saved;
    }

    private List<User> seedUsers() {
        Random random = new Random();

        List<User> saved = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String lastName = lastNames.get(random.nextInt(lastNames.size()));

            User user = new User();
            user.setUserFirstName(firstName);
            user.setUserLastName(lastName);
            user.setUserEmail(emails.get(i));
            user.setUserPassword(passwordEncoder.encode("Password123!"));
            user.setRole(random.nextInt(1, 100) == 1 ? CustomRole.ADMIN : CustomRole.USER);

            try {
                saved.add(userManager.saveUser(user));
            } catch (Exception e) {
                log.error("Failed to seed user {}: {}", emails.get(i), e.getMessage());
            }
        }
        return saved;
    }

    private List<Showtime> seedShowtimes(List<Movie> movies, List<Theater> theaters) {
        Random random = new Random();
        List<Showtime> saved = new ArrayList<>();

        LocalTime[] showtimeTimes = {
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                LocalTime.of(16, 0),
                LocalTime.of(19, 0),
                LocalTime.of(21, 30)
        };

        for (int i = 0; i < 200; i++) {
            Showtime showtime = new Showtime();
            showtime.setMovie(movies.get(random.nextInt(movies.size())));
            showtime.setTheater(theaters.get(random.nextInt(theaters.size())));
            showtime.setLocalDate(LocalDate.now().plusDays(random.nextInt(0, 30)));
            showtime.setLocalTime(showtimeTimes[random.nextInt(showtimeTimes.length)]);
            showtime.setTotalSeats(random.nextInt(50, 201));
            showtime.setAvailableSeats(showtime.getTotalSeats());
            showtime.setTicketPrice(BigDecimal.valueOf(random.nextInt(800, 2000) / 100.0));

            try {
                saved.add(showtimeManager.saveshowtime(showtime));
            } catch (Exception e) {
                log.error("Failed to seed showtime {}: {}", i, e.getMessage());
            }
        }
        return saved;
    }

    private void seedBookings(List<User> users, List<Showtime> showtimes) {
        Random random = new Random();

        for (int i = 0; i < 500; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Showtime randomShowtime = showtimes.get(random.nextInt(showtimes.size()));

            if (randomShowtime.getAvailableSeats() <= 0) continue;

            int seatCount = Math.min(random.nextInt(1, 6), randomShowtime.getAvailableSeats());

            Booking booking = new Booking();
            booking.setUser(randomUser);
            booking.setShowtime(randomShowtime);
            booking.setSeatCount(seatCount);
            booking.setTotalPrice(randomShowtime.getTicketPrice().multiply(BigDecimal.valueOf(seatCount)));
            booking.setBookingTime(LocalDateTime.now());

            try {
                bookingManager.saveBooking(booking);
                randomShowtime.setAvailableSeats(randomShowtime.getAvailableSeats() - seatCount);
            } catch (Exception e) {
                log.error("Failed to seed booking {}: {}", i, e.getMessage());
            }
        }
    }


    private List<String> generateNames(int count, List<String> list){
        List<String> names = new ArrayList<>();

        for(int i = 0; i < count; i++){
            String name = list.get(i % list.size()) + " " + list.get((i + 1) % list.size());
            names.add(name);
        }
        return names;
    }

    private <T> List<T> randomSample(List<T> list, int count) {
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private List<String> emailGenerator(List<String> firstNames, List<String> lastNames, int count) {
        List<String> emails = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            String firstName = firstNames.get(i % firstNames.size());
            String lastName = lastNames.get(i % lastNames.size());
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.com";
            emails.add(email);
        }
        return emails;
    }
}
