package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookingServiceIT {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking_shouldCreateWaitingBooking() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Desc", true, owner, null));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        // when
        BookingDto result = bookingService.createBooking(bookingDto, booker.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void approveBooking_shouldChangeStatusToApproved() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Desc", true, owner, null));

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        // when
        BookingDto result = bookingService.approveBooking(savedBooking.getId(), true, owner.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getUserBookings_shouldFilterByState() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Desc", true, owner, null));

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        // when
        List<BookingDto> waitingBookings = bookingService.getUserBookings(booker.getId(), "WAITING");
        List<BookingDto> allBookings = bookingService.getUserBookings(booker.getId(), "ALL");

        // then
        assertThat(waitingBookings).hasSize(1);
        assertThat(allBookings).hasSize(1);
    }
}