package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated(Create.class) @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Не удалось выполнить запрос создание пользователя: {} , email: {} не прошёл валидацию или не уникален", userDto, userDto.getEmail());
            return ResponseEntity.badRequest().body(userDto);
        }
        log.info("Получен запрос на добавление пользователя : {}.", userDto);
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> readUsers() {
        log.info("Получен запрос на получение всех пользователей.");
        return new ResponseEntity<>(userService.readUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> readUser(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя по id = " + id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя : {}.", userDto);
        return new ResponseEntity<>(userService.updateUser(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя c айди: {}.", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
