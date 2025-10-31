package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.controller.BookingGatewayController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingGatewayController.class)
class BookingGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking_shouldPassDataToClient() throws Exception {
        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2025-12-01T10:00:00\",\"end\":\"2025-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_shouldRejectNullStart() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"end\":\"2025-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.description").value("Дата начала бронирования не может быть пустой"));
    }

    @Test
    void createBooking_shouldRejectNullEnd() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2025-12-01T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Дата окончания бронирования не может быть пустой"));
    }

    @Test
    void createBooking_shouldRejectNullItemId() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2025-12-01T10:00:00\",\"end\":\"2025-12-02T10:00:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("ID вещи не может быть пустым"));
    }

    @Test
    void createBooking_shouldRejectPastStartDate() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2020-12-01T10:00:00\",\"end\":\"2025-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Дата начала бронирования должна быть в настоящем или будущем"));
    }

    @Test
    void approveBooking_shouldPassApprovedParameter() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_shouldPassUserId() throws Exception {
        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }
}
