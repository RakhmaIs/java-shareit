package ru.practicum.shareit.user.repositorytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder()
                .name("test")
                .email("test@email.com")
                .build());

        userRepository.save(User.builder()
                .name("test1")
                .email("test2@email.com")
                .build());
    }

    @Test
    void existsUserByIdShouldReturnTrue() {
        List<User> all = userRepository.findAll();
        assertTrue(userRepository.existsById(all.get(0).getId()));
    }

    @Test
    void existsUserByIdShouldReturnFalseWhenUserNotExists() {
        assertFalse(userRepository.existsById(0L));
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

}
