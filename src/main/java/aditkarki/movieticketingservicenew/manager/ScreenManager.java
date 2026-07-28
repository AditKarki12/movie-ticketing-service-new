package aditkarki.movieticketingservicenew.manager;

import aditkarki.movieticketingservicenew.entity.Screen;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScreenManager {
    private final ScreenRepository screenRepository;

    public Screen saveScreen(Screen screen) {
        return screenRepository.save(screen);
    }

    public Screen findById(Long id) {
        return screenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
    }

    public List<Screen> findByTheaterId(Long theaterId) {
        return screenRepository.findByTheater_TheaterId(theaterId);
    }

    public List<Screen> findAll(){
        return screenRepository.findAll();
    }

    public void deleteScreen(Screen screen) {
        screenRepository.delete(screen);
    }

    public boolean existsByScreenName(Theater theater, String screenName) {
        return screenRepository.existsByTheaterAndScreenName(theater, screenName);
    }

}
