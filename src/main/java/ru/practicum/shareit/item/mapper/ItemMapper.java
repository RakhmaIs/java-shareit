package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    private ItemMapper() {

    }

    public static ItemDto toItemDto(Item item) {
        return item != null ?
                ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .owner(item.getOwnerId())
                        .request(item.getRequest())
                        .build() : null;
    }

    public static Item toItem(ItemDto itemDto) {
        return itemDto != null ?
                Item.builder()
                        .id(itemDto.getId())
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .ownerId(itemDto.getOwner())
                        .request(itemDto.getRequest())
                        .build() : null;
    }

    public static List<ItemDto> toItemListDto(List<Item> itemList) {
        return itemList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
