package ru.practicum.shareit.storagetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UserRepositoryImplTest {

    UserRepositoryImpl userRepository;
    User user;
    User user2;
    User userForUpdate;

    @BeforeEach
    void setup() {

        userRepository = new UserRepositoryImpl();

        user = User.builder()
                .name("TestUser")
                .email("testuser@mail.ru")
                .id(1L)
                .build();

        user2 = User.builder()
                .name("SecondTestUser")
                .email("secondtestuser@mail.ru")
                .id(2L)
                .build();

        userForUpdate = User.builder()
                .name("UpdatedUser")
                .email("updated@mail.ru")
                .id(1L)
                .build();
    }

    @Test
    void createUserShouldAddUserInMap() {
        userRepository.createUser(user);
        assertThat(userRepository.readUsers().size()).isEqualTo(1);
    }

    @Test
    void updateUserShouldUpdateUserInMap() {
        userRepository.createUser(user);
        assertThat(userRepository.readUser(user.getId()).getName()).isEqualTo("TestUser");
        userRepository.updateUser(user.getId(), userForUpdate);
        assertThat(userRepository.readUser(userForUpdate.getId()).getName()).isEqualTo("UpdatedUser");
    }

    @Test
    void readUsersShouldReturnListOfUsers() {
        userRepository.createUser(user);
        userRepository.createUser(user2);
        assertThat(userRepository.readUsers().size()).isEqualTo(2);
    }

    @Test
    void deleteUserShouldDeleteUserFromMap() {
        userRepository.createUser(user);
        assertThat(userRepository.readUsers().size()).isEqualTo(1);
        userRepository.deleteUser(user.getId());
        assertThat(userRepository.readUsers().size()).isEqualTo(0);
    }
}
