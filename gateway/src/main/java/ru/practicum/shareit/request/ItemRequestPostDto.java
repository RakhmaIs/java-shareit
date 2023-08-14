package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestPostDto {
    @NotEmpty(message = "Описание не должно быть пустым")
    private String description;

}

