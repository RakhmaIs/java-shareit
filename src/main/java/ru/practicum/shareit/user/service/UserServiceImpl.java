package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepositoryImpl;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (checkEmail(userDto.getEmail(), userDto.getId())) {
            throw new DuplicateEmailException("Обнаружен дубликат email создание пользователя невозможно");
        }
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public List<UserDto> readUsers() {
        return UserMapper.toListUsersDto(userRepository.readUsers());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        if (checkEmail(userDto.getEmail(), id)) {
            throw new DuplicateEmailException("Обнаружен дубликат email создание пользователя невозможно");
        }
        return UserMapper.toUserDto(userRepository.updateUser(id, UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto deleteUser(Long id) {
        return UserMapper.toUserDto(userRepository.deleteUser(id));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userRepository.readUser(id));
    }

    private boolean checkEmail(String email, Long userId) {
        return userRepository.readUsers().stream().filter(user -> !user.getId().equals(userId)).anyMatch(user -> user.getEmail().equals(email));
    }
}
