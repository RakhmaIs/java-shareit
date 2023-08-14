package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID;


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
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto,
                                              @RequestHeader(USER_ID) Long id,
                                              @RequestParam(name = "requestId", required = false) Long requestId) {
        log.info("Получен запрос на добавление вещи : {}.", itemDto);
        return new ResponseEntity<>(itemService.createItem(itemDto, id, requestId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> readAllByOwnerId(@RequestHeader(USER_ID) Long userId,
                                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение информации о вещах, владельцем которых является пользователь с id = {}", userId);
        return new ResponseEntity<>(itemService.readItemsOwnedByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> readByUserId(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId) {
        log.info("Получен запрос на получение  вещей пользователя по userID = {}, itemId = {}", userId, itemId);
        return new ResponseEntity<>(itemService.readItemByItemIdAndUserId(itemId, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto, @RequestHeader(USER_ID) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Получен запрос на обновление item : {}.", itemDto);
        return new ResponseEntity<>(itemService.updateItem(itemDto, userId, itemId), HttpStatus.OK);
    }

    @DeleteMapping("/itemId")
    public ResponseEntity<HttpStatus> deleteById(Long id) {
        log.info("Получен запрос на удаление вещи c id : {}.", id);
        itemService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(name = "from", required = false, defaultValue = "0")
                                                Integer from,
                                                @RequestParam(name = "size", required = false, defaultValue = "10")
                                                Integer size) {
        return new ResponseEntity<>(itemService.search(text, from, size), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId,
                                                            @RequestBody CommentRequestDto commentRequestDto) {
        return new ResponseEntity<>(itemService.createComment(userId, itemId, commentRequestDto), HttpStatus.OK);
    }
}
