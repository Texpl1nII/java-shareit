package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null,  // lastBooking
                null,  // nextBooking
                new ArrayList<>()  // comments
        );
    }

    public ItemDto toItemDtoWithComments(Item item, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null,  // lastBooking
                null,  // nextBooking
                comments  // ПЕРЕДАЕМ реальные комментарии
        );
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public ItemShortDto toItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}