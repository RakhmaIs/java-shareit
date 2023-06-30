package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemsMap = new HashMap<>();
    private Long idGen = 1L;

    @Override
    public Item createItem(Item item, Long userId) {
        item.setId(idGen);
        item.setOwnerId(userId);
        log.info("Получен и успешно выполнен запрос на добавление вещи с id = {} ", item);
        itemsMap.put(idGen++, item);
        return item;
    }

    @Override
    public List<Item> readAll(Long userId) {
        return itemsMap.values().stream().filter(item -> Objects.equals(item.getOwnerId(), userId)).collect(Collectors.toList());
    }

    @Override
    public Item readById(Long itemId, Long userId) {
        if (itemsMap.containsKey(itemId)) {
            log.info("Получен и успешно выполнен запрос на получение вещи с id = {} ", itemId);
            return itemsMap.get(itemId);
        }
        log.warn("Получен запрос на получение вещи с id = {}, но такой вещи не найдено", itemId);
        throw new ItemNotFoundException("Вещь не найдена");
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        Item oldItem = itemsMap.get(itemId);

        if (itemsMap.containsKey(itemId)) {
            if (!Objects.equals(oldItem.getOwnerId(), userId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserId в Item не совпадает с приходящим");
            }
            if (item.getName() != null && !item.getName().isBlank()) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            log.info("Получен и успешно выполнен запрос на обновление вещи : {} ", oldItem);
            return oldItem;
        }
        log.warn("Получен запрос обновление вещи с id = {}, но такой вещи не найдено", itemId);
        throw new ItemNotFoundException("Вещь не найдена");
    }


    @Override
    public Item deleteById(Long id) {
        if (itemsMap.containsKey(id)) {
            log.info("Получен и успешно выполнен запрос на удаление вещи с id = {} ", id);
            return itemsMap.remove(id);
        }
        log.warn("Получен запрос удаление вещи с id = {}, но такой вещи не найдено", id);
        throw new ItemNotFoundException("Вещь не найдена");
    }


    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemsMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase()
                        .contains(text.toLowerCase()) || item.getName().toLowerCase()
                        .contains(text.toLowerCase())).collect(Collectors.toList());
    }
}


