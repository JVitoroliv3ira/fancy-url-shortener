package io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public record ShortCode(String value) {
  private static final Set<String> RESERVED_WORDS = Set.of(
      "api",
      "docs",
      "admin",
      "health",
      "swagger",
      "actuator");

  private static final Pattern ALLOWED_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

  public ShortCode {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Short code is required");
    }

    value = value.trim();

    if (value.length() != 6) {
      throw new IllegalArgumentException("Short code must have 6 characters");
    }

    if (!ALLOWED_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Short code contains invalid characters");
    }

    if (isReserved(value)) {
      throw new IllegalArgumentException("Short code is reserved");
    }
  }

  private static boolean isReserved(String value) {
    return RESERVED_WORDS.contains(value.toLowerCase(Locale.ROOT));
  }
}
