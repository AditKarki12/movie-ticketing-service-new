package aditkarki.movieticketingservicenew.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MovieSearchException extends RuntimeException {
    public MovieSearchException(String message) {
        super(message);
    }
}
