package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.ItemDto;
import ru.practicum.shareit.request.dto.CommentDto;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemGatewayController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Gateway: POST /items - создание вещи пользователем: {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Gateway: PATCH /items/{} - обновление вещи пользователем: {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Gateway: POST /items/{}/comment - создание комментария пользователем: {}", itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Gateway: GET /items/{} - получение вещи по ID, пользователь: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Gateway: GET /items - получение вещей пользователя: {}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @RequestParam String text) {
        log.info("Gateway: GET /items/search?text={} - поиск вещей пользователем: {}", text, userId);
        return itemClient.searchItems(userId, text);
    }
}
