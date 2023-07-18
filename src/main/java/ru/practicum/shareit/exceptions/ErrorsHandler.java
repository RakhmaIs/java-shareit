package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackageClasses = {UserController.class, ItemController.class, BookingController.class})
public class ErrorsHandler {

    private static final String ERROR = "error";

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundExc(final RuntimeException e) {
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler(UniqueEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateEmailExc(final RuntimeException e) {
        log.error("Запрос не выполнен. Email должен быть уникален.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler(BookingNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExc(final RuntimeException e) {
        log.error("Запрос не выполнен. Email должен быть уникален.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusExc(final ResponseStatusException e) {
        return new ResponseEntity<>(Map.of(ERROR, Objects.requireNonNull(e.getReason())), e.getStatus());
    }


    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<?> handleThrowable(final RuntimeException e) {
        return new ResponseEntity<>(ERROR, HttpStatus.CONFLICT);
    }
}

