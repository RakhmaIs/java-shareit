package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    List<UserDto> readUsers();

    void deleteUser(Long id);

    UserDto getUserById(Long id);
}
