package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PaginationInvalidParamExceptionTest {
    @Test
    void testItemRequestNotFoundException() {
        String s = "PaginationInvalidParamException";
        PaginationInvalidParamException exception = new PaginationInvalidParamException(s);
        assertThat(exception.getMessage(), equalTo(s));
    }
}
