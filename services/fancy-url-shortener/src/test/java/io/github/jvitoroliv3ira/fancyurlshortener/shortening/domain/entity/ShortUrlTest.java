package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.OriginalUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public class ShortUrlTest {
  private final ShortCode shortCode = new ShortCode("abc123");
  private final OriginalUrl originalUrl = new OriginalUrl("https://example.com");
  private final Instant createdAt = Instant.parse("2026-08-15T10:00:00Z");
  private final Instant expiresAt = Instant.parse("2026-08-15T11:00:00Z");

  @Test
  void shouldCreateShortUrl() {
    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.shortCode()).isEqualTo(shortCode);
    assertThat(result.originalUrl()).isEqualTo(originalUrl);
    assertThat(result.createdAt()).isEqualTo(createdAt);
    assertThat(result.expiresAt()).isEqualTo(expiresAt);
  }

  @Test
  void shouldRejectNullShortCode() {
    ShortCode payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(payload, originalUrl, createdAt, expiresAt));

    assertThat(exception.getMessage()).isEqualTo("Short code is required");
  }

  @Test
  void shouldRejectNullOriginalUrl() {
    OriginalUrl payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(shortCode, payload, createdAt, expiresAt));

    assertThat(exception.getMessage()).isEqualTo("Original URL is required");
  }

  @Test
  void shouldRejectNullCreatedAt() {
    Instant payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(shortCode, originalUrl, payload, expiresAt));

    assertThat(exception.getMessage()).isEqualTo("Created at is required");
  }

  @Test
  void shouldRejectNullExpiresAt() {
    Instant payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(shortCode, originalUrl, createdAt, payload));

    assertThat(exception.getMessage()).isEqualTo("Expires at is required");
  }

  @Test
  void shouldRejectExpirationEqualToCreatedAt() {
    Instant payload = createdAt;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(shortCode, originalUrl, createdAt, payload));

    assertThat(exception.getMessage()).isEqualTo("Expiration date must be after creation date");
  }

  @Test
  void shouldRejectExpirationBeforeCreatedAt() {
    Instant payload = Instant.parse("2026-08-15T09:59:59Z");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ShortUrl.create(shortCode, originalUrl, createdAt, payload));

    assertThat(exception.getMessage()).isEqualTo("Expiration date must be after creation date");
  }

  @Test
  void shouldNotBeExpiredBeforeExpiration() {
    Instant payload = Instant.parse("2026-08-15T10:59:59Z");

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isExpired(payload)).isFalse();
  }

  @Test
  void shouldBeExpiredAtExpirationInstant() {
    Instant payload = expiresAt;

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeExpiredAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeRedirectableBeforeExpiration() {
    Instant payload = Instant.parse("2026-08-15T10:59:59Z");

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isRedirectableAt(payload)).isTrue();
  }

  @Test
  void shouldNotBeRedirectableAtExpirationInstant() {
    Instant payload = expiresAt;

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isRedirectableAt(payload)).isFalse();
  }

  @Test
  void shouldNotBeRedirectableAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    ShortUrl result = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

    assertThat(result.isRedirectableAt(payload)).isFalse();
  }
}
