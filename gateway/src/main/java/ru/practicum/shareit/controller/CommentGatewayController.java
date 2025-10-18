package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.CommentClient;
import ru.practicum.shareit.Constants;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentGatewayController {
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @RequestBody Object commentDto) {
        return commentClient.createComment(userId, commentDto);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Object> getItemComments(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                  @PathVariable Long itemId) {
        return commentClient.getItemComments(userId, itemId);
    }
}
