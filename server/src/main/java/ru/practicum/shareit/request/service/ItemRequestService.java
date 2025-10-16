package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userService.getUserById(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        userService.getUserById(userId); // Проверка существования пользователя

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequestId = getItemsByRequestIds(requestIds);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
                    List<ItemShortDto> itemDtos = items.stream()
                            .map(itemMapper::toItemShortDto)  // Изменено
                            .collect(Collectors.toList());
                    dto.setItems(itemDtos);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        userService.getUserById(userId); // Проверка существования пользователя

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageRequest).getContent();

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequestId = getItemsByRequestIds(requestIds);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
                    List<ItemShortDto> itemDtos = items.stream()
                            .map(itemMapper::toItemShortDto)
                            .collect(Collectors.toList());
                    dto.setItems(itemDtos);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userService.getUserById(userId); // Проверка существования пользователя

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);

        // Получаем вещи, созданные в ответ на этот запрос
        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemShortDto> itemDtos = items.stream()  // Изменено с ItemDto на ItemShortDto
                .map(itemMapper::toItemShortDto)  // Изменено с toItemDto на toItemShortDto
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    // Вспомогательный метод для получения вещей по ID запросов
    private Map<Long, List<Item>> getItemsByRequestIds(List<Long> requestIds) {
        if (requestIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        return items.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
    }
}
