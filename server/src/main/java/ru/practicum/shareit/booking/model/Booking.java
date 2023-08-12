package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time", columnDefinition = "timestamp", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_time", columnDefinition = "timestamp", nullable = false)
    private LocalDateTime end;
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;
    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User booker;
    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
}
