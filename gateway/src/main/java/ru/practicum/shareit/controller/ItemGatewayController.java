package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.ItemDto;
import ru.practicum.shareit.request.dto.CommentDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemGatewayController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) { // ИЗМЕНИТЬ Object на ItemDto
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody ItemDto itemDto) { // ИЗМЕНИТЬ Object на ItemDto
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) { // ИЗМЕНИТЬ Object на CommentDto
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @RequestParam String text) {
        return itemClient.searchItems(userId, text);
    }

}
