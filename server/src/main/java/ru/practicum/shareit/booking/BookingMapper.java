package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem().getId());
        dto.setItem(booking.getItem().getId()); // Для ответа
        dto.setBooker(booking.getBooker().getId()); // Для ответа
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        // item и booker устанавливаются в сервисе
        booking.setStatus(bookingDto.getStatus() != null ?
                bookingDto.getStatus() : Booking.BookingStatus.WAITING);
        return booking;
    }
}
