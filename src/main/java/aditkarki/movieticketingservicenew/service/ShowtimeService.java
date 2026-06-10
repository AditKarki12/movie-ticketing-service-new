package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.mapper.ShowtimeMapper;
import aditkarki.movieticketingservicenew.repository.MovieRepository;
import aditkarki.movieticketingservicenew.repository.ShowtimeRepository;
import aditkarki.movieticketingservicenew.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeMapper showtimeMapper;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;

    public ShowtimeResponse createShowtime(ShowtimeRequest showtimeRequest) {
        Movie movie = movieRepository.findById(showtimeRequest.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Theater theater = theaterRepository.findById(showtimeRequest.getTheaterId()).orElseThrow(() -> new ResourceNotFoundException("Theater not found"));
        Showtime showtime = showtimeMapper.toEntity(showtimeRequest);
        showtime.setMovie(movie);
        showtime.setTheater(theater);

        return showtimeMapper.toResponse(showtimeRepository.save(showtime));
    }

    public ShowtimeResponse getShowtimeById(Long id){
        Showtime showtime = showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
        return showtimeMapper.toResponse(showtime);
    }

    public List<ShowtimeResponse> getAllShowtimes(){
        return showtimeRepository.findAll().stream().map(showtimeMapper::toResponse).toList();
    }

    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest showtimeRequest){
        return showtimeRepository.findById(id).map(existingShowtime -> {
            showtimeMapper.updateEntityFromRequest(showtimeRequest, existingShowtime);
            showtimeRepository.save(existingShowtime);
            return showtimeMapper.toResponse(existingShowtime);
        }).orElse(null);
    }

    public void deleteShowtime(Long id){
        Showtime showtime = showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
        showtimeRepository.delete(showtime);
    }
}
