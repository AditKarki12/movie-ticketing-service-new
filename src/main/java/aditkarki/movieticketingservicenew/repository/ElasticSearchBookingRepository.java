package aditkarki.movieticketingservicenew.repository;

import aditkarki.movieticketingservicenew.document.BookingDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchBookingRepository extends ElasticsearchRepository<BookingDocument, String> {
}
