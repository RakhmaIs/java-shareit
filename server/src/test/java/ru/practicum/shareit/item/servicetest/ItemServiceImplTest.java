
package ru.practicum.shareit.item.servicetest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotAvailableException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    static long ID = 1L;
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    Item item;
    ItemDto itemDto;
    ItemResponseDto itemResponseDto;
    User user;
    Comment comment;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

        item = Item.builder()
                .id(ID)
                .owner(User.builder().id(ID).build())
                .name("test")
                .requestId(ID)
                .description("test")
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .name("test")
                .comments(List.of())
                .owner(User.builder().build())
                .requestId(ID)
                .description("test")
                .available(true)
                .lastBooking(BookingResponseDto.builder().id(ID).build())
                .nextBooking(BookingResponseDto.builder().id(2L).build())
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(ID)
                .name("test")
                .comments(List.of())
                .requestId(ID)
                .description("test")
                .available(true)
                .lastBooking(BookingResponseDto.builder().id(ID).build())
                .nextBooking(BookingResponseDto.builder().id(2L).build())
                .build();


        user = User.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();

        comment = Comment.builder()
                .id(ID)
                .author(user)
                .item(item)
                .text("test")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createItem - валидные данные - должен сохранять пользователя")
    void createItemShouldSaveItem() {
        when(userRepository.findById(eq(ID))).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.createItem(itemDto, ID, ID);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertEquals(itemResponseDto.getRequestId(), result.getRequestId());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createItem - user не найден - выбрасывает UserNotFoundException")
    void createItemWhenUserNotExistShouldThrowUserNotFoundException() {
        when(userRepository.findById(eq(ID))).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(itemDto, ID, ID));

        assertEquals("Пользователь с id 1 не найден. Невозможно создать вещь", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateItem - валидные данные - должен обвновить вещь")
    void updateItemShouldUpdate() {
        Item updatedItem = Item.builder()
                .id(ID)
                .name("updated")
                .available(false)
                .description("updated")
                .requestId(ID)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(ID)
                .name("updated")
                .description("updated")
                .available(false)
                .requestId(ID)
                .build();
        when(itemRepository.findById(eq(ID))).thenReturn(Optional.of(item));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);
        when(userRepository.existsById(eq(ID))).thenReturn(true);

        ItemDto result = itemService.updateItem(updatedItemDto, ID, ID);

        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());
        assertEquals(updatedItemDto.getRequestId(), result.getRequestId());
        verify(itemRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).save(any());
        verify(userRepository, times(1)).existsById(any());
    }


    @Test
    @DisplayName("updateItem - если не владелец вещи - выбрасывает ItemNotFoundException")
    void updateItemShouldThrowItemNotFoundExceptionWhenUserNotItemOwner() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(ID)
                .name("updated")
                .description("updated")
                .available(true)
                .build();

        when(itemRepository.findById(eq(ID))).thenReturn(Optional.of(item));
        when(userRepository.existsById(eq(2L))).thenReturn(true);

        ItemNotFoundException exc = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(updatedItemDto, 2L, ID));

        assertEquals("Вещь не найдена", exc.getMessage());
        verify(itemRepository, times(1)).findById(ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateItem - id пользователя не существует - бросает UserNotFoundException")
    void updateItemShouldThrowUserNotFoundExceptionWhenUserNotExist() {
        when(userRepository.existsById(ID)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                itemService.updateItem(itemDto, ID, ID));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRepository, times(0)).findById(ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateItem - id вещи не существует - бросает ItemNotFoundException")
    void updateItemShouldThrowItemNotFoundExceptionWhenItemNotExist() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.updateItem(itemDto, ID, ID));

        assertEquals("Вещь с id = " + ID + " не найдена", exception.getMessage());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("readItemByItemIdAndUserId - id не существует - выбрасывает ItemNotFoundException")
    void readItemByItemIdAndUserIdWhenItemNotExistShouldThrowItemNotFoundException() {
        when(itemRepository.findById(ID)).thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class, () ->
                itemService.readItemByItemIdAndUserId(ID, ID));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).findById(ID);
        verify(commentRepository, times(0)).findAllByItemIdOrderByCreatedDesc(eq(ID));
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                eq(ID), any(), any(), any());
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                eq(ID), any(), any(), any());
    }


    @Test
    @DisplayName("readItemByItemIdAndUserId- валидный id - должен вернуть вещь")
    void readItemByItemIdAndUserIdShouldReturnItem() {
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(eq(ID))).thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(eq(ID), any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(eq(ID), any(), any(), any()))
                .thenReturn(List.of());

        ItemResponseDto result = itemService.readItemByItemIdAndUserId(ID, ID);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        verify(itemRepository, times(1)).findById(ID);
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(eq(ID));
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                eq(ID), any(), any(), any());
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                eq(ID), any(), any(), any());
    }

    @Test
    @DisplayName("readItemByItemIdAndUserId- user not owner - return item without bookings")
    void readItemByItemIdAndUserIdShouldReturnItemResponseDtoWhenUserNotOwner() {
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(eq(ID))).thenReturn(List.of());

        ItemResponseDto result = itemService.readItemByItemIdAndUserId(ID, 5L);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertNotEquals(itemResponseDto.getLastBooking(), result.getLastBooking());
        assertNotEquals(itemResponseDto.getNextBooking(), result.getNextBooking());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());


        verify(itemRepository, times(1)).findById(ID);
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(eq(ID));
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                eq(ID), any(), any(), any());
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                eq(ID), any(), any(), any());
    }

    @Test
    @DisplayName("deleteItem - валидный id - удаление вещи")
    void deleteItemShouldDeleteItem() {
        when(itemRepository.existsById(ID)).thenReturn(true);

        itemService.deleteById(ID);

        verify(itemRepository, times(1)).existsById(ID);
        verify(itemRepository, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("deleteItem - id не существует - выбрасывает ItemNotFoundException")
    void deleteItemShouldThrowItemNotFoundExceptionWhenItemNotExist() {
        when(itemRepository.existsById(ID)).thenReturn(false);

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
                () -> itemService.deleteById(ID));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).existsById(ID);
        verify(itemRepository, times(0)).deleteById(ID);
    }

    @Test
    @DisplayName("readItemsOwnedByUserId - валидный id - возвращает список вещей")
    void readItemsOwnedByUserIdShouldReturnListOfItem() {
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(eq(ID), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(eq(ID))).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(ID), any()))
                .thenReturn(Booking.builder().id(1L).booker(user).build());
        when(bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(eq(ID), any()))
                .thenReturn(Booking.builder().id(2L).booker(user).build());
        when(userRepository.existsById(any())).thenReturn(true);

        List<ItemResponseDto> result = itemService.readItemsOwnedByUserId(ID, 0, 2);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getLastBooking().getId(), 1L);
        assertEquals(result.get(0).getNextBooking().getId(), 2L);
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderByIdAsc(eq(ID), any());
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(eq(ID));
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(ID), any());
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(ID), any());
    }


    @Test
    @DisplayName("readItemsOwnedByUserId - id не существует - выбрасывает UserNotFoundException ")
    void readItemsOwnedByUserIdWhenUserNotExistShouldThrowUserNotFoundException() {
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(eq(ID), any())).thenReturn(Page.empty());
        when(userRepository.existsById(any())).thenReturn(false).thenThrow(UserNotFoundException.class);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.readItemsOwnedByUserId(ID, 0, 2));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderByIdAsc(eq(ID), any());
        verify(commentRepository, times(0)).findAllByItemIdOrderByCreatedDesc(eq(ID));
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(ID), any());
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(ID), any());
    }


    @Test
    @DisplayName("search - валидные данные - возвращает список вещей")
    void searchShouldReturnListOfItem() {
        when(itemRepository.search(anyString(), any())).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> result = itemService.search("test", 0, 2);

        assertEquals(1, result.size());
        verify(itemRepository, times(1)).search(anyString(), any());
    }

    @Test
    @DisplayName("search - пустой текст - возвращает пустой список вещей")
    void searchWhenTextIsEmptyShouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("", 0, 2);

        assertEquals(0, result.size());
        verify(itemRepository, times(0)).search(eq(""), any());
    }

    @Test
    @DisplayName("search - пробелы - возвращает пустой список вещей")
    void searchWhenTextIsBlankShouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("  ", 0, 2);

        assertEquals(0, result.size());
        verify(itemRepository, times(0)).search(eq("  "), any());
    }

    @Test
    @DisplayName("createComment - валидные данные - сохранение комментария")
    void createCommentShouldSaveComment() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto test = itemService.createComment(ID, ID,
                CommentRequestDto.builder().text("test").build());

        assertEquals(comment.getText(), test.getText());
        assertEquals(comment.getAuthor().getName(), test.getAuthorName());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createComment - id пользователя не существует - выбрасывает UserNotFoundException")
    void createCommentShouldThrowUserNotFoundExceptionWhenUserNotExist() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(0)).findById(ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - id вещи не существует - выбрасывает ItemNotFoundException")
    void createCommentShouldThrowItemNotFoundExceptionWhenItemNotExist() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - ошибка в параметрах букинга - выбрасывает BookingNotAvailableException")
    void createCommentShouldThrowBookingNotAvailableExceptionWhenItemNotFound() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of(booking));

        BookingNotAvailableException bookingNotAvailableException = assertThrows(BookingNotAvailableException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Бронирование не подтверждено, не завершено или отклонено. Невозможно оставить комментарий.", bookingNotAvailableException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - booking end раньше start - выбрасывает BookingNotAvailableException")
    void createCommentShouldThrowBookingNotAvailableExceptionWhenBookingEndIsAfterStart() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusDays(4))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of(booking));

        BookingNotAvailableException bookingNotAvailableException = assertThrows(BookingNotAvailableException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Бронирование не подтверждено, не завершено или отклонено. Невозможно оставить комментарий.", bookingNotAvailableException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - статус Approved - выбрасывает BookingNotAvailableException")
    void createCommentShouldThrowBookingNotAvailableExceptionWhenBookingStatusIsApproved() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of(booking));

        BookingNotAvailableException bookingNotAvailableException = assertThrows(BookingNotAvailableException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Бронирование не подтверждено, не завершено или отклонено. Невозможно оставить комментарий.", bookingNotAvailableException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - статус Canceled - выбрасывает BookingNotAvailableException")
    void createCommentShouldThrowBookingNotAvailableExceptionWhenBookingStatusIsCanceled() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.CANCELED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of(booking));

        BookingNotAvailableException bookingNotAvailableException = assertThrows(BookingNotAvailableException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Бронирование не подтверждено, не завершено или отклонено. Невозможно оставить комментарий.", bookingNotAvailableException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createComment - статус Canceled - выбрасывает BookingNotAvailableException")
    void createCommentShouldThrowBookingNotAvailableExceptionWhenUserBookingsIsEmpty() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .bookingStatus(BookingStatus.CANCELED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID))
                .thenReturn(List.of());

        BookingNotAvailableException bookingNotAvailableException = assertThrows(BookingNotAvailableException.class,
                () -> itemService.createComment(ID, ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Бронирований у данного пользователя не найдено", bookingNotAvailableException.getMessage());
        verify(userRepository, times(1)).findById(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(ID, ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("findAllItemByRequest - валидные данные - возвращает список вещей")
    void findAllItemByRequestShouldReturnListOfItem() {
        when(itemRepository.findAllByRequestId(ID)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.findAllItemByRequest(ID);

        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAllByRequestId(ID);
    }

    @Test
    @DisplayName("findAllItemByRequest - id request не существует - возвращает пустой список")
    void findAllItemByRequestShouldReturnEmptyListWhenRequestNotExist() {
        when(itemRepository.findAllByRequestId(ID)).thenReturn(List.of());

        List<ItemDto> result = itemService.findAllItemByRequest(ID);

        assertEquals(0, result.size());
        verify(itemRepository, times(1)).findAllByRequestId(ID);
    }
}
