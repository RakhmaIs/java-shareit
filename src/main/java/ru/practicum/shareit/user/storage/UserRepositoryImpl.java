package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> usersMap = new HashMap<>();
    private Long idGen = 1L;

    @Override
    public User createUser(User user) {
        user.setId(idGen);
        log.info("Пользователь " + user + " успешно добавлен");
        usersMap.put(idGen++, user);
        return user;
    }

    public User updateUser(Long id, User user) {
        if (!usersMap.containsKey(id)) {
            log.warn("Ошибка обновления пользователя");
            throw new UserNotFoundException("User с id = " + id + " не найден. Обновление невозмоно");
        }
        User updatedUser = usersMap.get(id);
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        log.info("Пользователь " + user + " успешно обновлен");
        return updatedUser;
    }


    @Override
    public List<User> readUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User deleteUser(Long id) {
        if (usersMap.containsKey(id)) {
            log.info("Пользователь c id " + id + " успешно обновлен");
            return usersMap.remove(id);
        }
        throw new UserNotFoundException("User с id = " + id + " не найден. Удаление невозмоно");
    }


    @Override
    public User readUser(Long id) {
        if (usersMap.containsKey(id)) {
            return usersMap.get(id);
        }
        throw new UserNotFoundException("User с id = " + id + " не найден. Удаление невозмоно");
    }
}

