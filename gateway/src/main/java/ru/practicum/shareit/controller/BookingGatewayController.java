package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.BookingDto;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingGatewayController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        log.info("Gateway: POST /bookings - создание бронирования пользователем: {}", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Gateway: PATCH /bookings/{}?approved={} - подтверждение бронирования пользователем: {}",
                bookingId, approved, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Gateway: GET /bookings/{} - получение бронирования по ID, пользователь: {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: GET /bookings?state={} - получение бронирований пользователя: {}", state, userId);
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: GET /bookings/owner?state={} - получение бронирований владельца: {}", state, userId);
        return bookingClient.getOwnerBookings(userId, state);
    }
}