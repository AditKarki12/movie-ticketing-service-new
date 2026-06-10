package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse createMovie(@RequestBody MovieRequest movieRequest) {
        return movieService.createMovie(movieRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MovieResponse getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MovieResponse updateMovie(@PathVariable Long id, @RequestBody MovieRequest movieRequest) {
        return movieService.updateMovie(id, movieRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
