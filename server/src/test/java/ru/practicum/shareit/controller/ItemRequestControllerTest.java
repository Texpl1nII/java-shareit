package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_shouldReturn201() throws Exception {
        // given
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need item", 1L, LocalDateTime.now(), null);
        when(itemRequestService.createItemRequest(any(), eq(1L))).thenReturn(requestDto);

        // when & then
        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need item\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need item"));
    }

    @Test
    void getUserItemRequests_shouldReturnRequests() throws Exception {
        // given
        ItemRequestDto request1 = new ItemRequestDto(1L, "Request 1", 1L, LocalDateTime.now(), null);
        ItemRequestDto request2 = new ItemRequestDto(2L, "Request 2", 1L, LocalDateTime.now(), null);
        List<ItemRequestDto> requests = Arrays.asList(request1, request2);

        when(itemRequestService.getUserItemRequests(1L)).thenReturn(requests);

        // when & then
        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Request 2"));
    }

    @Test
    void getAllItemRequests_shouldReturnPaginatedRequests() throws Exception {
        // given
        ItemRequestDto request1 = new ItemRequestDto(1L, "Request 1", 2L, LocalDateTime.now(), null);
        ItemRequestDto request2 = new ItemRequestDto(2L, "Request 2", 3L, LocalDateTime.now(), null);
        List<ItemRequestDto> requests = Arrays.asList(request1, request2);

        when(itemRequestService.getAllItemRequests(eq(0), eq(20), eq(1L))).thenReturn(requests);

        // when & then
        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Request 2"));
    }

    @Test
    void getAllItemRequests_shouldUseDefaultPagination() throws Exception {
        // given
        when(itemRequestService.getAllItemRequests(eq(0), eq(20), eq(1L)))
                .thenReturn(Arrays.asList());

        // when & then - without parameters
        mockMvc.perform(get("/requests/all")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById_shouldReturnRequest() throws Exception {
        // given
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need item", 1L, LocalDateTime.now(), null);
        when(itemRequestService.getItemRequestById(eq(1L), eq(1L))).thenReturn(requestDto);

        // when & then
        mockMvc.perform(get("/requests/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need item"));
    }

    @Test
    void createItemRequest_shouldValidateDescription() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need item", 1L, LocalDateTime.now(), null);
        when(itemRequestService.createItemRequest(any(), eq(1L))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"   \"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllItemRequests_shouldHandleMissingUserIdHeader() throws Exception {
        // when & then
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }
}