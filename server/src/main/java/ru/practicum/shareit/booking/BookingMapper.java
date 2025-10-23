package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public BookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());

        // ✅ Оставляем как LocalDateTime - Jackson сам преобразует в JSON
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        dto.setItemId(booking.getItem().getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(userMapper.toUserDto(booking.getBooker()));
        dto.setItem(itemMapper.toItemDto(booking.getItem()));

        return dto;
    }
}