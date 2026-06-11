package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticsearchMovieRepository extends ElasticsearchRepository<MovieDocument, String> {
    List<MovieDocument> findByTitleContaining(String title);
    List<MovieDocument> findByGenre(String genre);
    List<MovieDocument> findByLanguage(String language);
}
