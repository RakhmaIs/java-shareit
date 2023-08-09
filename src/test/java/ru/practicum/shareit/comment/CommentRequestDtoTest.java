package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentRequestDtoTest {
    @Autowired
    JacksonTester<CommentRequestDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text")
                .build();

        JsonContent<CommentRequestDto> write = json.write(commentRequestDto);

        assertThat(write).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"text\":\"text\"}";
        CommentRequestDto object = json.parse(jsonContent).getObject();

        assertThat("text").isEqualTo(object.getText());
    }
}