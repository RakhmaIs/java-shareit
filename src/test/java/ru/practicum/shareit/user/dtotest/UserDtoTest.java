package ru.practicum.shareit.user.dtotest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();

        JsonContent<UserDto> write = json.write(userDto);

        assertThat(write).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(write).extractingJsonPathStringValue("$.name").isEqualTo("testName");
        assertThat(write).extractingJsonPathStringValue("$.email").isEqualTo("testEmail@gmail.com");
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String jsonContent = "{\"id\":1,\"name\":\"testName\",\"email\":\"testEmail@gmail.com\"}";
        UserDto testObject = json.parse(jsonContent).getObject();

        assertThat(testObject.getId()).isEqualTo(1L);
        assertThat(testObject.getName()).isEqualTo("testName");
        assertThat(testObject.getEmail()).isEqualTo("testEmail@gmail.com");
    }
}
