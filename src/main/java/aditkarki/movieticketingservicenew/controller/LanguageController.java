package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.LanguageRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> languageSearch(
            @RequestBody LanguageRequest languageRequest,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "language") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(languageService.languageSearch(languageRequest, customSorting, sortBy, pageNumber, size));
    }
}
