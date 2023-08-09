package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.util.Pagination.getPaginationWithSortDesc;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequestDto createItemRequest(Long requesterId, ItemRequestPostDto itemRequestPostDto) {
        checkUserExists(requesterId);
        ItemRequest itemRequest = fromItemRequestPostToItemRequest(requesterId, itemRequestPostDto);
        log.info("Запрос на вещь успешно создан - requester_id: {}, item_request_id: {}", requesterId, itemRequest.getId());
        return toItemDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> readAllOtherItemRequests(Long requesterId, Integer from, Integer size) {
        checkUserExists(requesterId);
        Pageable pageWithSort = getPaginationWithSortDesc(from, size);
        Page<ItemRequest> itemRequestsOtherUsers = itemRequestRepository
                .findAllByRequesterIdNot(requesterId, pageWithSort);
        List<ItemRequestDto> itemRequestDto = toListItemRequestDto(itemRequestsOtherUsers.stream()
                .collect(Collectors.toList()));
        itemRequestDto.forEach(requestDto -> requestDto.setItems(itemService.findAllItemByRequest(requestDto.getId())));
        log.info("Информация о запросах на вещи от других пользователей успешно получена - requester_id: {}", requesterId);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> readOwnItemRequestsById(Long requesterId) {
        checkUserExists(requesterId);
        List<ItemRequestDto> ownerRequests = toListItemRequestDto(itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requesterId));
        ownerRequests.forEach(requestDto -> requestDto.setItems(itemService.findAllItemByRequest(requestDto.getId())));
        log.info("Информация о собственных запросах на вещи успешно получена - requester_id: {}", requesterId);
        return ownerRequests;
    }

    @Override
    public ItemRequestDto readOneConcreteItemRequest(Long requesterId, Long requestId) {
        checkUserExists(requesterId);
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId)
                .orElseThrow(() -> {
                    log.error("Ошибка получения запроса по request_id = {} в методе readOneConcreteItemRequest", requestId);
                    return new ItemRequestNotFoundException("Запроса с id " + requestId + " не существует");
                });
        ItemRequestDto itemRequestDto = toItemDto(itemRequest);
        itemRequestDto.setItems(itemService.findAllItemByRequest(itemRequest.getId()));
        log.info("Информация об одном конкретном запросе на вещь успешно получена - requester_id: {}, request_id: {} ", requesterId, requesterId);
        return itemRequestDto;
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("Ошибка проверки пользователя в методе checkUserExists класса ItemRequestServiceImpl " +
                    "- Пользователь не найден. requester_id: {}", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}