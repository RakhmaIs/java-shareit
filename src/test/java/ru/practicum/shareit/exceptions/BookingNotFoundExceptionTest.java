package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingNotFoundExceptionTest {
    @Test
    void testBookingNotFoundException() {
        String s = "BookingNotFoundException";
        BookingNotFoundException exception = new BookingNotFoundException(s);
        assertThat(exception.getMessage(), equalTo(s));
    }
}
