package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UniqueEmailExceptionTest {
    @Test
    void testUniqueEmailException() {
        String s = "UniqueEmailException";
        UniqueEmailException exception = new UniqueEmailException(s);
        assertThat(exception.getMessage(), equalTo(s));
    }
}
