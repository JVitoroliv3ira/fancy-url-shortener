package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class OriginalUrlTest {
  @ParameterizedTest
  @ValueSource(strings = { "http://example.com", "https://example.com" })
  void shouldCreateValidUrl(String payload) {
    String expected = payload;

    OriginalUrl result = new OriginalUrl(payload);

    assertThat(result.value()).isEqualTo(expected);
  }

  @Test
  void shouldRejectNullUrl() {
    String payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OriginalUrl(payload));

    assertThat(exception.getMessage()).isEqualTo("Original URL is required");
  }

  @Test
  void shouldRejectBlankUrl() {
    String payload = "      ";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OriginalUrl(payload));

    assertThat(exception.getMessage()).isEqualTo("Original URL is required");
  }

  @Test
  void shouldRejectUrlWithoutScheme() {
    String payload = "example.com";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OriginalUrl(payload));

    assertThat(exception.getMessage()).isEqualTo("URL scheme is required");
  }

  @ParameterizedTest
  @ValueSource(strings = { "ftp://example.com", "file:///tmp/file.txt", "mailto:test@example.com" })
  void shouldRejectUnsupportedScheme(String payload) {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OriginalUrl(payload));

    assertThat(exception.getMessage()).isEqualTo("Only HTTP and HTTPS URLs are allowed");
  }

  @Test
  void shouldRejectUrlWithoutHost() {
    String payload = "https:///path";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OriginalUrl(payload));

    assertThat(exception.getMessage()).isEqualTo("URL host is required");
  }
}
