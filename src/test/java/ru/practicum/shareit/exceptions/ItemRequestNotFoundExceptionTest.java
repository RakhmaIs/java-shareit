package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestNotFoundExceptionTest {
    @Test
    void testItemRequestNotFoundException() {
        String s = "ItemRequestNotFoundException";
        ItemRequestNotFoundException exception = new ItemRequestNotFoundException(s);
        assertThat(exception.getMessage(), equalTo(s));
    }
}
