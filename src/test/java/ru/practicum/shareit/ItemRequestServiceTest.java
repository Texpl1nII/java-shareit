package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Item request description");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item request description");
        itemRequestDto.setRequesterId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequestId(itemRequest.getId());
        item.setRequestId(1L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
    }

    @Test
    void testCreateItemRequest() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void testGetUserRequests() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllRequests() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(0, 10, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());
    }

    @Test
    void testGetRequestById() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        ItemRequestDto result = itemRequestService.getItemRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void testGetRequestByIdNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequestById(99L, 1L);
        });
    }
}
