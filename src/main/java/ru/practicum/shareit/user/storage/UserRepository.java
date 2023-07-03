package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User updateUser(Long id, User user);

    List<User> readUsers();

    User deleteUser(Long id);

    public User readUser(Long id);

}
