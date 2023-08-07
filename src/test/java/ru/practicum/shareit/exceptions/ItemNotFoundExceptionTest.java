package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemNotFoundExceptionTest {
    @Test
    void testItemNotFoundException() {
        String s = "ItemNotFoundException";
        ItemNotFoundException itemNotFoundException = new ItemNotFoundException(s);
        assertThat(itemNotFoundException.getMessage(), equalTo(s));
    }
}
