package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long nextId = 1L;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return users.get(userId);
    }

    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        user.setId(nextId++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User updateUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User existingUser = users.get(userId);

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (emails.contains(user.getEmail())) {
                throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
            }

            emails.remove(existingUser.getEmail());
            existingUser.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }

        return existingUser;
    }

    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        String email = users.get(userId).getEmail();
        users.remove(userId);
        emails.remove(email);
    }
}