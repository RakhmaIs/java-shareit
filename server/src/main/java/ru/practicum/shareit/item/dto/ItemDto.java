package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}
