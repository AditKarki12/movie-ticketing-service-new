package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.TheaterCityRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.service.TheaterCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaterCity")
@RequiredArgsConstructor
public class TheaterCityController {
    private final TheaterCityService theaterCityService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> theaterCitySearch(
            @RequestBody TheaterCityRequest theaterCityRequest,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "theaterCity") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(theaterCityService.theaterCitySearch(theaterCityRequest, customSorting, sortBy, pageNumber, size));
    }
}