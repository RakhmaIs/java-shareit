package ru.practicum.shareit.item.dtotest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.util.Constants.DATE_TIME_FORMATTER;


@JsonTest
class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String formatCreated = created.format(DATE_TIME_FORMATTER);
        String formatEnd = end.format(DATE_TIME_FORMATTER);
        ItemDto build = ItemDto.builder()
                .id(1L)
                .name("name")
                .owner(User.builder()
                        .id(1L)
                        .name("name")
                        .email("email@mail.ru")
                        .build())
                .description("description")
                .comments(List.of(CommentResponseDto.builder()
                        .id(1L)
                        .authorName("author")
                        .created(created)
                        .text("text")
                        .build()))
                .available(true)
                .requestId(1L)
                .lastBooking(BookingResponseDto.builder()
                        .id(2L)
                        .bookerId(5L)
                        .end(end)
                        .item(Item.builder().build())
                        .bookingStatus(BookingStatus.WAITING)
                        .start(created)
                        .build())
                .nextBooking(BookingResponseDto.builder()
                        .id(1L)
                        .bookerId(6L)
                        .end(end)
                        .item(Item.builder().build())
                        .bookingStatus(BookingStatus.WAITING)
                        .start(created)
                        .build())
                .build();

        JsonContent<ItemDto> result = json.write(build);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.bookingStatus").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.bookingStatus").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo(formatCreated);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo(formatCreated);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo(formatEnd);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo(formatEnd);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"id\":1,\"name\":\"name\",\"description\":\"description\",\"available\":true," +
                "\"owner\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"},\"requestId\":1," +
                "\"lastBooking\":{\"id\":1,\"start\":\"2023-07-04T04:16:26\"," +
                "\"end\":\"2023-07-04T05:16:26\",\"item\":{\"id\":null,\"name\":null," +
                "\"description\":null,\"available\":null,\"owner\":null,\"requestId\":null,\"lastBooking\":null," +
                "\"nextBooking\":null,\"comments\":null},\"booker\":{\"id\":1,\"name\":\"name\"," +
                "\"email\":\"email@mail.ru\"},\"bookingStatus\":\"WAITING\"},\"nextBooking\":{\"id\":1," +
                "\"start\":\"2023-07-04T04:16:26\",\"end\":\"2023-07-04T05:16:26\"," +
                "\"item\":{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"owner\":null," +
                "\"requestId\":null,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null}," +
                "\"booker\":{\"id\":2,\"name\":\"name\",\"email\":\"email@mail.ru\"},\"bookingStatus\":\"WAITING\"}," +
                "\"comments\":[{\"id\":1,\"text\":\"text\",\"authorName\":\"author\"," +
                "\"created\":\"2023-07-04T04:16:26\"}]}";

        ItemDto itemDto = json.parse(jsonContent).getObject();

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(1);
        assertThat(itemDto.getLastBooking().getStart()).isEqualTo("2023-07-04T04:16:26");
        assertThat(itemDto.getNextBooking().getStart()).isEqualTo("2023-07-04T04:16:26");
        assertThat(itemDto.getLastBooking().getEnd()).isEqualTo("2023-07-04T05:16:26");
        assertThat(itemDto.getNextBooking().getEnd()).isEqualTo("2023-07-04T05:16:26");
        assertThat(itemDto.getLastBooking().getBookingStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(itemDto.getNextBooking().getBookingStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("text");
        assertThat(itemDto.getComments().get(0).getAuthorName()).isEqualTo("author");
        assertThat(itemDto.getComments().get(0).getCreated()).isEqualTo("2023-07-04T04:16:26");
    }
}