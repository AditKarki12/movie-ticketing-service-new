package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.ShowtimeDocument;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.ShowtimeMapper;
import aditkarki.movieticketingservicenew.repository.ShowtimeRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowtimeManager {
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeMapper showtimeMapper;
    private final ElasticsearchClient elasticsearchClient;

    public Showtime saveshowtime(Showtime showtime) {
        Showtime showtimeSaved = showtimeRepository.save(showtime);
        syncToES(showtimeSaved);
        return showtimeSaved;
    }

    public Showtime findShowtimeById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId).orElseThrow(() -> new ResourceNotFoundException("showtime not found"));
    }

    public List<Showtime> findAllshowtimes() {
        return showtimeRepository.findAll();
    }

    public void deleteshowtime(Long showtimeId) {
        Showtime showtime = findShowtimeById(showtimeId);
        showtimeRepository.delete(showtime);
        deleteFromES(showtimeId);
    }

    public int reserveSeats(Long id, int seatCount) {
        return showtimeRepository.reserveSeats(id, seatCount);
    }

    public int releaseSeats(Long id, int seatCount) {
        return showtimeRepository.releaseSeats(id, seatCount);
    }

    public BigDecimal findTicketPrice(Long id) {
        return showtimeRepository.findTicketPriceByShowtimeId(id);
    }

    public boolean existsByTheaterId(Long theaterId) {
        return showtimeRepository.existsByTheater_TheaterId(theaterId);
    }

    public void resyncToES(List<Showtime> showtimes) {showtimes.forEach(this::syncToES);}

    public List<Showtime> findShowtimesByMovieId(Long movieId) {
        return showtimeRepository.findShowtimesByMovieId(movieId);
    }

    public List<Showtime> findShowtimeByTheaterId(Long theaterId) {
        return showtimeRepository.findShowtimesByTheater_TheaterId(theaterId);
    }

    public void syncToES(Showtime showtime) {
        ShowtimeDocument showtimeDocument = showtimeMapper.toDocument(showtime);
        showtimeDocument.setShowtimeId(String.valueOf(showtime.getShowtimeId()));

        try{
            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.SHOWTIMES_INDEX)
                    .id(String.valueOf(showtime.getShowtimeId()))
                    .document(showtimeDocument)
            );
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    public void deleteFromES(Long id){
        try{
            elasticsearchClient.delete(d -> d
                    .index(ElasticSearchConstants.SHOWTIMES_INDEX)
                    .id(String.valueOf(id))
            );
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
