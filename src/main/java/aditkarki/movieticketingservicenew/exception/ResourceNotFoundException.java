package aditkarki.movieticketingservicenew.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String object, Long id) {
        super("Could not find object " + object + " with id " + id);
    }

    public ResourceNotFoundException(String object) {
        super("Could not find object " + object);
    }
}
