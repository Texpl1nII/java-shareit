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
import ru.practicum.shareit.request.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    void searchItems_shouldReturnEmptyForBlankText() throws Exception {
        // given
        when(itemClient.searchItems(anyLong(), eq(" ")))
                .thenReturn(ResponseEntity.ok().body("[]")); // Возвращаем пустой JSON массив

        // when & then
        mockMvc.perform(get("/items/search?text= ")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // Ожидаем пустой массив

        // verify call to client with correct parameters
        verify(itemClient).searchItems(eq(1L), eq(" "));
    }

    @Test
    void searchItems_shouldCallClientWithText() throws Exception {
        // given
        when(itemClient.searchItems(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(get("/items/search?text=drill")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).searchItems(eq(1L), eq("drill"));
    }

    @Test
    void createItem_shouldPassDataToClient() throws Exception {
        // given
        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"description\":\"Description\",\"available\":true}"))
                .andExpect(status().isOk());

        verify(itemClient).createItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void createComment_shouldPassCommentData() throws Exception {
        // given
        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}"))
                .andExpect(status().isOk());

        verify(itemClient).createComment(eq(1L), eq(1L), any(CommentDto.class));
    }

    @Test
    void updateItem_shouldPassPatchData() throws Exception {
        // given
        when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(patch("/items/1")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(1L), eq(1L), any(ItemDto.class));
    }

    @Test
    void getItemById_shouldPassParameters() throws Exception {
        // given
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(get("/items/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(eq(1L), eq(1L));
    }

    @Test
    void getUserItems_shouldPassUserId() throws Exception {
        // given
        when(itemClient.getUserItems(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(get("/items")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getUserItems(eq(1L));
    }
}