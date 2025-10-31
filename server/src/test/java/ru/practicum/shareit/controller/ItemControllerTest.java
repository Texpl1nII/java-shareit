package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturn201() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null);
        when(itemService.createItem(any(), eq(1L))).thenReturn(itemDto);

        // when & then
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"description\":\"Description\",\"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void searchItems_shouldReturnEmptyForBlankText() throws Exception {
        // given
        when(itemService.searchItems(anyString())).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/items/search?text= "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Item", "Desc", true, null, null, null, null);
        when(itemService.getItemById(eq(1L), eq(1L))).thenReturn(itemDto);

        // when & then
        mockMvc.perform(get("/items/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Updated", "New Desc", true, null, null, null, null);
        when(itemService.updateItem(eq(1L), any(), eq(1L))).thenReturn(itemDto);

        // when & then
        mockMvc.perform(patch("/items/1")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}
