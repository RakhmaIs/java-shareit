package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Builder
public class ItemDto {
    @Positive(message = "Item id должен быть положительным")
    private Long id;
    @NotBlank(message = "Наименование не должно быть пустым")
    private String name;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    @NotBlank
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}
