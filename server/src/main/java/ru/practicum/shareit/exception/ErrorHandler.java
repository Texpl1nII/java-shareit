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

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException exception) {
        log.error("NotFoundException: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final ConflictException exception) {
        log.error("ConflictException: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final BadRequestException exception) {
        log.error("BadRequestException: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        log.error("ValidationException: {}", errorMessage, exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingHeader(final MissingRequestHeaderException exception) {
        log.error("MissingHeaderException: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", "Required header '" + exception.getHeaderName() + "' is missing");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleJsonParseError(final HttpMessageNotReadableException exception) {
        log.error("JsonParseError: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", "JSON parse error: " + exception.getLocalizedMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        log.error("TypeMismatch: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        String errorMessage = String.format("Неверный тип параметра '%s': ожидается %s",
                exception.getName(), exception.getRequiredType().getSimpleName());
        return Map.of("error", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(final ForbiddenException exception) {
        log.error("ForbiddenException: {}", exception.getMessage(), exception);  // ← ДОБАВЛЕНО ", exception"
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)  // ← ИЗМЕНЕНО: Конкретный Exception вместо Throwable
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Exception exception) {  // ← ИЗМЕНЕНО: Exception вместо Throwable
        log.error("Unhandled exception: ", exception);  // ← КРИТИЧЕСКИ ВАЖНО: запятая и exception
        return Map.of("error", "Internal server error");
    }
}
