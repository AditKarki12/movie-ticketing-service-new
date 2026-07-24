package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.ShowtimeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchShowtimeRepository extends ElasticsearchRepository<ShowtimeDocument, String> {
}
