package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import aditkarki.movieticketingservicenew.repository.ElasticsearchMovieRepository;
import aditkarki.movieticketingservicenew.repository.MovieRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieManager {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ElasticsearchClient elasticsearchClient;

    public Movie saveMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        syncToES(savedMovie);
        return savedMovie;
    }

    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
        deleteFromES(movieId);
    }

    public Movie findById(Long movieId) {
        return movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    public boolean existsByTitle(String title) {
        return movieRepository.existsByTitle(title);
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    private void syncToES(Movie movie){
        try{
            MovieDocument document = movieMapper.toDocument(movie);
            document.setId(String.valueOf(movie.getId()));

            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.MOVIES_INDEX)
                    .id(String.valueOf(movie.getId()))
                    .document(document)
            );
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private void deleteFromES(Long id){
        try{
            elasticsearchClient.delete(d -> d
                    .index(ElasticSearchConstants.MOVIES_INDEX)
                    .id(String.valueOf(id))
            );
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
