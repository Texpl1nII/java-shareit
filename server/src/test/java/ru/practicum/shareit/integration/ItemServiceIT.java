package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceIT {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void getUserItems_shouldReturnUserItems() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Description", true, owner, null));

        // when
        List<ItemDto> result = itemService.getUserItems(owner.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Item");
    }

    @Test
    void createItem_withRequestId_shouldLinkToRequest() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User requester = userRepository.save(new User(null, "requester", "requester@mail.com"));
        ItemRequest request = itemRequestRepository.save(new ItemRequest(null, "Need item", requester, LocalDateTime.now()));

        ItemDto itemDto = new ItemDto(null, "Item", "For request", true, request.getId(), null, null, null);

        // when
        ItemDto result = itemService.createItem(itemDto, owner.getId());

        // then
        assertThat(result.getRequestId()).isEqualTo(request.getId());
        assertThat(result.getName()).isEqualTo("Item");
    }

    @Test
    void searchItems_shouldReturnAvailableItems() {
        // given
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(null, "Drill", "Powerful drill", true, owner, null));

        // when
        List<ItemDto> result = itemService.searchItems("drill");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void searchItems_shouldReturnEmptyForBlankText() {
        // when
        List<ItemDto> result = itemService.searchItems(" ");

        // then
        assertThat(result).isEmpty();
    }
}