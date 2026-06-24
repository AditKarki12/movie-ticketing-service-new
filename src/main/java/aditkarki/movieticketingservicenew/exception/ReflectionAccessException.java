package aditkarki.movieticketingservicenew.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ReflectionAccessException extends RuntimeException {
    public ReflectionAccessException(String message) {
        super(message);
    }
}
