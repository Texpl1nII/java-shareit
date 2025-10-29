package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceIT {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_shouldCreateUser() {
        // given
        User user = new User(null, "user", "user@mail.com");

        // when
        User result = userService.createUser(user);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("user");
        assertThat(result.getEmail()).isEqualTo("user@mail.com");
    }

    @Test
    void createUser_shouldThrowConflictForDuplicateEmail() {
        // given
        User user1 = new User(null, "user1", "same@mail.com");
        userService.createUser(user1);

        User user2 = new User(null, "user2", "same@mail.com");

        // when & then
        assertThrows(ConflictException.class, () -> userService.createUser(user2));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // given
        User user = userRepository.save(new User(null, "oldName", "old@mail.com"));

        User updateData = new User();
        updateData.setName("newName");
        updateData.setEmail("new@mail.com");

        // when
        User result = userService.updateUser(user.getId(), updateData);

        // then
        assertThat(result.getName()).isEqualTo("newName");
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        // given
        User user = userRepository.save(new User(null, "user", "user@mail.com"));

        // when
        userService.deleteUser(user.getId());

        // then
        assertThat(userRepository.existsById(user.getId())).isFalse();
    }
}
