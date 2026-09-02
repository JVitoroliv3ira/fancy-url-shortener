package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject;

import java.time.Instant;

public record RedirectTarget(String url, Instant expiresAt) {
  public RedirectTarget {
    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException("Url is required");
    }

    if (expiresAt == null) {
      throw new IllegalArgumentException("Expires at is required");
    }
  }

  public boolean isExpired(Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("Current instant is required");
    }

    return !expiresAt.isAfter(now);
  }

  public boolean isRedirectableAt(Instant now) {
    return !isExpired(now);
  }
}
