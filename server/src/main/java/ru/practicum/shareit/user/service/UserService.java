package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        log.debug("Getting user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public User createUser(User user) {
        log.debug("Creating user: {}", user);
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        User saved = userRepository.save(user);
        log.debug("User created: {}", saved);
        return saved;
    }

    public User updateUser(Long userId, User user) {
        log.info("Updating user {} with data: {}", userId, user);

        User existingUser = getUserById(userId);
        log.debug("Found existing user: {}", existingUser);

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            log.debug("Checking email conflict for: {}", user.getEmail());
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            existingUser.setEmail(user.getEmail());
            log.debug("Email updated to: {}", user.getEmail());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
            log.debug("Name updated to: {}", user.getName());
        }

        User saved = userRepository.save(existingUser);
        log.info("User updated successfully: {}", saved);
        return saved;
    }

    public void deleteUser(Long userId) {
        log.debug("Deleting user: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        log.debug("User deleted: {}", userId);
    }
}