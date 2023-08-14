package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toItemDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest fromItemRequestPostToItemRequest(Long userId, ItemRequestPostDto itemRequestPostDto) {
        return ItemRequest.builder()
                .description(itemRequestPostDto.getDescription())
                .created(LocalDateTime.now())
                .requesterId(userId)
                .build();
    }

    public static List<ItemRequestDto> toListItemRequestDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
