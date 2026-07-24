package aditkarki.movieticketingservicenew.controller;



import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.TheaterRequest;
import aditkarki.movieticketingservicenew.dto.requests.TheaterSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.dto.responses.TheaterResponse;
import aditkarki.movieticketingservicenew.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @PostMapping
    public ResponseEntity<TheaterResponse> createTheater(@RequestBody TheaterRequest theaterRequest) {
        return new ResponseEntity<>(theaterService.createTheater(theaterRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterResponse> getTheaterById(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getTheaterById(id));
    }

    @GetMapping
    public ResponseEntity<List<TheaterResponse>> getAllTheaters() {
        return ResponseEntity.ok(theaterService.getAllTheaters());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterResponse> updateTheater(@PathVariable Long id, @RequestBody TheaterRequest theaterRequest) {
        return ResponseEntity.ok(theaterService.updateTheater(id, theaterRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        theaterService.deleteTheater(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/search")
    public ResponseEntity<TableResponse> searchTheater(
            @RequestBody TheaterSearchRequest theaterSearchRequest,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "screensTotal") String sortBy,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting) {
        return ResponseEntity.ok(theaterService.getTheater(theaterSearchRequest, pageNumber, size, sortBy, customSorting));
    }
}
