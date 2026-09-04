package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.event;

import java.time.Instant;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.ApplicationEvent;

public record ShortUrlCreatedEvent(
    String shortCode,
    String originalUrl,
    Instant createdAt,
    Instant expiresAt) implements ApplicationEvent {
  public String topic() {
    return "url-shortener.short-url-created";
  }

  public String key() {
    return shortCode;
  }
}
