package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.dto.requests.ShowtimeSeatSelectionRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeSeatResponse;
import aditkarki.movieticketingservicenew.entity.ShowtimeSeat;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import aditkarki.movieticketingservicenew.exception.BookingConflictException;
import aditkarki.movieticketingservicenew.exception.InvalidRequestException;
import aditkarki.movieticketingservicenew.manager.ShowtimeSeatManager;
import aditkarki.movieticketingservicenew.manager.UserManager;
import aditkarki.movieticketingservicenew.mapper.ShowtimeSeatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowtimeSeatService {
    private static final long HOLD_DURATION_MINUTES = 10;

    private final ShowtimeSeatManager showtimeSeatManager;
    private final ShowtimeSeatMapper showtimeSeatMapper;
    private final UserManager userManager;

    public List<ShowtimeSeatResponse> getShowtimeSeats(Long showtimeId) {
        return showtimeSeatManager.findByShowtimeId(showtimeId).stream().map(showtimeSeatMapper::toResponse).toList();
    }

    public List<ShowtimeSeatResponse> holdSeats(ShowtimeSeatSelectionRequest request, Authentication authentication) {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
        return transitionSeats(request, ShowtimeSeatStatus.AVAILABLE, ShowtimeSeatStatus.HELD, expiry, authentication, true, false);
    }

    public List<ShowtimeSeatResponse> releaseSeats(ShowtimeSeatSelectionRequest request, Authentication authentication) {
        return transitionSeats(request, ShowtimeSeatStatus.HELD, ShowtimeSeatStatus.AVAILABLE, null, authentication, false, true);
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHolds() {
        List<ShowtimeSeat> expiredHolds = showtimeSeatManager.findExpiredHolds(LocalDateTime.now());
        for (ShowtimeSeat showtimeSeat : expiredHolds) {
            showtimeSeatManager.conditionalUpdateStatus(showtimeSeat.getId(), ShowtimeSeatStatus.HELD, ShowtimeSeatStatus.AVAILABLE, null, null);
        }
    }

    private List<ShowtimeSeatResponse> transitionSeats(ShowtimeSeatSelectionRequest request, ShowtimeSeatStatus expectedStatus,
            ShowtimeSeatStatus newStatus, LocalDateTime expiry, Authentication authentication,
            boolean stampRequesterAsHolder, boolean enforceOwnership) {
        if (request.getShowtimeId() == null || request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new InvalidRequestException("Showtime id and at least one seat id are required");
        }

        User requester = resolveUser(authentication);
        User heldByOnSuccess = stampRequesterAsHolder ? requester : null;

        List<ShowtimeSeat> transitioned = new ArrayList<>();
        try {
            for (Long seatId : request.getSeatIds()) {
                ShowtimeSeat showtimeSeat = showtimeSeatManager.findByShowtimeIdAndSeatId(request.getShowtimeId(), seatId);
                if (enforceOwnership && !isOwnerOrAdmin(showtimeSeat.getHeldBy(), requester, authentication)) {
                    throw new AccessDeniedException("You do not have permission to modify seat " + seatId);
                }
                int updated = showtimeSeatManager.conditionalUpdateStatus(showtimeSeat.getId(), expectedStatus, newStatus, expiry, heldByOnSuccess);
                if (updated == 0) {
                    throw new BookingConflictException("Seat " + seatId + " is no longer " + expectedStatus.name().toLowerCase());
                }
                transitioned.add(showtimeSeat);
            }
        } catch (RuntimeException e) {
            for (ShowtimeSeat showtimeSeat : transitioned) {
                showtimeSeatManager.conditionalUpdateStatus(showtimeSeat.getId(), newStatus, expectedStatus, showtimeSeat.getHoldExpiresAt(), showtimeSeat.getHeldBy());
            }
            log.error(e.getMessage());
            throw e;
        }

        return transitioned.stream()
                .map(showtimeSeat -> showtimeSeatManager.findByShowtimeIdAndSeatId(request.getShowtimeId(), showtimeSeat.getSeat().getId()))
                .map(showtimeSeatMapper::toResponse)
                .toList();
    }

    private User resolveUser(Authentication authentication) {
        return userManager.findUserByEmail(authentication.getName());
    }

    private boolean isOwnerOrAdmin(User heldBy, User requester, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
        return isAdmin || (heldBy != null && heldBy.getUserId().equals(requester.getUserId()));
    }
}
