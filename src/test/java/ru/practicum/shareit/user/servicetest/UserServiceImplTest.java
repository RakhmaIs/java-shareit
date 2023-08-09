package ru.practicum.shareit.user.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    UserService userService;

    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();
    }

    @Test
    @DisplayName("createUser - должен сохранять пользвателя в базу")
    void createUserShouldSaveWhenRequestHasValidData() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userSave = userService.createUser(userDto);

        assertEquals(user.getEmail(), userSave.getEmail());
        assertEquals(user.getName(), userSave.getName());

    }

    @Test
    @DisplayName("createUser - должен выбрасывать AlreadyExistsException")
    public void createUserShouldThrowAlreadyExistException() {
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(AlreadyExistException.class, () -> userService.createUser(userDto));
    }


    @Test
    @DisplayName("updateUser - должен обновить email и name ")
    void updateUserShouldUpdateNameAndEmailWhenRequestHasDataToUpdateNameAndEmail() {
        User testUpdate = User.builder()
                .id(1L)
                .name("update")
                .email("update@gmail.com")
                .build();

        when(userRepository.saveAndFlush(any())).thenReturn(testUpdate);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userSave = userService.updateUser(user.getId(), toUserDto(testUpdate));

        assertEquals(testUpdate.getEmail(), userSave.getEmail());
        assertEquals(testUpdate.getName(), userSave.getName());
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    @DisplayName("updateUser - должен выбрасывать UserNotFoundException если пользователь не найден")
    void updateUserShouldThrowUserNotFoundExceptionWhenUserNotFound() {
        User userUpdate = User.builder()
                .id(1L)
                .name("update")
                .email("update@gmail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userUpdate.getId(), toUserDto(userUpdate)));

        assertEquals("Невозможно обновить данные пользователя с id =  1 - пользователь с таким id не найден в базе.", userNotFoundException.getMessage());
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).findById(userUpdate.getId());
    }

    @Test
    @DisplayName("updateUser - должен выбрасывать AlreadyExistException")
    public void updateUser_ThrowsAlreadyExistException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(userDto.getId(), userDto));
        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("findById - должен возвращать правильного пользователя")
    void findByIdShouldReturnValidUserWhenRequestDataIsValid() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(user.getId());

        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("findById - должен выбрасывать UserNotFoundException")
    void findByIdShouldThrowUserNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(user.getId()));

        assertEquals("Пользователь с id 1 не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("deleteUser - должен удалять из базы")
    void deleteUserShouldDelete() {
        when(userRepository.existsById(any())).thenReturn(true);

        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).existsById(user.getId());
    }

    @Test
    @DisplayName("deleteUser - должен выбрасывать UserNotFoundException")
    void deleteUserShouldThrowUserNotFoundExceptionWhenUserNotExists() {
        when(userRepository.existsById(eq(user.getId()))).thenReturn(false);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(user.getId()));

        assertEquals("Пользователь с id 1 не найден. Удаление невозможно.", userNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).deleteById(user.getId());
    }

    @Test
    @DisplayName("readUser - должен возвращать список пользователей")
    void readUsersShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.readUsers();

        assertEquals(1, userDtoList.size());
    }

    @Test
    @DisplayName("readAll - размер возвращаемого листа должен быть 0")
    void readAllListSizeShouldBe0WhenUsersListIsEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> userDtoList = userService.readUsers();

        assertEquals(0, userDtoList.size());
    }
}
