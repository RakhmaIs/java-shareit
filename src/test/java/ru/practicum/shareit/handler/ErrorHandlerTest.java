package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.UniqueEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.handler.ErrorsHandler;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    ErrorsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ErrorsHandler();
    }

    @Test
    void handleNotFoundTest() {
        ResponseEntity<?> response = handler.handleNotFoundExc(new UserNotFoundException("error"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void handleConflictTest() {
        ResponseEntity<?> response = handler.handleDuplicateEmailExc(new UniqueEmailException("error"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
    }

    @Test
    void handleBadRequestTest() {
        RuntimeException exception = new RuntimeException("error");
        Map<String, String> expectedResponse = Map.of("error", exception.getMessage());

        Map<String, String> response = handler.handleBadRequestExc(exception);

        assertEquals(expectedResponse, response);
    }

    @Test
    void handleAlreadyExistExcTest() {
        ResponseEntity<?> response = handler.handleAlreadyExistsExc(new AlreadyExistException("error"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
    }
}
