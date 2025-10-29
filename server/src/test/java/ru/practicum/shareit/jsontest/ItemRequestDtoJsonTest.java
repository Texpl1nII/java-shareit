package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeWithItemsList() throws Exception {
        // given
        // Используем правильный конструктор ItemShortDto
        ItemShortDto item1 = new ItemShortDto(1L, "Item1", 10L);
        ItemShortDto item2 = new ItemShortDto(2L, "Item2", 10L);
        List<ItemShortDto> items = List.of(item1, item2);

        ItemRequestDto dto = new ItemRequestDto(1L, "Need items", 1L, LocalDateTime.now(), items);

        // when & then
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need items");
        assertThat(json.write(dto)).extractingJsonPathArrayValue("$.items")
                .hasSize(2);
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Item1");
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.items[1].name")
                .isEqualTo("Item2");
        assertThat(json.write(dto)).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(1);
        assertThat(json.write(dto)).extractingJsonPathNumberValue("$.items[0].ownerId")
                .isEqualTo(10);
    }

    @Test
    void shouldDeserializeWithItemsArray() throws Exception {
        // given
        String content = "{" +
                "\"description\":\"Need items\"," +
                "\"requesterId\":1," +
                "\"items\":[" +
                "  {\"id\":1,\"name\":\"Item1\",\"ownerId\":10}," +
                "  {\"id\":2,\"name\":\"Item2\",\"ownerId\":10}" +
                "]" +
                "}";

        // when
        ItemRequestDto result = json.parseObject(content);

        // then
        assertThat(result.getDescription()).isEqualTo("Need items");
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Item1");
        assertThat(result.getItems().get(0).getOwnerId()).isEqualTo(10L);
        assertThat(result.getItems().get(1).getName()).isEqualTo("Item2");
    }

    @Test
    void shouldDeserializeWithEmptyItems() throws Exception {
        // given
        String content = "{\"description\":\"Need item\",\"requesterId\":1}";

        // when
        ItemRequestDto result = json.parseObject(content);

        // then
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getItems()).isNull();
    }

    @Test
    void shouldHandleCreatedTimestamp() throws Exception {
        // given
        String content = "{\"description\":\"Need item\",\"requesterId\":1,\"created\":\"2024-01-01T10:00:00\"}";

        // when
        ItemRequestDto result = json.parseObject(content);

        // then
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void shouldHandleNullItems() throws Exception {
        // given
        String content = "{\"description\":\"Need item\",\"requesterId\":1,\"items\":null}";

        // when
        ItemRequestDto result = json.parseObject(content);

        // then
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getItems()).isNull();
    }

    @Test
    void shouldHandleEmptyItemsArray() throws Exception {
        // given
        String content = "{\"description\":\"Need item\",\"requesterId\":1,\"items\":[]}";

        // when
        ItemRequestDto result = json.parseObject(content);

        // then
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getItems()).isEmpty();
    }
}
