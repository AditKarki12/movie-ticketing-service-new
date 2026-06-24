package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovieServiceInterface {
    MovieResponse createMovie(MovieRequest movieRequest);
    MovieResponse getMovieById(Long movieId);
    List<MovieResponse> getAllMovies();
    MovieResponse updateMovie(Long movieId, MovieRequest movieRequest);
    void deleteMovie(Long movieId);
    List<MovieResponse> getMovie(MovieSearchRequest movieSearchRequest);
}
