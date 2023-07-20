package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndBookingStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndBookingStatusOrderByStartDesc(Long ownerId, BookingStatus bookingStatus);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

}
