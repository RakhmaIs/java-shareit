package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserMapper {
    private UserMapper() {

    }

    public static UserDto toUserDto(User user) {
        return user != null ?
                UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build() : null;

    }

    public static User toUser(UserDto userDto) {
        return userDto != null ?
                User.builder()
                        .id(userDto.getId())
                        .name(userDto.getName())
                        .email(userDto.getEmail())
                        .build() : null;
    }

    public static List<UserDto> ToListUsersDto(Collection<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }
}
