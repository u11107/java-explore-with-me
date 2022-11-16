package ru.practicum.explore.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ApiError {
    private List<String> errors;

    private String message;

    private String reason;

    private String status;

    private LocalDateTime timestamp;
}
