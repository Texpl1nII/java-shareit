package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.ItemMapper;

import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private Long nextId = 1L;

    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId(), null));

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь с ID " + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования должна быть позже даты начала");
        }

        Booking booking = new Booking();
        booking.setId(nextId++);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Booking.BookingStatus.WAITING);

        bookings.put(booking.getId(), booking);
        return bookingMapper.toBookingDto(booking);
    }

    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        if (!bookings.containsKey(bookingId)) {
            throw new NotFoundException("Бронирование с ID " + bookingId + " не найдено");
        }

        Booking booking = bookings.get(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только владелец вещи может подтвердить бронирование");
        }

        if (booking.getStatus() != Booking.BookingStatus.WAITING) {
            throw new BadRequestException("Бронирование уже имеет статус: " + booking.getStatus());
        }

        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);

        return bookingMapper.toBookingDto(booking);
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        if (!bookings.containsKey(bookingId)) {
            throw new NotFoundException("Бронирование с ID " + bookingId + " не найдено");
        }

        Booking booking = bookings.get(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Доступ к данному бронированию запрещен");
        }

        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(String state, Long userId) {
        userService.getUserById(userId); // проверка существования пользователя

        List<Booking> userBookings = bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(userId))
                .collect(Collectors.toList());

        return filterBookingsByState(userBookings, state);
    }

    public List<BookingDto> getOwnerBookings(String state, Long userId) {
        userService.getUserById(userId); // проверка существования пользователя

        List<Booking> ownerBookings = bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(userId))
                .collect(Collectors.toList());

        return filterBookingsByState(ownerBookings, state);
    }

    private List<BookingDto> filterBookingsByState(List<Booking> bookingList, String state) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> filteredBookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingList;
            case "CURRENT" -> bookingList.stream()
                    .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                    .collect(Collectors.toList());
            case "PAST" -> bookingList.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .collect(Collectors.toList());
            case "FUTURE" -> bookingList.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .collect(Collectors.toList());
            case "WAITING" -> bookingList.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.WAITING)
                    .collect(Collectors.toList());
            case "REJECTED" -> bookingList.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.REJECTED)
                    .collect(Collectors.toList());
            default -> throw new BadRequestException("Unknown state: " + state);
        };

        return filteredBookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}