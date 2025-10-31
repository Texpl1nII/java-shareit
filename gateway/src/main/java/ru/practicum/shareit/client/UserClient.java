package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class UserClient extends BaseClient {
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users")).build());
    }

    public ResponseEntity<Object> createUser(Object userDto) {
        return post("", null, null, userDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, Object userDto) {
        return patch("/" + userId, null, null, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId, null, null);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", null, null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, null, null);
    }
}