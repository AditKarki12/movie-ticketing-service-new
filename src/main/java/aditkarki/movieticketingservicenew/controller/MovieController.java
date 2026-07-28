package aditkarki.movieticketingservicenew.controller;


import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TableResponse> searchMovies(
            @RequestBody MovieSearchRequest movieSearchRequest,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting){
        return ResponseEntity.ok(movieService.getMovie(movieSearchRequest, pageNumber, size, sortBy, customSorting));
    }

    // Disabled: these called MovieService's disconnected ES-only CRUD path (see MovieService for why).
    /*
    @PostMapping("/es")
    public ResponseEntity<MovieResponse> createMovieES(@RequestBody MovieRequest movieRequest) {
        return new ResponseEntity<>(movieService.createMovieES(movieRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/es")
    public ResponseEntity<Void> deleteMovieES(@RequestBody MovieRequest movieRequest) {
        movieService.deleteMovieES(movieRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/title")
    public ResponseEntity<MovieResponse> updateMovieES(
            @RequestBody MovieRequest movieRequest,
            @RequestParam String title) {
        return new ResponseEntity<>(movieService.updateMovieES(movieRequest, title), HttpStatus.CREATED);
    }
    */

}
