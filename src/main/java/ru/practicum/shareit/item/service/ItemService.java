package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<ItemDto> getUserItems(Long userId) {
        // Проверка существования пользователя
        userService.getUserById(userId);

        List<Item> userItems = itemRepository.findByOwnerId(userId);
        return userItems.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        // Получаем комментарии к вещи
        List<Comment> comments = commentRepository.findByItemId(itemId);

        ItemDto itemDto = itemMapper.toItemDto(item);
        // Здесь можно добавить код для заполнения информации о комментариях в itemDto

        return itemDto;
    }

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userService.getUserById(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь владельцем вещи с ID " + itemId);
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemDto(updatedItem);
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> foundItems = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text);

        return foundItems.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    // Новый метод для создания комментариев
    @Transactional
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        User author = userService.getUserById(userId);

        boolean hasBookings = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (!hasBookings) {
            throw new BadRequestException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }
}