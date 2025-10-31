package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException exception) {
        log.error("NotFoundException: {}", exception.getMessage(), exception);
        return new ErrorResponse("Not Found", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException exception) {
        log.error("ConflictException: {}", exception.getMessage(), exception);
        return new ErrorResponse("Conflict", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException exception) {
        log.error("BadRequestException: {}", exception.getMessage(), exception);
        return new ErrorResponse("Bad Request", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        log.error("ValidationException: {}", errorMessage, exception);
        return new ErrorResponse("Validation Error", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(final MissingRequestHeaderException exception) {
        log.error("MissingHeaderException: {}", exception.getMessage(), exception);
        String description = "Required header '" + exception.getHeaderName() + "' is missing";
        return new ErrorResponse("Missing Header", description);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJsonParseError(final HttpMessageNotReadableException exception) {
        log.error("JsonParseError: {}", exception.getMessage(), exception);
        String description = "JSON parse error: " + exception.getLocalizedMessage();
        return new ErrorResponse("JSON Parse Error", description);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        log.error("TypeMismatch: {}", exception.getMessage(), exception);
        String description = String.format("Parameter type mismatch '%s': expected %s",
                exception.getName(), exception.getRequiredType().getSimpleName());
        return new ErrorResponse("Type Mismatch", description);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException exception) {
        log.error("ForbiddenException: {}", exception.getMessage(), exception);
        return new ErrorResponse("Forbidden", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {
        log.error("Unhandled exception: ", exception);
        return new ErrorResponse("Internal Server Error", "An unexpected error occurred");
    }
}
