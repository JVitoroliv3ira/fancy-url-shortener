package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity;

import java.time.Instant;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.OriginalUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.ShortCode;

public class ShortUrl {
  private final ShortCode shortCode;
  private final OriginalUrl originalUrl;
  private final Instant createdAt;
  private final Instant expiresAt;

  private ShortUrl(ShortCode shortCode, OriginalUrl originalUrl, Instant createdAt, Instant expiresAt) {
    this.shortCode = shortCode;
    this.originalUrl = originalUrl;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
  }

  public static ShortUrl create(ShortCode shortCode, OriginalUrl originalUrl, Instant createdAt, Instant expiresAt) {
    if (shortCode == null) {
      throw new IllegalArgumentException("Short code is required");
    }

    if (originalUrl == null) {
      throw new IllegalArgumentException("Original URL is required");
    }

    if (createdAt == null) {
      throw new IllegalArgumentException("Created at is required");
    }

    if (expiresAt == null) {
      throw new IllegalArgumentException("Expires at is required");
    }

    if (!expiresAt.isAfter(createdAt)) {
      throw new IllegalArgumentException("Expiration date must be after creation date");
    }

    return new ShortUrl(shortCode, originalUrl, createdAt, expiresAt);
  }

  public boolean isExpired(Instant now) {
    return expiresAt != null && !expiresAt.isAfter(now);
  }

  public boolean isRedirectableAt(Instant now) {
    return !isExpired(now);
  }

  public ShortCode shortCode() {
    return shortCode;
  }

  public OriginalUrl originalUrl() {
    return originalUrl;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant expiresAt() {
    return expiresAt;
  }
}
