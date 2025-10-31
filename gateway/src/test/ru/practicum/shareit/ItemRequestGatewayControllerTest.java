package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.controller.ItemRequestGatewayController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestGatewayController.class)
class ItemRequestGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createItemRequest_shouldPassDataToClient() throws Exception {
        when(itemRequestClient.createItemRequest(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need item\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest_shouldRejectBlankDescription() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Описание запроса не может быть пустым"));
    }

    @Test
    void createItemRequest_shouldRejectNullDescription() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Описание запроса не может быть пустым"));
    }

    @Test
    void createItemRequest_shouldRejectWhitespaceDescription() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Описание запроса не может быть пустым"));
    }

    @Test
    void getUserItemRequests_shouldPassUserId() throws Exception {
        when(itemRequestClient.getUserItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests_shouldPassParametersToClient() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById_shouldPassRequestId() throws Exception {
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }
}