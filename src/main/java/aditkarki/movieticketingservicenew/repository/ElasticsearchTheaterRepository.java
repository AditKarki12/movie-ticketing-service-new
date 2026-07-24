package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.TheaterDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchTheaterRepository extends ElasticsearchRepository<TheaterDocument, String> {
}
