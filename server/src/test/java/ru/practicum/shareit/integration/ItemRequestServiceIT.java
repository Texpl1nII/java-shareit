package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemRequestServiceIT {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItemRequest_shouldCreateRequest() {
        // given
        User requester = userRepository.save(new User(null, "user", "user@mail.com"));
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need item", null, null, null);

        // when
        ItemRequestDto result = itemRequestService.createItemRequest(requestDto, requester.getId());

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getRequesterId()).isEqualTo(requester.getId());
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void createItemRequest_shouldThrowWhenUserNotFound() {
        // given
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need item", null, null, null);

        // when & then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(requestDto, 999L));
    }

    @Test
    void getUserItemRequests_shouldReturnUserRequests() {
        // given
        User requester = userRepository.save(new User(null, "user", "user@mail.com"));

        // Create multiple requests
        ItemRequest request1 = new ItemRequest(null, "Need item 1", requester, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = new ItemRequest(null, "Need item 2", requester, LocalDateTime.now());
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        // when
        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(requester.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Need item 2"); // sorted by created desc
        assertThat(result.get(1).getDescription()).isEqualTo("Need item 1");
    }

    @Test
    void getUserItemRequests_shouldReturnEmptyForNoRequests() {
        // given
        User user = userRepository.save(new User(null, "user", "user@mail.com"));

        // when
        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(user.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getAllItemRequests_shouldPaginateResults() {
        // given
        User user1 = userRepository.save(new User(null, "user1", "user1@mail.com"));
        User user2 = userRepository.save(new User(null, "user2", "user2@mail.com"));

        for (int i = 1; i <= 3; i++) {
            ItemRequest request = new ItemRequest(null, "Request " + i, user1, LocalDateTime.now().minusHours(i));
            itemRequestRepository.save(request);
        }

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(0, 2, user2.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Request 1");
        assertThat(result.get(1).getDescription()).isEqualTo("Request 2");
    }

    @Test
    void getAllItemRequests_shouldExcludeCurrentUserRequests() {
        // given
        User currentUser = userRepository.save(new User(null, "current", "current@mail.com"));
        User otherUser = userRepository.save(new User(null, "other", "other@mail.com"));

        // Create requests by both users
        ItemRequest currentUserRequest = new ItemRequest(null, "My request", currentUser, LocalDateTime.now());
        ItemRequest otherUserRequest = new ItemRequest(null, "Other request", otherUser, LocalDateTime.now());
        itemRequestRepository.save(currentUserRequest);
        itemRequestRepository.save(otherUserRequest);

        // when
        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(0, 10, currentUser.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Other request"); // should exclude current user's request
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithItems() {
        // given
        User requester = userRepository.save(new User(null, "requester", "requester@mail.com"));
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));

        ItemRequest request = new ItemRequest(null, "Need item", requester, LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);

        // Create item for this request
        Item item = new Item(null, "Provided Item", "Description", true, owner, savedRequest.getId());
        itemRepository.save(item);

        // when
        ItemRequestDto result = itemRequestService.getItemRequestById(savedRequest.getId(), owner.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedRequest.getId());
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Provided Item");
    }

    @Test
    void getItemRequestById_shouldThrowWhenNotFound() {
        // given
        User user = userRepository.save(new User(null, "user", "user@mail.com"));

        // when & then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(999L, user.getId()));
    }

    @Test
    void getItemRequestById_shouldReturnEmptyItemsWhenNoItems() {
        // given
        User requester = userRepository.save(new User(null, "requester", "requester@mail.com"));
        User viewer = userRepository.save(new User(null, "viewer", "viewer@mail.com"));

        ItemRequest request = new ItemRequest(null, "Need item", requester, LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);

        // when
        ItemRequestDto result = itemRequestService.getItemRequestById(savedRequest.getId(), viewer.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedRequest.getId());
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }
}