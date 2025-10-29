package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.error("Validation error: {}", errorMessage);
        ErrorResponse errorResponse = new ErrorResponse("Validation Error", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Bad Request", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException e) {
        log.error("HTTP client error: {} - {}", e.getStatusCode(), e.getMessage());

        String description = switch (e.getStatusCode()) {
            case HttpStatus.NOT_FOUND -> "Resource not found";
            case HttpStatus.BAD_REQUEST -> "Bad request to server";
            case HttpStatus.CONFLICT -> "Conflict on server";
            default -> "Server error: " + e.getStatusCode();
        };

        ErrorResponse errorResponse = new ErrorResponse("Server Error", description);
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
}
