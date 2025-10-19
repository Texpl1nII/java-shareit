package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public abstract class BaseClient {
    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    // ДОБАВИТЬ ЭТИ МЕТОДЫ!
    protected <T> ResponseEntity<Object> patch(String path, Long userId, Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        try {
            if (parameters != null && !parameters.isEmpty()) {
                return rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                return rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
