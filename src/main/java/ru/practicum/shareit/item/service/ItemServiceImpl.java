package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemResponseDto;

@Slf4j
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ru.practicum.shareit.item.dto.ItemDto createItem(ru.practicum.shareit.item.dto.ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Не выполнен запрос на получение информации о пользователе по id = {} в методе createItem", userId);
                    return new UserNotFoundException("Пользователь с id " + userId + " не найден. Невозможно создать вещь");
                });
        Item item = ItemMapper.toItem(itemDto, user);
        log.info("Успешно выполнен запрос на создание вещи {}", itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemResponseDto> readItemsOwnedByUserId(Long userId) {
        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        List<Item> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        if (!userRepository.existsById(userId)) {
            log.error("Не выполнен запрос на получение информации о вещах, которыми владеет пользователь с id = {} ", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        for (Item item : items) {
            List<CommentResponseDto> comments = CommentMapper
                    .toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()));
            itemResponseDtoList.add(toItemResponseDto(item,
                    bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    comments));
        }

        log.info("Успешно выполнен запрос на получение списка вещей, по id пользователя = {}", userId);
        return itemResponseDtoList;
    }

    @Override
    public ItemResponseDto readItemByItemIdAndUserId(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<CommentResponseDto> comments = CommentMapper.toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        ItemResponseDto itemResponseDto = toItemResponseDto(item, null, null, comments);

        if (!item.getOwner().getId().equals(userId)) {
            return itemResponseDto;
        }

        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(DESC, "end"));
        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        if (lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemResponseDto.setLastBooking(BookingMapper.toBookingResponseDto(nextBooking.get(0)));
            itemResponseDto.setNextBooking(null);
        } else if (!lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemResponseDto.setLastBooking(BookingMapper.toBookingResponseDto(lastBooking.get(0)));
            itemResponseDto.setNextBooking(BookingMapper.toBookingResponseDto(nextBooking.get(0)));
        }
        log.info("Успешно выполнен получение вещи c id = {}, у пользователя с id = {}", itemId, userId);
        return itemResponseDto;

    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        if (!userRepository.existsById(userId)) {
            log.error("Не выполнен запрос на обновление информации о вещи по id владельца = {}. Пользователя с таким id не существует.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Не выполнен запрос на получение информации о вещи по id = {} в методе updateItem", itemId);
                    return new ItemNotFoundException("Вещь с id = " + itemId + " не найдена");
                });
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteById(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.error("Не выполнен запрос на получение информации о вещи по id = {} в методе deleteById", itemId);
            throw new ItemNotFoundException("Вещь не найдена");
        }
        log.info("Успешно выполнен запрос на удаление информации о вещи по id = {} в методе deleteById", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ru.practicum.shareit.item.dto.ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return ItemMapper.toItemListDto(itemRepository.search(text));
    }

    @Override
    public CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Не выполнен запрос на получение информации о пользователе по id = {} в методе createComment", userId);
                    return new UserNotFoundException("Пользователь не найден");
                });
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Не выполнен запрос на получение информации о вещи по id = {} в методе createComment", itemId);
                    return new ItemNotFoundException("Вещь не найдена");
                });
        List<Booking> userBookings = bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(itemId, userId);
        if (!userBookings.isEmpty()) {
            if (userBookings.stream().anyMatch(booking ->
                    (booking.getBookingStatus() != BookingStatus.REJECTED)
                            && booking.getBookingStatus() != BookingStatus.WAITING
                            && booking.getEnd().isBefore(LocalDateTime.now()))) {
                Comment comment = Comment.builder()
                        .item(item)
                        .author(user)
                        .text(commentRequestDto.getText())
                        .build();
                return CommentMapper.toResponseDto(commentRepository.save(comment));
            } else {
                log.error("Не выполнен запрос на создание комментария");
                throw new BookingNotAvailableException("Бронирование не подтверждена, не завершена или отклонена. Невозможно оставить комментарий.");
            }
        } else {
            log.error("Не выполнен запрос на создание комментария");
            throw new BookingNotAvailableException("Бронирований у данного пользователя не найдено");
        }
    }
}

