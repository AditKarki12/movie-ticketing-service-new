package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.GenreRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> genreSearch(
            @RequestBody GenreRequest genreRequest,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "genre") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(genreService.genreSearch(genreRequest, customSorting, sortBy, pageNumber, size));
    }
}
