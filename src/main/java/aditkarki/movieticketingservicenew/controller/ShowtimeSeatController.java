package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.ShowtimeSeatSelectionRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeSeatResponse;
import aditkarki.movieticketingservicenew.service.ShowtimeSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes/{showtimeId}/seats")
@RequiredArgsConstructor
public class ShowtimeSeatController {
    private final ShowtimeSeatService showtimeSeatService;

    @GetMapping
    public ResponseEntity<List<ShowtimeSeatResponse>> getShowtimeSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(showtimeSeatService.getShowtimeSeats(showtimeId));
    }

    @PostMapping("/hold")
    public ResponseEntity<List<ShowtimeSeatResponse>> holdSeats(@PathVariable Long showtimeId, @RequestBody ShowtimeSeatSelectionRequest request, Authentication authentication) {
        request.setShowtimeId(showtimeId);
        return ResponseEntity.ok(showtimeSeatService.holdSeats(request, authentication));
    }

    @PostMapping("/release")
    public ResponseEntity<List<ShowtimeSeatResponse>> releaseSeats(@PathVariable Long showtimeId, @RequestBody ShowtimeSeatSelectionRequest request, Authentication authentication) {
        request.setShowtimeId(showtimeId);
        return ResponseEntity.ok(showtimeSeatService.releaseSeats(request, authentication));
    }
}
