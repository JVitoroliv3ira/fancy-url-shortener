package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject;

import java.net.URI;

public record OriginalUrl(String value) {
  public OriginalUrl {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Original URL is required");
    }

    URI uri = URI.create(value);
    String scheme = uri.getScheme();

    if (scheme == null) {
      throw new IllegalArgumentException("URL scheme is required");
    }

    if (!scheme.equals("http") && !scheme.equals("https")) {
      throw new IllegalArgumentException("Only HTTP and HTTPS URLs are allowed");
    }

    if (uri.getHost() == null || uri.getHost().isBlank()) {
      throw new IllegalArgumentException("URL host is required");
    }
  }
}
