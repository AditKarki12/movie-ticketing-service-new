package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.BookingRequest;
import aditkarki.movieticketingservicenew.dto.responses.BookingResponse;
import aditkarki.movieticketingservicenew.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestBody BookingRequest BookingRequest){
        return bookingService.createBooking(BookingRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBookingById(@PathVariable Long id){
        return bookingService.getBookingById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getAllBookings(){
        return bookingService.getAllBookings();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse updateBooking(@PathVariable Long id, @RequestBody BookingRequest BookingRequest){
        return bookingService.updateBooking(id, BookingRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id){
        bookingService.deleteBooking(id);
    }
}
