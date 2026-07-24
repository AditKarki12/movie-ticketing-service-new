package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchUserRepository extends ElasticsearchRepository<UserDocument, String> {
}
