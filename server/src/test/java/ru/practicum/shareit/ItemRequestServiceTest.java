package ru.practicum.shareit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith({MockitoExtension.class})
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

    ItemRequestServiceTest() {
    }

    @BeforeEach
    void setUp() {
        this.user = new User();
        this.user.setId(1L);
        this.user.setName("User");
        this.user.setEmail("user@example.com");
        this.itemRequest = new ItemRequest();
        this.itemRequest.setId(1L);
        this.itemRequest.setDescription("Item request description");
        this.itemRequest.setRequester(this.user);
        this.itemRequest.setCreated(LocalDateTime.now());
        this.itemRequestDto = new ItemRequestDto();
        this.itemRequestDto.setId(1L);
        this.itemRequestDto.setDescription("Item request description");
        this.itemRequestDto.setRequesterId(1L);
        this.itemRequestDto.setCreated(LocalDateTime.now());
        this.itemRequestDto.setItems(new ArrayList());
        this.item = new Item();
        this.item.setId(1L);
        this.item.setName("Item");
        this.item.setDescription("Item description");
        this.item.setAvailable(true);
        this.item.setOwner(this.user);
        this.item.setRequestId(this.itemRequest.getId());
        this.itemDto = new ItemDto();
        this.itemDto.setId(1L);
        this.itemDto.setName("Item");
        this.itemDto.setDescription("Item description");
        this.itemDto.setAvailable(true);
        this.itemDto.setRequestId(1L);
    }

    @Test
    void testCreateItemRequest() {
        Mockito.when(this.userService.getUserById(Mockito.anyLong())).thenReturn(this.user);
        Mockito.when(this.itemRequestMapper.toItemRequest((ItemRequestDto) Mockito.any(ItemRequestDto.class))).thenReturn(this.itemRequest);
        Mockito.when((ItemRequest) this.itemRequestRepository.save((ItemRequest) Mockito.any(ItemRequest.class))).thenReturn(this.itemRequest);
        Mockito.when(this.itemRequestMapper.toItemRequestDto((ItemRequest) Mockito.any(ItemRequest.class))).thenReturn(this.itemRequestDto);
        ItemRequestDto result = this.itemRequestService.createItemRequest(this.itemRequestDto, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.itemRequestDto.getId(), result.getId());
        Assertions.assertEquals(this.itemRequestDto.getDescription(), result.getDescription());
        ((ItemRequestRepository) Mockito.verify(this.itemRequestRepository)).save((ItemRequest) Mockito.any(ItemRequest.class));
    }

    @Test
    void testGetUserRequests() {
        this.itemRequestDto.setItems(new ArrayList());
        Mockito.when(this.userService.getUserById(Mockito.anyLong())).thenReturn(this.user);
        Mockito.when(this.itemRequestRepository.findByRequesterIdOrderByCreatedDesc(Mockito.anyLong())).thenReturn(List.of(this.itemRequest));
        Mockito.when(this.itemRequestMapper.toItemRequestDto((ItemRequest) Mockito.any(ItemRequest.class))).thenReturn(this.itemRequestDto);
        Mockito.when(this.itemRepository.findByRequestIdIn(Mockito.anyList())).thenReturn(Collections.emptyList());
        List<ItemRequestDto> result = this.itemRequestService.getUserItemRequests(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(this.itemRequestDto.getId(), ((ItemRequestDto) result.get(0)).getId());
        Assertions.assertNotNull(((ItemRequestDto) result.get(0)).getItems());
        Assertions.assertTrue(((ItemRequestDto) result.get(0)).getItems().isEmpty());
    }

    @Test
    void testGetRequestById() {
        this.itemRequestDto.setItems(new ArrayList());
        Mockito.when(this.userService.getUserById(Mockito.anyLong())).thenReturn(this.user);
        Mockito.when(this.itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(this.itemRequest));
        Mockito.when(this.itemRequestMapper.toItemRequestDto((ItemRequest) Mockito.any(ItemRequest.class))).thenReturn(this.itemRequestDto);
        Mockito.when(this.itemRepository.findByRequestId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        ItemRequestDto result = this.itemRequestService.getItemRequestById(1L, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.itemRequestDto.getId(), result.getId());
        Assertions.assertNotNull(result.getItems());
        Assertions.assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testGetRequestByIdWithNotFoundException() {
        Mockito.when(this.userService.getUserById(Mockito.anyLong())).thenReturn(this.user);
        Mockito.when(this.itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> this.itemRequestService.getItemRequestById(1L, 1L));
    }
}