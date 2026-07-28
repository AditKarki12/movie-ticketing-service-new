package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.ScreenRequest;
import aditkarki.movieticketingservicenew.dto.responses.ScreenResponse;
import aditkarki.movieticketingservicenew.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
public class ScreenController {
    private final ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> createScreen(@RequestBody ScreenRequest screenRequest) {
        return new ResponseEntity<>(screenService.createScreen(screenRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreenResponse> getScreenById(@PathVariable Long id) {
        return ResponseEntity.ok(screenService.getScreenById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScreenResponse>> getAllScreens() {
        return ResponseEntity.ok(screenService.getAllScreens());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreenResponse> updateScreen(@PathVariable Long id, @RequestBody ScreenRequest screenRequest) {
        return ResponseEntity.ok(screenService.updateScreen(id, screenRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }

}
