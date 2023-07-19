package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(BookingRequestDto bookingRequestDto, Long userId) {
        Item item = itemRepository
                .findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));

        User booker = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (!item.getAvailable()) {
            throw new BookingNotAvailableException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Вы не можете бронировать свою вещь");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())
                || bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())
                || bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingNotAvailableException("Время бронировани неверное");
        }
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();

        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserNotFoundException("Бронирование не найдено"));

        if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new BookingNotAvailableException("Статус брони должен быть в ожидании - 'WAITING', другой статус подтвердить невозможно");
        }
        if (booking.getBooker().getId().equals(userId)) {
            throw new UserNotFoundException("Только владелец вещи может подтвердить бронирование");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotAvailableException("Только владелец вещи может подтвердить бронирование");
        }

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }


    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                log.error("Запрос на получении информации о бронированиях вещей владельца не выполнен. Передан некорректный статус");
                throw new BookingNotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Запрос на получении информации о бронированиях вещей владельца выполнен.");
        return BookingMapper.toBookingDtoList(bookings);

    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                log.error("Запрос на получении информации о бронированиях пользователя не выполнен. Передан некорректный статус");
                throw new BookingNotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Запрос на получении информации о бронированиях пользователя выполнен.");
        return BookingMapper.toBookingDtoList(bookings);

    }

    @Override
    public BookingDto getByIdAndBookerId(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new UserNotFoundException("Id владельца вещи или id арендатора не совпадают с входящим параметром");
        }
        return BookingMapper.toBookingDto(booking);

    }
}
