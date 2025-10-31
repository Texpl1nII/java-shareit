package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void shouldSerializeUserData() throws Exception {
        // given
        UserDto dto = new UserDto(1L, "user", "user@mail.com");

        // when & then
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.name")
                .isEqualTo("user");
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.email")
                .isEqualTo("user@mail.com");
    }

    @Test
    void shouldDeserializeFromJSON() throws Exception {
        // given
        String content = "{\"name\":\"user\",\"email\":\"user@mail.com\"}";

        // when
        UserDto result = json.parseObject(content);

        // then
        assertThat(result.getName()).isEqualTo("user");
        assertThat(result.getEmail()).isEqualTo("user@mail.com");
    }

    @Test
    void shouldHandleMissingFields() throws Exception {
        // given
        String content = "{\"name\":\"user\"}";

        // when
        UserDto result = json.parseObject(content);

        // then
        assertThat(result.getName()).isEqualTo("user");
        assertThat(result.getEmail()).isNull();
    }
}