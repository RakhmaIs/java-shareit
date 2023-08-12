package ru.practicum.shareit.booking.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotAvailableException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    static long TEST_ID = 1L;
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    BookingDto bookingDto;
    Booking booking;
    Item item;
    User user;
    BookingRequestDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder().id(TEST_ID).build();
        item = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(user)
                .build();
        bookingDto = BookingDto.builder()
                .id(TEST_ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();
        booking = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        bookingCreateDto = BookingRequestDto.builder()
                .itemId(TEST_ID)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId - valid data - return booking")
    void getBookingByIdAndBookerIdWhenValidDataShouldReturnBooking() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.findById(TEST_ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(item));

        BookingDto result = bookingService.getByIdAndBookerId(TEST_ID, user.getId());

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId - item not found - throw ItemNotFoundException")
    void getBookingByIdAndBookerIdWhenItemNotFoundShouldThrowItemNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.findById(TEST_ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.getByIdAndBookerId(TEST_ID, user.getId()));

        assertEquals("Вещь не найдена", exception.getMessage());
        verify(bookingRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId- booking not found - throw BookingNotFoundException")
    void getBookingByIdAndBookerIdWhenBookingNotFoundShouldThrowBookingNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getByIdAndBookerId(TEST_ID, user.getId()));

        assertEquals("Бронирование не найдено", exception.getMessage());
        verify(bookingRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(0)).findById(TEST_ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId - user not found - throw UserNotFoundException")
    void getBookingByIdAndBookerIdWhenUserNotFoundShouldThrowUserNotFoundException() {
        when(userRepository.existsById(eq(TEST_ID))).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getByIdAndBookerId(TEST_ID, user.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(bookingRepository, times(0)).findById(TEST_ID);
        verify(itemRepository, times(0)).findById(TEST_ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId - user not owner - throw UserNotFoundException")
    void getBookingByIdAndBookerIDWhenUserNotOwnerShouldThrowUserNotFoundException() {
        Item badItem = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(TEST_ID)
                .booker(User.builder().id(2L).build())
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.findById(TEST_ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(badItem));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getByIdAndBookerId(TEST_ID, TEST_ID));

        assertEquals("Id владельца вещи или id арендатора не совпадают с входящим параметром", exception.getMessage());
        verify(bookingRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBookerId - id != request param id - throw UserNotFoundException ")
    void getBookingByIdAndBookerIdWhenUserOrOwnerIdNotEqualsParamIdShouldThrowUserNotFoundException() {
        Item badItem = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(TEST_ID)
                .booker(User.builder().id(2L).build())
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(TEST_ID))).thenReturn(true);
        when(bookingRepository.findById(TEST_ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(badItem));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getByIdAndBookerId(TEST_ID, TEST_ID));

        assertEquals("Id владельца вещи или id арендатора не совпадают с входящим параметром", exception.getMessage());
        verify(bookingRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).existsById(TEST_ID);
    }

    @Test
    @DisplayName("create - valid data - success")
    void createShouldCreateWhenRequestHasValidData() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.create(bookingCreateDto, TEST_ID);

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).save(any());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }


    @Test
    @DisplayName("create - user not found - throw UserNotFoundException")
    void createWhenUserNotFoundShouldThrowUserNotFoundException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.create(bookingCreateDto, TEST_ID));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("create - item not found - throw ItemNotFoundException")
    void createWhenItemNotFoundShouldThrowItemNotFoundException() {
        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingCreateDto, TEST_ID));

        assertEquals("Вещь не найдена", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(userRepository, times(0)).findById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("create - available false - throw BookingNotAvailableException")
    void createWhenAvailableFalseShouldThrowBookingNotAvailableException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(false)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.create(bookingCreateDto, TEST_ID));

        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("create - user not owner - throw UserNotFoundException")
    void creatWhenUserNotOwnerShouldThrowUserNotFoundException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();

        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.create(bookingCreateDto, TEST_ID));

        assertEquals("Вы не можете бронировать свою вещь", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("create - invalid start or end time - throw BookingNotAvailableException")
    void createWhenInvalidStartOrEndDateShouldThrowBookingNotAvailableException() {
        Item createItem = Item.builder()
                .id(TEST_ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto badBooking = BookingRequestDto.builder()
                .itemId(TEST_ID)
                .end(now)
                .start(now)
                .build();


        when(itemRepository.findById(TEST_ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.create(badBooking, TEST_ID));

        assertEquals("Время бронировани неверное", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(userRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("approve - approved false - return patched booking")
    void approveWhenApprovedFalseShouldReturnPatchedBooking() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(TEST_ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .bookingStatus(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();


        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.approve(TEST_ID, TEST_ID, Boolean.FALSE);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("approve - approved true - return patched booking")
    void patchWhenApprovedTrueShouldReturnPatchedBooking() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(TEST_ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .bookingStatus(BookingStatus.APPROVED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();


        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.approve(TEST_ID, TEST_ID, Boolean.TRUE);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("approve - id not exists - throw UserNotFoundException")
    void approveWhenUserNotExistsShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(TEST_ID, TEST_ID, true));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).existsById(TEST_ID);
        verify(bookingRepository, times(0)).findById(TEST_ID);

    }

    @Test
    @DisplayName("approve - not owner - throw BookingNotAvailableException")
    void approveWhenUserNotOwnerShouldThrowBookingNotAvailableException() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(userPatch)
                .build();
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(userPatch)
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingPatch));
        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.approve(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Только владелец вещи может подтвердить бронирование", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("approve - not owner - throw UserNotFoundException")
    void approveWhenBookingUserNotOwnerShouldThrowUserNotFoundException() {
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .bookingStatus(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingPatch));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Только владелец вещи может подтвердить бронирование", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("approve - status not WAITING - throw BookingNotAvailableException")
    void approveWhenBookingStatusNotWaitingShouldThrowBookingNotAvailableException() {
        Booking bookingPatch = Booking.builder()
                .id(TEST_ID)
                .booker(user)
                .bookingStatus(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingPatch));
        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.approve(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Статус брони должен быть в ожидании - 'WAITING', другой статус подтвердить невозможно", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("approve - booking not found - throw BookingNotFoundException")
    void approveWhenBookingNotFoundShouldThrowBookingNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approve(TEST_ID, TEST_ID, Boolean.FALSE));

        assertEquals("Бронирование не найдено", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("getAllByUser - id not exists - throw UserNotFoundException")
    void getAllByUserShouldThrowUserNotFoundExceptionWhenUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllByUser(TEST_ID, "APPROVED", 0, 10));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1))
                .existsById(anyLong());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("getAllByOwner - id not exists - throw UserNotFoundException")
    void getAllByOwnerShouldThrowUserNotFoundExceptionWhenUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllByOwner(TEST_ID, "APPROVED", 0, 10));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1))
                .existsById(anyLong());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("getAllByUser - PAST - list of one booking")
    void getAllByUserWhenInvokedDataStatePastShouldReturnListOfOneBooking() {
        String state = "PAST";
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - CURRENT - list of one booking")
    void getAllByUserWhenInvokedWithStateCurrentShouldReturnListOfOneBooking() {
        String state = "CURRENT";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - FUTURE - list of one booking")
    void getAllByUserWhenInvokedWithStateFutureShouldReturnListOfOneBooking() {
        String state = "FUTURE";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - WAITING - list of one booking")
    void getAllByUserWhenInvokedWithStateWaitingShouldReturnListOfOneBooking() {
        String state = "WAITING";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - REJECTED - list of one booking")
    void getAllByUserWhenInvokedWithStateRejectedShouldReturnListOfOneBooking() {
        String state = "REJECTED";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - REJECTED - list of one booking")
    void getAllByUserWhenInvokedWithStateAllShouldReturnListOfOneBooking() {
        String state = "ALL";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findAllByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByUser(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findAllByBooker_IdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByUser - invalid state - throw BookingNotAvailableException ")
    void getAllByUserWhenInvokeWithInvalidStateShouldThrowBookingNotAvailableException() {
        String state = "ANYTHING";
        when(userRepository.existsById(anyLong())).thenReturn(true);
        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.getAllByUser(TEST_ID, state, 0, 2));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage(

        ));
        verify(bookingRepository, times(0))
                .findAllByBooker_IdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - CURRENT - throw BookingNotAvailableException")
    void getAllByOwnerWhenInvokeWithStateCurrentShouldReturnListOneBooking() {
        String state = "CURRENT";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - PAST - list of one booking")
    void getAllByOwnerWhenInvokeWithStatePastShouldReturnListOneBooking() {
        String state = "PAST";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - FUTURE - list of one booking")
    void getAllByOwnerWhenInvokeByStateFutureShouldReturnListOneBooking() {
        String state = "FUTURE";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - WAITING - list of one booking")
    void getAllByOwnerWhenInvokeWithStateWaitingShouldReturnListOneBooking() {
        String state = "WAITING";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdAndBookingStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - REJECTED - list of one booking")
    void getAllByOwnerBookingsWhenInvokeWithStateRejectedShouldReturnListOneBooking() {
        String state = "REJECTED";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdAndBookingStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndBookingStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - ALL - list of one booking")
    void getAllByOwnerWhenInvokeWithStateAllShouldReturnListOneBooking() {
        String state = "ALL";

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllByOwner(TEST_ID, state, 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getAllByOwner - invalid state - BookingNotAvailableException")
    void getAllByOwnerWhenInvokeWithInvalidStateShouldThrowBookingNotAvailableException() {
        String state = "INVALID";

        when(userRepository.existsById(anyLong())).thenReturn(true);

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.getAllByOwner(TEST_ID, state, 0, 2));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
        verify(bookingRepository, times(0))
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }
}