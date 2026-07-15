package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.CustomAggregation;
import aditkarki.movieticketingservicenew.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovieServiceInterface {
    MovieResponse createMovie(MovieRequest movieRequest);
    MovieResponse getMovieById(Long movieId);
    List<MovieResponse> getAllMovies();
    MovieResponse updateMovie(Long movieId, MovieRequest movieRequest);
    void deleteMovie(Long movieId);
    TableResponse getMovie(MovieSearchRequest movieSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting, CustomAggregation customAggregation, String aggregationField);
}
