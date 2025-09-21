package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ItemRequestDto();
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return Collections.emptyList();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return Collections.emptyList();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ItemRequestDto();
    }
}
