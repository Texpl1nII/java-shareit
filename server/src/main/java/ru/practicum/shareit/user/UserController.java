package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users - получение всех пользователей");
        return userService.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("GET /users/{} - получение пользователя по ID", userId);
        return userMapper.toUserDto(userService.getUserById(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("POST /users - создание нового пользователя: {}", userDto.getEmail());
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - обновление пользователя с данными: {}", userId, userDto);

        try {
            // Временная диагностика
            log.debug("Mapping UserDto to User: {}", userDto);
            User user = userMapper.toUser(userDto);
            log.debug("User mapped successfully: {}", user);

            log.debug("Calling userService.updateUser with userId: {}, user: {}", userId, user);
            UserDto result = userMapper.toUserDto(userService.updateUser(userId, user));
            log.info("User updated successfully: {}", result);

            return result;
        } catch (Exception e) {
            log.error("ERROR in PATCH /users/{}: ", userId, e);  // ← КРИТИЧЕСКИ ВАЖНО ДЛЯ ДИАГНОСТИКИ
            throw e;
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("DELETE /users/{} - удаление пользователя", userId);
        userService.deleteUser(userId);
    }
}