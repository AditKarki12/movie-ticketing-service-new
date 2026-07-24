package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.BookingRequest;
import aditkarki.movieticketingservicenew.dto.requests.BookingSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.BookingResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest bookingRequest, Authentication authentication){
        return new ResponseEntity<>(bookingService.createBooking(bookingRequest, authentication), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id, Authentication authentication){
        return ResponseEntity.ok(bookingService.getBookingById(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id, @RequestBody BookingRequest bookingRequest, Authentication authentication){
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingRequest, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id, Authentication authentication){
        bookingService.deleteBooking(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, Authentication authentication){
        return ResponseEntity.ok(bookingService.cancelBooking(id, authentication));
    }

    @PostMapping("/search")
    public ResponseEntity<TableResponse> searchBookings(
            @RequestBody BookingSearchRequest bookingSearchRequest,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingTime") String sortBy,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting){
        return ResponseEntity.ok(bookingService.getBooking(bookingSearchRequest, pageNumber, size, sortBy, customSorting));
    }
}
