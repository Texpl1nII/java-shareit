package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.Constants;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "20") Integer size,
                                                   @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") Long requestId, // ✅ ИСПРАВЛЕНО
                                             @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
