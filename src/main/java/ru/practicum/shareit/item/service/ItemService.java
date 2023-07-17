package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    List<ItemResponseDto> readItemsOwnedByUserId(Long userId);

    ItemResponseDto readItemByItemIdAndUserId(Long itemId, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    void deleteById(Long itemId);

    List<ItemDto> search(String text);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);

}
