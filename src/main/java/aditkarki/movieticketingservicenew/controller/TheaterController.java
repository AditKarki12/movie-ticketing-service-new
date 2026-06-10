package aditkarki.movieticketingservicenew.controller;



import aditkarki.movieticketingservicenew.dto.requests.TheaterRequest;
import aditkarki.movieticketingservicenew.dto.responses.TheaterResponse;
import aditkarki.movieticketingservicenew.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theater")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TheaterResponse createTheater(@RequestBody TheaterRequest theaterRequest) {
        return theaterService.createTheater(theaterRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TheaterResponse getTheaterById(@PathVariable Long id){
        return theaterService.getTheaterById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TheaterResponse> getAllTheaters(){
        return theaterService.getAllTheaters();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TheaterResponse updateTheater(@PathVariable Long id, @RequestBody TheaterRequest theaterRequest){
        return theaterService.updateTheater(id, theaterRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheater(@PathVariable Long id){
        theaterService.deleteTheater(id);
    }
}
