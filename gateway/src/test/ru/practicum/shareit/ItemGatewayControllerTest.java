package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.controller.ItemGatewayController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemGatewayController.class)
class ItemGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient itemClient;

    @Test
    void createItem_shouldPassDataToClient() throws Exception {
        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"description\":\"Description\",\"available\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_shouldRejectBlankName() throws Exception {
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"Description\",\"available\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Название не может быть пустым"));
    }

    @Test
    void createItem_shouldRejectNullName() throws Exception {
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Description\",\"available\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Название не может быть пустым"));
    }

    @Test
    void createItem_shouldRejectBlankDescription() throws Exception {
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"description\":\"\",\"available\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Описание не может быть пустым"));
    }

    @Test
    void createItem_shouldRejectNullAvailable() throws Exception {
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"description\":\"Description\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Статус доступности должен быть указан"));
    }

    @Test
    void createComment_shouldRejectBlankText() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Текст комментария не может быть пустым"));
    }

    @Test
    void createComment_shouldRejectNullText() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Текст комментария не может быть пустым"));
    }

    @Test
    void createComment_shouldRejectWhitespaceText() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Текст комментария не может быть пустым"));
    }

    @Test
    void createComment_shouldPassCommentData() throws Exception {
        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturnEmptyForBlankText() throws Exception {
        when(itemClient.searchItems(anyLong(), eq(" ")))
                .thenReturn(ResponseEntity.ok().body("[]"));

        mockMvc.perform(get("/items/search?text= ")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}