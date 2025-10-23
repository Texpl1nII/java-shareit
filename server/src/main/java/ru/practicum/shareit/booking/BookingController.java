package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.Constants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("POST /bookings - создание бронирования пользователем: {}", userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable("bookingId") Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("PATCH /bookings/{}?approved={} - подтверждение бронирования пользователем: {}",
                bookingId, approved, userId);

        try {
            return bookingService.approveBooking(bookingId, approved, userId);
        } catch (Exception e) {
            log.error("Error in PATCH /bookings/{}: ", bookingId, e);
            throw e; // Пробрасываем исключение для обработки в ErrorHandler
        }
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable("bookingId") Long bookingId,
                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /bookings/{} - получение бронирования по ID, пользователь: {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /bookings?state={} - получение бронирований пользователя: {}", state, userId);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /bookings/owner?state={} - получение бронирований владельца: {}", state, userId);
        return bookingService.getOwnerBookings(userId, state);
    }
}
