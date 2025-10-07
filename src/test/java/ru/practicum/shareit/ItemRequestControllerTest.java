package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void testCreateItemRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test description");
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.createItemRequest(any(), eq(userId))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test description\"}"))
                .andExpect(status().isCreated())  // Изменено на status().isCreated()
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    @Test
    void testGetUserRequests() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test description");
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getUserItemRequests(userId)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetAllRequests() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test description");
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), eq(userId)))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Test description");
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    @Test
    void testGetRequestByIdNotFound() throws Exception {
        Long userId = 1L;
        Long requestId = 99L;

        when(itemRequestService.getItemRequestById(requestId, userId))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }
}
