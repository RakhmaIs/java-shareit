package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserNotFoundExceptionTest {

    @Test
    void userNotFoundException() {
        String s = "Not found.";
        UserNotFoundException exception = new UserNotFoundException(s);

        assertThat(exception.getMessage(), equalTo(s));
    }
}
