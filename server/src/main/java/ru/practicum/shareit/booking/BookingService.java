package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BadRequestException("Даты начала и окончания бронирования обязательны");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала бронирования не может быть в прошлом");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания должно быть после времени начала");
        }

        User booker = userService.getUserById(userId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        // ✅ ДОБАВЛЕНО: Валидация входных параметров
        if (bookingId == null) {
            throw new BadRequestException("ID бронирования не может быть пустым");
        }
        if (approved == null) {
            throw new BadRequestException("Параметр approved не может быть пустым");
        }
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть пустым");
        }

        log.debug("Approving booking {} with approved={} for user {}", bookingId, approved, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));

        // ✅ ДОБАВЛЕНО: Проверка на null для booking.getItem() и booking.getItem().getOwner()
        if (booking.getItem() == null) {
            throw new NotFoundException("Вещь не найдена для бронирования с ID " + bookingId);
        }
        if (booking.getItem().getOwner() == null) {
            throw new NotFoundException("Владелец вещи не найден для бронирования с ID " + bookingId);
        }

        // ✅ УЛУЧШЕНО: Более информативное сообщение об ошибке
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец вещи (ID: " + booking.getItem().getOwner().getId() +
                    ") может подтвердить бронирование. Текущий пользователь: " + userId);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        try {
            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking {} successfully {} by user {}", bookingId,
                    approved ? "APPROVED" : "REJECTED", userId);
            return bookingMapper.toBookingDto(savedBooking);
        } catch (Exception e) {
            // ✅ ДОБАВЛЕНО: Обработка ошибок сохранения
            log.error("Error saving booking {}: ", bookingId, e);
            throw new BadRequestException("Ошибка при сохранении бронирования: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        if (bookingId == null) {
            throw new BadRequestException("ID бронирования не может быть пустым");
        }
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть пустым");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Доступ к информации о бронировании имеют только владелец вещи и автор бронирования");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, String state) {
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть пустым");
        }

        userService.getUserById(userId); // Проверка существования пользователя

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть пустым");
        }

        userService.getUserById(userId); // Проверка существования пользователя

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}