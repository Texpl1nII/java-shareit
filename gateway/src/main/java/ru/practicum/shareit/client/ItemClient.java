package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items")).build());
    }

    public ResponseEntity<Object> createItem(Long userId, Object itemDto) {
        return post("", userId, null, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, Object itemDto) {
        return patch("/" + itemId, userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, Object commentDto) {
        return post("/" + itemId + "/comment", userId, null, commentDto);
    }
}
