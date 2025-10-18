package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class CommentClient extends BaseClient {
    public CommentClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/comments")).build());
    }

    public ResponseEntity<Object> createComment(Long userId, Object commentDto) {
        return post("", userId, null, commentDto);
    }

    public ResponseEntity<Object> getItemComments(Long userId, Long itemId) {
        return get("/item/" + itemId, userId, null);
    }
}