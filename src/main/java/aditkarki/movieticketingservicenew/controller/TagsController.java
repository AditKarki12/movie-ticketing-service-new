package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.TagsRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.service.TagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagsController {
    private final TagsService tagsService;

    @PostMapping("/search")
    public ResponseEntity<TableResponse> tagsSearch(
            @RequestBody TagsRequest tagsRequest,
            @RequestParam(defaultValue = "ASC") CustomSorting customSorting,
            @RequestParam (defaultValue = "tags") String sortBy,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(tagsService.tagsSearch(tagsRequest, customSorting, sortBy, pageNumber, size));
    }
}
