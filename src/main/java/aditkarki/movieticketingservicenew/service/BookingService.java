package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.BookingRequest;
import aditkarki.movieticketingservicenew.dto.responses.BookingResponse;
import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.mapper.BookingMapper;
import aditkarki.movieticketingservicenew.repository.BookingRepository;
import aditkarki.movieticketingservicenew.repository.ShowtimeRepository;
import aditkarki.movieticketingservicenew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingResponse createBooking(BookingRequest bookingRequest) {
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", bookingRequest.getUserId()));
        Showtime showtime = showtimeRepository.findById(bookingRequest.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", bookingRequest.getShowtimeId()));
        Booking booking = bookingMapper.toEntity(bookingRequest);
        booking.setUser(user);
        booking.setShowtime(showtime);
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.toResponse(bookingSaved);
    }

    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        return bookingMapper.toResponse(booking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(bookingMapper::toResponse).toList();
    }

    public BookingResponse updateBooking(Long bookingId, BookingRequest bookingRequest) {
        return bookingRepository.findById(bookingId).map(existingBooking -> {
            bookingMapper.updateEntityFromRequest(bookingRequest, existingBooking);
            bookingRepository.save(existingBooking);
            return bookingMapper.toResponse(existingBooking);
        }).orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
    }

    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        bookingRepository.delete(booking);
    }
}
