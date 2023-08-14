package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @Positive(message = "Item id должен быть положительным")
    private Long id;
    @NotBlank(message = "Наименование не должно быть пустым")
    private String name;
    @NotBlank
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}