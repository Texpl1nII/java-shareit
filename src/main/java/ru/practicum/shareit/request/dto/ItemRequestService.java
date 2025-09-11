package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private Long nextId = 1L;

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userService.getUserById(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(nextId++);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        userService.getUserById(userId); // проверка существования пользователя

        return itemRequests.values().stream()
                .filter(itemRequest -> itemRequest.getRequester().getId().equals(userId))
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        userService.getUserById(userId); // проверка существования пользователя

        return itemRequests.values().stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .skip(from)
                .limit(size)
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userService.getUserById(userId); // проверка существования пользователя

        if (!itemRequests.containsKey(requestId)) {
            throw new NotFoundException("Запрос с ID " + requestId + " не найден");
        }

        return itemRequestMapper.toItemRequestDto(itemRequests.get(requestId));
    }
}
