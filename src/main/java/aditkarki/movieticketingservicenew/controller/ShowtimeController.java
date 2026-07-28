package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.entity.Seat;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.requests.ShowtimeSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ShowtimeResponse> createShowtime(@RequestBody ShowtimeRequest showtimeRequest) {
        return new ResponseEntity<>(showtimeService.createShowtime(showtimeRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeResponse> getShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getShowtimeById(id));
    }

    @GetMapping
    public ResponseEntity<List<ShowtimeResponse>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowtimeResponse> updateShowtime(@PathVariable Long id, @RequestBody ShowtimeRequest showtimeRequest) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtimeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<TableResponse> searchShowtimes(
            @RequestBody ShowtimeSearchRequest showtimeSearchRequest,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "localDate") String sortBy,
            @RequestParam(defaultValue = "DESC") CustomSorting customSorting){
        return ResponseEntity.ok(showtimeService.getShowtime(showtimeSearchRequest, pageNumber, size, sortBy, customSorting));
    }

}
