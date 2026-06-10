package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import aditkarki.movieticketingservicenew.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieResponse createMovie(MovieRequest movieRequest) {
        Movie movie = movieMapper.toEntity(movieRequest);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(savedMovie);
    }

    public MovieResponse getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream().map(movieMapper::toResponse).toList();
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest movieRequest) {
        return movieRepository.findById(movieId).map(existingMovie -> {
            movieMapper.updateEntityFromRequest(movieRequest, existingMovie);
            movieRepository.save(existingMovie);
            return movieMapper.toResponse(existingMovie);
        }).orElse(null);
    }

    public void deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        movieRepository.delete(movie);
    }

}
