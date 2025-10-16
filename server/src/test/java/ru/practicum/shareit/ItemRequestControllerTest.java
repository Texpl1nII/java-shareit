package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDto createdItemRequestDto;

    @BeforeEach
    public void setup() {
        LocalDateTime now = LocalDateTime.now();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        createdItemRequestDto = new ItemRequestDto();
        createdItemRequestDto.setId(1L);
        createdItemRequestDto.setDescription("Test description");
        createdItemRequestDto.setRequesterId(1L);
        createdItemRequestDto.setCreated(now);
        createdItemRequestDto.setItems(Collections.emptyList());
    }

    @Test
    public void createItemRequest_ValidInput_ReturnsCreatedItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(createdItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test description")))
                .andExpect(jsonPath("$.requesterId", is(1)))
                .andExpect(jsonPath("$.items", hasSize(0)));

        verify(itemRequestService, times(1)).createItemRequest(any(ItemRequestDto.class), eq(1L));
    }

    @Test
    public void getUserItemRequests_ValidUserId_ReturnsUserRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(createdItemRequestDto);
        when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test description")));

        verify(itemRequestService, times(1)).getUserItemRequests(1L);
    }

    @Test
    public void createItemRequest_WithItems_ReturnsItemRequestWithItems() throws Exception {
        ItemShortDto itemDto = new ItemShortDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        ItemRequestDto requestWithItems = new ItemRequestDto();
        requestWithItems.setId(1L);
        requestWithItems.setDescription("Test description");
        requestWithItems.setRequesterId(1L);
        requestWithItems.setCreated(LocalDateTime.now());
        requestWithItems.setItems(Arrays.asList(itemDto));

        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(requestWithItems);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Test Item")));

        verify(itemRequestService, times(1)).createItemRequest(any(ItemRequestDto.class), eq(1L));
    }
}