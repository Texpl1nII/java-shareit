package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.Constants;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
