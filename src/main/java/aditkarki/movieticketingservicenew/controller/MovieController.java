package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.service.MovieService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@RequestBody MovieRequest movieRequest) {
        return new ResponseEntity<>(movieService.createMovie(movieRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @RequestBody MovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.updateMovie(id, movieRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @PostMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(@RequestBody MovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.getMovie(movieRequest));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<MovieResponse>> searchByDate(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(movieService.searchByReleaseDate(startDate, endDate));
    }

    @GetMapping("/search/genre")
    public ResponseEntity<List<MovieResponse>> searchByGenre(@RequestParam String genre) {
        return ResponseEntity.ok(movieService.searchByGenre(genre));
    }

    @GetMapping("/search/tag")
    public ResponseEntity<List<MovieResponse>> searchByTag(@RequestParam String tag) {
        return ResponseEntity.ok(movieService.searchByTag(tag));
    }

    @GetMapping("/search/language")
    public ResponseEntity<List<MovieResponse>> searchByLanguage(@RequestParam String language) {
        return ResponseEntity.ok(movieService.searchByLanguage(language));
    }

    @GetMapping("/search/duration")
    public ResponseEntity<List<MovieResponse>> searchByDuration(
            @RequestParam int min,
            @RequestParam int max) {
        return ResponseEntity.ok(movieService.searchByDuration(min, max));
    }

    @GetMapping("/search/rating")
    public ResponseEntity<List<MovieResponse>> searchByRating(
            @RequestParam double min,
            @RequestParam double max) {
        return ResponseEntity.ok(movieService.searchByRating(min, max));
    }

    @GetMapping("/search/active")
    public ResponseEntity<List<MovieResponse>> searchByIsActive(@RequestParam Boolean isActive) {
        return ResponseEntity.ok(movieService.searchByIsActive(isActive));
    }

}
