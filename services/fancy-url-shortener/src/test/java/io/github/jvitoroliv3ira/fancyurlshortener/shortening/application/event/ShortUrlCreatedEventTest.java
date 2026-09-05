package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class ShortUrlCreatedEventTest {
  @Test
  void shouldUseShortUrlCreatedTopic() {
    ShortUrlCreatedEvent event = event();

    assertThat(event.topic()).isEqualTo("url-shortener.short-url-created");
  }

  @Test
  void shouldUseShortCodeAsKey() {
    ShortUrlCreatedEvent event = event();

    assertThat(event.key()).isEqualTo("abc123");
  }

  private ShortUrlCreatedEvent event() {
    return new ShortUrlCreatedEvent(
        "abc123",
        "https://example.com.br",
        Instant.parse("2026-08-18T10:00:00Z"),
        Instant.parse("2026-08-25T10:00:00Z"));
  }
}
