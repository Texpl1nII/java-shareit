package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.Constants;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestGatewayController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Gateway: POST /requests - создание запроса пользователем: {}", userId);
        return client.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Gateway: GET /requests - получение запросов пользователя: {}", userId);
        return client.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "20") Integer size,
                                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Gateway: GET /requests/all?from={}&size={} - получение всех запросов, пользователь: {}",
                from, size, userId);
        return client.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Gateway: GET /requests/{} - получение запроса по ID, пользователь: {}", requestId, userId);
        return client.getItemRequestById(userId, requestId);
    }
}
