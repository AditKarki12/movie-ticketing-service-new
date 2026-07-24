package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.TheaterStateRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.service.TheaterStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaterState")
@RequiredArgsConstructor
public class TheaterStateController {
    private final TheaterStateService theaterStateService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> theaterStateSearch(
            @RequestBody TheaterStateRequest theaterStateRequest,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "theaterState") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(theaterStateService.theaterStateSearch(theaterStateRequest, customSorting, sortBy, pageNumber, size));
    }
}
