package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.ValueSources;

public class ShortCodeTest {
  @Test
  void shouldCreateValidShortCode() {
    String expected = "abc123";
    String payload = "abc123";

    ShortCode result = new ShortCode(payload);

    assertThat(result.value()).isEqualTo(expected);
  }

  @Test
  void shouldRejectNullShortCode() {
    String payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code is required");
  }

  @Test
  void shouldRejectBlankShortCode() {
    String payload = "      ";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code is required");
  }

  @Test
  void shouldRejectCodeWithLessThanSixCharacters() {
    String payload = "abc12";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code must have 6 characters");
  }

  @Test
  void shouldRejectCodeWithMoreThanSixCharacters() {
    String payload = "abc1234";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code must have 6 characters");
  }

  @Test
  void shouldRejectInvalidCharacters() {
    String payload = "abc12!";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code contains invalid characters");
  }

  void shouldRejectReservedWord() {
    String payload = "health";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ShortCode(payload));

    assertThat(exception.getMessage()).isEqualTo("Short code is reserved");
  }

  @ParameterizedTest
  @ValueSource(strings = { "ab-123", "ab_123" })
  void shouldAllowUnderscoreAndHyphen(String payload) {
    String expected = payload;
    ShortCode result = new ShortCode(payload);

    assertThat(result.value()).isEqualTo(expected);
  }
}
