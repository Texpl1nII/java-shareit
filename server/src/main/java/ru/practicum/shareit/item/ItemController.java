package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.Constants;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /items - получение вещей пользователя с ID: {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId,
                               @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /items/{} - получение вещи по ID, пользователь: {}", itemId, userId);
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("POST /items - создание новой вещи пользователем: {}", userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("PATCH /items/{} - обновление вещи, пользователь: {}", itemId, userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") Long itemId,
                                    @RequestBody CommentDto commentDto,
                                    @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("POST /items/{}/comment - создание комментария пользователем: {}", itemId, userId);
        return itemService.createComment(itemId, commentDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text) {
        log.info("GET /items/search?text={} - поиск вещей", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchItems(text);
    }
}