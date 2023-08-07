package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AlreadyExistExceptionTest {
    @Test
    void TestAlreadyExistException() {
        String s = "AlreadyExistException";
        UserNotFoundException exception = new UserNotFoundException(s);
        assertThat(exception.getMessage(), equalTo(s));
    }
}
