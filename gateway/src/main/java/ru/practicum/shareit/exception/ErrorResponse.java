package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String description;
    private LocalDateTime timestamp;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}