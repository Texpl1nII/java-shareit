package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;
    private final ItemMapper itemMapper;
    private Long nextId = 1L;

    public List<ItemDto> getUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }
        return itemMapper.toItemDto(items.get(itemId));
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId++);
        item.setOwner(userService.getUserById(userId));
        items.put(item.getId(), item);
        return itemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }

        Item existingItem = items.get(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь владельцем вещи с ID " + itemId);
        }

        // Используем новый метод маппера
        ItemMapper.updateItemFromDto(existingItem, itemDto);

        return itemMapper.toItemDto(existingItem);
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}