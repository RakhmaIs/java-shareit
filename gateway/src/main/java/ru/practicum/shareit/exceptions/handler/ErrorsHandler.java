package ru.practicum.shareit.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemClientController;
import ru.practicum.shareit.request.ItemRequestClientController;
import ru.practicum.shareit.user.UserClientController;

import java.util.Map;


@Slf4j
@RestControllerAdvice(basePackageClasses = {UserClientController.class, ItemRequestClientController.class, ItemClientController.class, BookingController.class})
public class ErrorsHandler {


    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            BookingNotFoundException.class, ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNotFoundExc(final RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UniqueEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleDuplicateEmailExc(final RuntimeException e) {
        log.error("Запрос не выполнен. Email должен быть уникален.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BookingNotAvailableException.class, PaginationInvalidParamException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExc(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleAlreadyExistsExc(final RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

}

