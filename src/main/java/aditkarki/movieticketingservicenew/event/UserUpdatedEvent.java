package aditkarki.movieticketingservicenew.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserUpdatedEvent {
    private final Long userId;
    private final String userEmail;
}