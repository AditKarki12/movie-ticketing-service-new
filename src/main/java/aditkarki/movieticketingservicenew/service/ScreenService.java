package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.ScreenRequest;
import aditkarki.movieticketingservicenew.dto.responses.ScreenResponse;
import aditkarki.movieticketingservicenew.entity.Screen;
import aditkarki.movieticketingservicenew.entity.Seat;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.enums.SeatType;
import aditkarki.movieticketingservicenew.exception.BookingConflictException;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.manager.ScreenManager;
import aditkarki.movieticketingservicenew.manager.ShowtimeManager;
import aditkarki.movieticketingservicenew.manager.TheaterManager;
import aditkarki.movieticketingservicenew.mapper.ScreenMapper;
import aditkarki.movieticketingservicenew.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenService {
    private final ScreenMapper screenMapper;
    private final ScreenManager screenManager;
    private final TheaterManager theaterManager;
    private final ShowtimeManager showtimeManager;
    private final SeatRepository seatRepository;

    public ScreenResponse createScreen(ScreenRequest screenRequest) {
        Theater theater = theaterManager.findTheaterById(screenRequest.getTheaterId());
        if(screenManager.existsByScreenName(theater, screenRequest.getScreenName())) {
            throw new DuplicateResourceException(screenRequest.getScreenName() + " already exists");
        }
        Screen screen = screenMapper.toEntity(screenRequest);
        screen.setTheater(theater);
        Screen savedScreen = screenManager.saveScreen(screen);
        for (char row = 'A'; row <= 'J'; row++) {
            for (int num = 1; num <= 10; num++) {
                Seat seat = new Seat();
                seat.setScreen(savedScreen);
                seat.setRowLabel(String.valueOf(row));
                seat.setSeatType(SeatType.STANDARD);
                seat.setSeatNumber(((long) num));
                seatRepository.save(seat);
            }
        }
        return screenMapper.toResponse(savedScreen);
    }

    public ScreenResponse getScreenById(Long screenId) {
        Screen screen = screenManager.findById(screenId);
        return screenMapper.toResponse(screen);
    }

    public List<ScreenResponse> getAllScreens() {
        return screenManager.findAll().stream().map(screenMapper::toResponse).collect(Collectors.toList());
    }
    
    public ScreenResponse updateScreen(Long id, ScreenRequest screenRequest) {
        Screen existingScreen = screenManager.findById(id);
        Theater theater = screenRequest.getTheaterId() != null
                ? theaterManager.findTheaterById(screenRequest.getTheaterId())
                : existingScreen.getTheater();
        if (screenRequest.getScreenName() != null && !screenRequest.getScreenName().equals(existingScreen.getScreenName()) && screenManager.existsByScreenName(theater, screenRequest.getScreenName())) {
            throw new DuplicateResourceException(screenRequest.getScreenName());
        }
        screenMapper.updateEntityfromRequest(screenRequest, existingScreen);
        existingScreen.setTheater(theater);
        screenManager.saveScreen(existingScreen);
        return screenMapper.toResponse(existingScreen);
    }

    public void deleteScreen(Long id) {
        Screen existingScreen = screenManager.findById(id);
        if (showtimeManager.existsByScreenId(id)) {
            throw new BookingConflictException("Cannot delete screen with existing showtimes");
        }
        screenManager.deleteScreen(existingScreen);
    }
}
