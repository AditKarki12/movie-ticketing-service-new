package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchMovieRepository extends ElasticsearchRepository<MovieDocument, String> {
}
