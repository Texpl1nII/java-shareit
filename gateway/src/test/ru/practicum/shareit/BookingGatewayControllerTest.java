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
        // given
        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then - используем даты из БУДУЩЕГО (2025 год или позже)
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2025-12-01T10:00:00\",\"end\":\"2025-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void approveBooking_shouldPassApprovedParameter() throws Exception {
        // given
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_shouldRejectUnknownState() throws Exception {
        // given
        when(bookingClient.getUserBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.badRequest().build());

        // when & then
        mockMvc.perform(get("/bookings?state=UNKNOWN")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById_shouldPassUserId() throws Exception {
        // given
        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(get("/bookings/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_shouldPassStateParameter() throws Exception {
        // given
        when(bookingClient.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_shouldValidateBookingRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"end\":\"2024-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2024-12-01T10:00:00\",\"end\":\"2024-12-02T10:00:00\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2020-12-01T10:00:00\",\"end\":\"2024-12-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
