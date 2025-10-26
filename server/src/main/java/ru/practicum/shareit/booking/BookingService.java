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
        log.info("=== START APPROVE BOOKING ===");
        log.info("Params: bookingId={}, approved={}, userId={}", bookingId, approved, userId);

        try {
            // Валидация
            if (bookingId == null) throw new BadRequestException("Booking ID is null");
            if (approved == null) throw new BadRequestException("Approved is null");
            if (userId == null) throw new BadRequestException("User ID is null");

            log.debug("Finding booking with ID: {}", bookingId);
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        log.error("Booking not found: {}", bookingId);
                        return new NotFoundException("Бронирование с ID " + bookingId + " не найдено");
                    });

            log.debug("Booking found: id={}, status={}", booking.getId(), booking.getStatus());
            log.debug("Booking item: {}", booking.getItem());
            log.debug("Booking item owner: {}", booking.getItem() != null ? booking.getItem().getOwner() : "NULL");

            // Проверки
            if (booking.getItem() == null) {
                log.error("Item is null for booking: {}", bookingId);
                throw new NotFoundException("Вещь не найдена");
            }

            if (booking.getItem().getOwner() == null) {
                log.error("Item owner is null for booking: {}", bookingId);
                throw new NotFoundException("Владелец вещи не найден");
            }

            Long ownerId = booking.getItem().getOwner().getId();
            log.debug("Checking permissions: ownerId={}, userId={}", ownerId, userId);

            if (!ownerId.equals(userId)) {
                log.error("Permission denied: ownerId={}, userId={}", ownerId, userId);
                throw new ForbiddenException("Только владелец может подтвердить бронирование");
            }

            if (booking.getStatus() != BookingStatus.WAITING) {
                log.error("Invalid status: current={}, expected=WAITING", booking.getStatus());
                throw new BadRequestException("Бронирование уже обработано");
            }

            // Обновление статуса
            BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
            log.debug("Updating status from {} to {}", booking.getStatus(), newStatus);
            booking.setStatus(newStatus);

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking {} successfully updated to {}", bookingId, newStatus);

            BookingDto result = bookingMapper.toBookingDto(savedBooking);
            log.debug("Result DTO: {}", result);

            return result;

        } catch (Exception e) {
            log.error("=== ERROR IN APPROVE BOOKING ===", e);
            throw e;
        } finally {
            log.info("=== END APPROVE BOOKING ===");
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