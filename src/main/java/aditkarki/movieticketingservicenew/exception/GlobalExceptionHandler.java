package aditkarki.movieticketingservicenew.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(DuplicateResourceException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBookingConflictException(BookingConflictException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReflectionAccessException.class)
    public ResponseEntity<Map<String, Object>> handleReflectionAccessException(ReflectionAccessException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<Map<String, Object>> handleSearchException(SearchException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequestException(InvalidRequestException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This resource was updated by another request. Please retry.");
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException exception, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid email or password");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidSortByException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSortByException(InvalidSortByException exception, WebRequest request){
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
