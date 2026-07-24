package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.dto.requests.DirectorRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.service.DirectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> directorSearch(
            @RequestBody DirectorRequest directorRequest,
            @RequestParam (defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "director") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        log.info("directorSearch" + directorRequest);
        return ResponseEntity.ok(directorService.directorSearch(directorRequest, customSorting, sortBy, pageNumber, size));
    }
}
