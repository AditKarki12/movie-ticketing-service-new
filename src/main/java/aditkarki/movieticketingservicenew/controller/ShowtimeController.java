package aditkarki.movieticketingservicenew.controller;

import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShowtimeResponse createShowtime(@RequestBody ShowtimeRequest showtimeRequest){
        return showtimeService.createShowtime(showtimeRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShowtimeResponse getShowtimeById(@PathVariable Long id){
        return showtimeService.getShowtimeById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShowtimeResponse> getAllShowtimes(){
        return showtimeService.getAllShowtimes();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShowtimeResponse updateShowtime(@PathVariable Long id, @RequestBody ShowtimeRequest showtimeRequest){
        return showtimeService.updateShowtime(id, showtimeRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShowtime(@PathVariable Long id){
        showtimeService.deleteShowtime(id);
    }


}
