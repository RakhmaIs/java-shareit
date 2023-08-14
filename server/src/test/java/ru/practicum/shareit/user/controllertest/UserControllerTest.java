package ru.practicum.shareit.user.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.handler.ErrorsHandler;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    UserDto testUser;
    @MockBean
    ErrorsHandler errorsHandler;
    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        testUser = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();
    }

    @Test
    @SneakyThrows
    void createUserShouldReturnCreatedUserWhenRequestDataIsValidAndStatusShouldBeOk() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@gmail.com")
                .build();

        when(userService.createUser(any())).thenReturn(testUser);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());


    }

   /* @Test*/
 /*   @SneakyThrows
    void createUserShouldThrowBadRequestWhenRequestDataIsNotValid() {
        UserDto userDto = UserDto.builder()
                .email("test@email.com")
                .build();

        when(userService.createUser(any())).thenThrow(HttpClientErrorException.BadRequest.class);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }*/

/*    @Test
    @SneakyThrows
    void createUserShouldThrowBadRequestWhenEmailDataIsNotValid() {
        UserDto userDto = UserDto.builder()
                .email("test")
                .email("invalid.com")
                .build();

        when(userService.createUser(any())).thenThrow(HttpClientErrorException.BadRequest.class);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }*/

/*
    @Test
    @SneakyThrows
    void createUserShouldThrowBadRequestWhenRequestHaveEmptyBody() {
        UserDto userDto = UserDto.builder().build();

        when(userService.createUser(userDto)).thenThrow(HttpClientErrorException.BadRequest.class);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
*/

    @Test
    @SneakyThrows
    void readUserShouldReturnValidValuesAndStatusOk() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andDo(print());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @SneakyThrows
    void readUsersShouldReturnListOfUsersAndStatusOk() {
        when(userService.readUsers()).thenReturn(getListOfTestUsers());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(getListOfTestUsers())))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void readUsersShouldReturnListOfUsersWhenUserIsEmpty() {
        when(userService.readUsers()).thenReturn(getEmptyTestUser());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(getEmptyTestUser())))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUserShouldReturnValidResponseAndStatusOkWhenRequestDataIsValid() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("update")
                .email("update@gmail.com")
                .build();

        when(userService.updateUser(eq(1L), eq(userDto))).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("update"))
                .andExpect(jsonPath("$.email").value("update@gmail.com"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUserShouldReturnNotFoundWhenRequestDataHasInvalidUserId() {
        UserDto wrongId = UserDto.builder()
                .id(0L)
                .build();
        when(userService.updateUser(eq(0L), any())).thenThrow(UserNotFoundException.class);

        String jsonUser = objectMapper.writeValueAsString(wrongId);

        mvc.perform(patch("/users/0")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @SneakyThrows
    void updateUserWhenUpdateEmailPartShouldReturnValidResponseAndStatusOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testUpdate@email.com")
                .build();

        when(userService.updateUser(eq(1L), eq(userDto))).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUserWhenUpdateNamePartShouldReturnValidResponseAndStatusOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUpdate")
                .email("test@email.com")
                .build();

        when(userService.updateUser(eq(1L), eq(userDto))).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteUserShouldReturnStatusOk() {
        mvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }


    @Test
    @SneakyThrows
    @DisplayName("Проверка контроллера")
    void getUser_whenInvokeIncorrectId_thenStatusNotFound() {
        when(userService.getUserById(-1L))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(get("/users/-1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private static List<UserDto> getEmptyTestUser() {
        return List.of(UserDto.builder().build());
    }

    private static List<UserDto> getListOfTestUsers() {
        return List.of(UserDto.builder()
                        .id(2L)
                        .name("second")
                        .email("mail2@gmail.com")
                        .build(),
                UserDto.builder()
                        .id(3L)
                        .name("third")
                        .email("mail3@gmail.com")
                        .build());
    }

    private static UserDto getTestUser() {
        return UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();
    }

}
