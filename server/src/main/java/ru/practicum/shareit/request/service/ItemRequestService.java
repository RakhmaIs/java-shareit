package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long requesterId, ItemRequestPostDto itemRequestPostDto);

    List<ItemRequestDto> readAllOtherItemRequests(Long requesterId, Integer from, Integer size);

    List<ItemRequestDto> readOwnItemRequestsById(Long requesterId);

    ItemRequestDto readOneConcreteItemRequest(Long requesterId, Long requestId);

}
