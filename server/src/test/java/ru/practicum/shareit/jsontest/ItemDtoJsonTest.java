package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldSerializeRequiredFields() throws Exception {
        // given
        ItemDto dto = new ItemDto(1L, "Item", "Description", true, null, null, null, null);

        // when & then
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.name")
                .isEqualTo("Item");
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(json.write(dto)).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
    }

    @Test
    void shouldDeserializeWithRequestId() throws Exception {
        // given
        String content = "{\"name\":\"Item\",\"description\":\"Description\",\"available\":true,\"requestId\":5}";

        // when
        ItemDto result = json.parseObject(content);

        // then
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(5L);
    }

    @Test
    void shouldHandleMissingOptionalFields() throws Exception {
        // given
        String content = "{\"name\":\"Item\",\"description\":\"Description\",\"available\":true}";

        // when
        ItemDto result = json.parseObject(content);

        // then
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getRequestId()).isNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }
}
