package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("Получен запрос на добавление пользователя : {}.", itemDto);
        return new ResponseEntity<>(itemService.createItem(itemDto, id), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех пользователей.");
        return new ResponseEntity<>(itemService.readAll(userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> readById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение пользователя по id = " + itemId);
        return new ResponseEntity<>(itemService.readById(itemId, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на обновление item : {}.", itemDto);
        return new ResponseEntity<>(itemService.updateItem(itemDto, userId, itemId), HttpStatus.OK);
    }

    @DeleteMapping("/itemId")
    public ResponseEntity<ItemDto> deleteById(Long id) {
        log.info("Получен запрос на удаление пользователя c айди: {}.", id);
        return new ResponseEntity<>(itemService.deleteById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.search(text), HttpStatus.OK);
    }
}
