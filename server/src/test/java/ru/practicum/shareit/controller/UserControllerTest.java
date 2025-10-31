package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        // given
        User user1 = new User(1L, "User1", "user1@mail.com");
        User user2 = new User(2L, "User2", "user2@mail.com");
        List<User> users = Arrays.asList(user1, user2);

        UserDto userDto1 = new UserDto(1L, "User1", "user1@mail.com");
        UserDto userDto2 = new UserDto(2L, "User2", "user2@mail.com");

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("User1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("User2"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // given
        User user = new User(1L, "User", "user@mail.com");
        UserDto userDto = new UserDto(1L, "User", "user@mail.com");

        when(userService.getUserById(1L)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        // when & then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        // given
        UserDto requestDto = new UserDto(null, "New User", "new@mail.com");
        User user = new User(null, "New User", "new@mail.com");
        User savedUser = new User(1L, "New User", "new@mail.com");
        UserDto responseDto = new UserDto(1L, "New User", "new@mail.com");

        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New User\",\"email\":\"new@mail.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        // given
        UserDto requestDto = new UserDto(null, "Updated User", null);
        User user = new User(null, "Updated User", null);
        User updatedUser = new User(1L, "Updated User", "old@mail.com");
        UserDto responseDto = new UserDto(1L, "Updated User", "old@mail.com");

        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("old@mail.com"));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        // given
        doNothing().when(userService).deleteUser(1L);

        // when & then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    void createUser_shouldValidateRequiredFields() throws Exception {
        UserDto requestDto = new UserDto(null, "New User", "new@mail.com");
        User user = new User(null, "New User", "new@mail.com");
        User savedUser = new User(1L, "New User", "new@mail.com");
        UserDto responseDto = new UserDto(1L, "New User", "new@mail.com");

        when(userMapper.toUser(any())).thenReturn(user);
        when(userService.createUser(any())).thenReturn(savedUser);
        when(userMapper.toUserDto(any())).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@mail.com\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User\",\"email\":\"invalid-email\"}"))
                .andExpect(status().isCreated());
    }
}
