package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.TheaterRequest;
import aditkarki.movieticketingservicenew.dto.responses.TheaterResponse;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.mapper.TheaterMapper;
import aditkarki.movieticketingservicenew.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;

    public TheaterResponse createTheater(TheaterRequest theaterRequest) {
        if (theaterRepository.existsByTheaterName(theaterRequest.getTheaterName())) {
            throw new DuplicateResourceException(theaterRequest.getTheaterName());
        }
        Theater theater = theaterMapper.toEntity(theaterRequest);
        Theater theaterSaved = theaterRepository.save(theater);
        return theaterMapper.toResponse(theaterSaved);
    }

    public List<TheaterResponse> getAllTheaters() {
        return theaterRepository.findAll().stream().map(theaterMapper::toResponse).toList();
    }

    public TheaterResponse getTheaterById(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", theaterId));
        return theaterMapper.toResponse(theater);
    }

    public TheaterResponse updateTheater(Long theaterId, TheaterRequest theaterRequest) {
        return theaterRepository.findById(theaterId).map(existingTheater -> {
            theaterMapper.updateEntityFromRequest(theaterRequest, existingTheater);
            theaterRepository.save(existingTheater);
            return theaterMapper.toResponse(existingTheater);
        }).orElseThrow(() -> new ResourceNotFoundException("Theater", theaterId));
    }

    public void deleteTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", theaterId));
        theaterRepository.delete(theater);
    }
}
