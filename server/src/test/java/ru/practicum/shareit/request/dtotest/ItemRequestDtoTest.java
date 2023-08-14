package ru.practicum.shareit.request.dtotest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(created)
                .items(List.of(ItemDto.builder().owner(User.builder()
                                .id(1L)
                                .name("name")
                                .email("email@mail.ru")
                                .build())
                        .id(1L)
                        .name("name")
                        .description("description")
                        .available(true)
                        .requestId(1L)
                        .build()))
                .description("description")
                .build();

        String formatCreated = created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(formatCreated);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String jsonContent = "{\"id\":1,\"description\":\"description\",\"created\":\"2023-07-04T03:48:17\"," +
                "\"items\":[{\"id\":1,\"name\":\"name\",\"description\":\"description\"," +
                "\"available\":true,\"owner\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"}," +
                "\"requestId\":1,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null}]}";

        ItemRequestDto object = json.parse(jsonContent).getObject();

        assertThat("name").isEqualTo(object.getItems().get(0).getName());
        assertThat("description").isEqualTo(object.getDescription());
        assertThat(LocalDateTime.parse("2023-07-04T03:48:17",
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).isEqualTo(object.getCreated());
        assertThat(1L).isEqualTo(object.getId());
        assertThat(1L).isEqualTo(object.getItems().get(0).getId());
        assertThat("name").isEqualTo(object.getItems().get(0).getName());
        assertThat("description").isEqualTo(object.getItems().get(0).getDescription());
        assertThat(true).isEqualTo(object.getItems().get(0).getAvailable());
        assertThat(1L).isEqualTo(object.getItems().get(0).getRequestId());
    }
}

