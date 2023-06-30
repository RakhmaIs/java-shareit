package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item, Long id);

    List<Item> readAll(Long userId);

    Item readById(Long itemId, Long userId);

    Item updateItem(Item item, Long userId, Long itemId);

    Item deleteById(Long id);

    List<Item> search(String text);
}
