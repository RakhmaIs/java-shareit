package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        userRepository.readUser(userId);
        if (itemDto.getAvailable() == null || !itemDto.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя создать недоступную вещь");
        }
        return ItemMapper.toItemDto(itemRepository.createItem(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public List<ItemDto> readAll(Long userId) {
        return ItemMapper.toItemListDto(itemRepository.readAll(userId));
    }

    @Override
    public ItemDto readById(Long itemId, Long userId) {
        return ItemMapper.toItemDto(itemRepository.readById(itemId, userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(itemDto), userId, itemId));
    }

    @Override
    public ItemDto deleteById(Long id) {
        return ItemMapper.toItemDto(itemRepository.deleteById(id));
    }

    @Override
    public List<ItemDto> search(String text) {
        return ItemMapper.toItemListDto(itemRepository.search(text));
    }
}
