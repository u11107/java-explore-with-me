package ru.practicum.explore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(InputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError incorrectRequest(RuntimeException e) {
        return ApiError.builder()
                .message(e.getLocalizedMessage())
                .reason("Incorrect request parameters.")
                .status(String.valueOf(HttpStatus.BAD_REQUEST))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException e) {
        return ApiError.builder()
                .message(e.getLocalizedMessage())
                .reason("Object was not found.")
                .status(String.valueOf(HttpStatus.NOT_FOUND))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError forbidden(RuntimeException e) {
        return ApiError.builder()
                .message(e.getLocalizedMessage())
                .reason("For requested operation conditions are not met.")
                .status(String.valueOf(HttpStatus.FORBIDDEN))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError serverError(Throwable e) {
        return ApiError.builder()
                .message(e.getLocalizedMessage())
                .reason("Error occurred")
                .status(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
