package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.BookingDocument;
import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.mapper.BookingMapper;
import aditkarki.movieticketingservicenew.repository.BookingRepository;
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
public class BookingManager {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final ApplicationEventPublisher eventPublisher;

    public Booking saveBooking(Booking booking) {
        Booking bookingSaved = bookingRepository.save(booking);
        syncToES(bookingSaved);
        return bookingSaved;
    }

    public Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        bookingRepository.delete(booking);
        deleteFromES(bookingId);
    }

    public List<Booking> findBookingsByUserId(Long userId) {
        return bookingRepository.findByUser_UserId(userId);
    }

    public List<Booking> findBookingsByMovieId(Long movieId) {
        return bookingRepository.findByShowtime_Movie_Id(movieId);
    }

    public List<Booking> findBookingsByTheaterId(Long theaterId) {
        return bookingRepository.findByShowtime_Theater_TheaterId(theaterId);
    }

    public boolean hasActiveBookingsForShowtime(Long showtimeId) {
        return bookingRepository.existsByShowtime_ShowtimeIdAndStatusNot(showtimeId, BookingStatus.CANCELLED);
    }

    public void resyncToES(List<Booking> bookings) {
        bookings.forEach(this::syncToES);
    }

    private void syncToES(Booking booking) {
        try{
            BookingDocument document = bookingMapper.toDocument(booking);
            document.setBookingId(String.valueOf(booking.getBookingId()));

            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.BOOKINGS_INDEX)
                    .id(String.valueOf(booking.getBookingId()))
                    .document(document)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void deleteFromES(Long id){
        try{
            elasticsearchClient.delete(d -> d
                    .index(ElasticSearchConstants.BOOKINGS_INDEX)
                    .id(String.valueOf(id)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
