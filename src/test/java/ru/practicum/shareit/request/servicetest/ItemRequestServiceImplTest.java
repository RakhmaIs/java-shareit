package ru.practicum.shareit.request.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    static long ID_FOR_CORRECT_TEST = 1L;
    ItemRequestService itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemService itemService;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;
    ItemRequestPostDto itemRequestPostDto;

    Pageable pageable = Pagination.getPaginationWithSortDesc(0, 2);

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemService);
        itemRequestDto = ItemRequestDto.builder()
                .items(List.of())
                .id(ID_FOR_CORRECT_TEST)
                .description("test")
                .created(LocalDateTime.now())
                .build();
        itemRequestPostDto = ItemRequestPostDto.builder()
                .description("test")
                .build();

        itemRequest = ItemRequest.builder()
                .id(ID_FOR_CORRECT_TEST)
                .description("test")
                .created(LocalDateTime.now())
                .requesterId(1L)
                .build();

    }

    @Test
    void readOwnItemRequestsByIdWhenInvokedWithValidDataShouldReturnListOfOneRequest() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(eq(ID_FOR_CORRECT_TEST)))
                .thenReturn(List.of(itemRequest));
        when(itemService.findAllItemByRequest(eq(ID_FOR_CORRECT_TEST))).thenReturn(List.of());


        List<ItemRequestDto> itemRequests = itemRequestService.readOwnItemRequestsById(ID_FOR_CORRECT_TEST);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void readOwnItemRequestsByIdWhenInvokedWithEmptyListShouldReturnEmptyList() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(eq(ID_FOR_CORRECT_TEST)))
                .thenReturn(List.of());


        List<ItemRequestDto> itemRequests = itemRequestService.readOwnItemRequestsById(ID_FOR_CORRECT_TEST);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void readOwnItemRequestsByIdWhenUserNotExistShouldThrowUserNotFoundException() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(false);


        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.readOwnItemRequestsById(ID_FOR_CORRECT_TEST));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, never())
                .findAllByRequesterIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void findItemRequestByIdWhenInvokedWithValidDataShouldReturnRequest() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findItemRequestById(ID_FOR_CORRECT_TEST)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDto = itemRequestService.readOneConcreteItemRequest(ID_FOR_CORRECT_TEST, ID_FOR_CORRECT_TEST);

        assertEquals(itemRequestDto, itemRequestDto);
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).findItemRequestById(ID_FOR_CORRECT_TEST);
    }

    @Test
    void readOneConcreteItemRequestWhenItemNotExistShouldThrowItemRequestNotFoundException() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findItemRequestById(ID_FOR_CORRECT_TEST)).thenReturn(Optional.empty());

        ItemRequestNotFoundException itemRequestNotFoundException = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.readOneConcreteItemRequest(ID_FOR_CORRECT_TEST, ID_FOR_CORRECT_TEST));

        assertEquals("Запроса с id 1 не существует", itemRequestNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).findItemRequestById(ID_FOR_CORRECT_TEST);
        verify(itemService, times(0)).findAllItemByRequest(ID_FOR_CORRECT_TEST);
    }

    @Test
    void readAllOtherItemRequestsWhenInvokedWithValidDataShouldReturnListOfOne() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(ID_FOR_CORRECT_TEST, pageable))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestDto> itemRequests = itemRequestService.readAllOtherItemRequests(ID_FOR_CORRECT_TEST, 0, 2);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(ID_FOR_CORRECT_TEST, pageable);
    }

    @Test
    void readAllOtherItemRequestsWhenInvokedShouldReturnEmptyList() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(ID_FOR_CORRECT_TEST, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemRequestDto> itemRequests = itemRequestService.readAllOtherItemRequests(ID_FOR_CORRECT_TEST, 0, 2);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(ID_FOR_CORRECT_TEST, pageable);
    }

    @Test
    void createItemRequestWhenInvokedShouldReturnSavedRequest() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto requestSave = itemRequestService.createItemRequest(ID_FOR_CORRECT_TEST, itemRequestPostDto);

        assertEquals(itemRequestDto.getId(), requestSave.getId());
        assertEquals(itemRequestDto.getCreated().getSecond(), requestSave.getCreated().getSecond());
        assertEquals(itemRequestDto.getDescription(), requestSave.getDescription());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }
}
