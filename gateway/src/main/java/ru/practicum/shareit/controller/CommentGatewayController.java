package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.CommentClient;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.CommentDto;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentGatewayController {
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Gateway: POST /comments - создание комментария пользователем: {}", userId);
        return commentClient.createComment(userId, commentDto);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Object> getItemComments(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                  @PathVariable Long itemId) {
        log.info("Gateway: GET /comments/item/{} - получение комментариев вещи, пользователь: {}", itemId, userId);
        return commentClient.getItemComments(userId, itemId);
    }
}
