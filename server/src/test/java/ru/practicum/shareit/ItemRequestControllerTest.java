package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@WebMvcTest(
        controllers = {ItemRequestController.class}
)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto createdItemRequestDto;

    public ItemRequestControllerTest() {
    }

    @BeforeEach
    public void setup() {
        LocalDateTime now = LocalDateTime.now();
        this.itemRequestDto = new ItemRequestDto();
        this.itemRequestDto.setDescription("Test description");
        this.createdItemRequestDto = new ItemRequestDto();
        this.createdItemRequestDto.setId(1L);
        this.createdItemRequestDto.setDescription("Test description");
        this.createdItemRequestDto.setRequesterId(1L);
        this.createdItemRequestDto.setCreated(now);
        this.createdItemRequestDto.setItems(Collections.emptyList());
    }

    @Test
    public void createItemRequest_ValidInput_ReturnsCreatedItemRequest() throws Exception {
        Mockito.when(this.itemRequestService.createItemRequest((ItemRequestDto)Mockito.any(ItemRequestDto.class), Mockito.anyLong())).thenReturn(this.createdItemRequestDto);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/requests", new Object[0]).header("X-Sharer-User-Id", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(this.itemRequestDto))).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1))).andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("Test description"))).andExpect(MockMvcResultMatchers.jsonPath("$.requesterId", Matchers.is(1))).andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(0)));
        ((ItemRequestService)Mockito.verify(this.itemRequestService, Mockito.times(1))).createItemRequest((ItemRequestDto)Mockito.any(ItemRequestDto.class), Mockito.eq(1L));
    }

    @Test
    public void getUserItemRequests_ValidUserId_ReturnsUserRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(this.createdItemRequestDto);
        Mockito.when(this.itemRequestService.getUserItemRequests(Mockito.anyLong())).thenReturn(requests);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/requests", new Object[0]).header("X-Sharer-User-Id", new Object[]{1L})).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("Test description")));
        ((ItemRequestService)Mockito.verify(this.itemRequestService, Mockito.times(1))).getUserItemRequests(1L);
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
        Mockito.when(this.itemRequestService.createItemRequest((ItemRequestDto)Mockito.any(ItemRequestDto.class), Mockito.anyLong())).thenReturn(requestWithItems);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/requests", new Object[0]).header("X-Sharer-User-Id", new Object[]{1L}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(this.itemRequestDto))).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1))).andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", Matchers.is(1))).andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.is("Test Item")));
        ((ItemRequestService)Mockito.verify(this.itemRequestService, Mockito.times(1))).createItemRequest((ItemRequestDto)Mockito.any(ItemRequestDto.class), Mockito.eq(1L));
    }
}