package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long itemId);

    List<ItemDto> readAll(Long userId);

    ItemDto readById(Long itemId, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemDto deleteById(Long itemId);

    List<ItemDto> search(String text);

}
