package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingMapper(ItemMapper itemMapper, UserMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                null,
                null,
                booking.getStatus()
        );
        dto.setItem(itemMapper.toItemDto(booking.getItem()));
        dto.setBooker(userMapper.toUserDto(booking.getBooker()));
        return dto;
    }
}
