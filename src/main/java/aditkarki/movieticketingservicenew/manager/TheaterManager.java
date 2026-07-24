package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.TheaterDocument;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.TheaterMapper;
import aditkarki.movieticketingservicenew.repository.TheaterRepository;
import aditkarki.movieticketingservicenew.event.TheaterUpdatedEvent;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TheaterManager {
    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final ApplicationEventPublisher eventPublisher;

    public Theater saveTheater(Theater theater) {
        Theater savedTheater = theaterRepository.save(theater);
        syncToES(savedTheater);
        eventPublisher.publishEvent(new TheaterUpdatedEvent(savedTheater.getTheaterId(), savedTheater.getTheaterName()));
        return savedTheater;
    }

    public Theater findTheaterById(Long theaterId) {
        return theaterRepository.findById(theaterId).orElseThrow(() -> new ResourceNotFoundException("Theater", theaterId));
    }

    public List<Theater> findAllTheaters() {
        return theaterRepository.findAll();
    }

    public void deleteTheater(Long theaterId) {
        Theater theater = findTheaterById(theaterId);
        theaterRepository.delete(theater);
        deleteFromES(theaterId);
    }

    public boolean existsByTheaterName(String theaterName) {
        return theaterRepository.existsByTheaterName(theaterName);
    }

    private void syncToES(Theater theater) {
        try {
            TheaterDocument document = theaterMapper.toDocument(theater);
            document.setTheaterId(String.valueOf(theater.getTheaterId()));

            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.THEATERS_INDEX)
                    .id(String.valueOf(theater.getTheaterId()))
                    .document(document)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void deleteFromES(Long id) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(ElasticSearchConstants.THEATERS_INDEX)
                    .id(String.valueOf(id))
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
