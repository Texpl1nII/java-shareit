package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.Booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldSerializeDatesAsISO8601() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);

        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(1L);
        dto.setStatus(BookingStatus.WAITING);

        // when & then
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-01T10:00:00");
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-02T10:00:00");
    }

    @Test
    void shouldDeserializeFromJSON() throws Exception {
        // given
        String content = "{\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\",\"itemId\":1,\"status\":\"WAITING\"}";

        // when
        BookingDto result = json.parseObject(content);

        // then
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        // given
        String content = "{\"itemId\":1}";

        // when
        BookingDto result = json.parseObject(content);

        // then
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isNull();
        assertThat(result.getEnd()).isNull();
        assertThat(result.getStatus()).isNull();
    }
}
