package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class RedirectTargetTest {
  private static final String URL = "http://example.com.br";
  private static final Instant EXPIRES_AT = Instant.parse("2026-07-01T00:00:00Z");

  @Test
  void shouldRejectNullUrl() {
    String payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new RedirectTarget(payload, EXPIRES_AT));

    assertThat(exception.getMessage()).isEqualTo("Url is required");
  }

  @Test
  void shouldRejectBlankUrl() {
    String payload = "      ";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new RedirectTarget(payload, EXPIRES_AT));

    assertThat(exception.getMessage()).isEqualTo("Url is required");
  }

  @Test
  void shouldRejectNullExpiresAt() {
    Instant payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new RedirectTarget(URL, payload));

    assertThat(exception.getMessage()).isEqualTo("Expires at is required");
  }

  @Test
  void shouldRejectNullCurrentInstantWhenCheckingExpiration() {
    Instant payload = null;
    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> redirectTarget.isExpired(payload));

    assertThat(exception.getMessage()).isEqualTo("Current instant is required");
  }

  @Test
  void shouldRejectNullCurrentInstantWhenCheckingRedirectable() {
    Instant payload = null;
    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> redirectTarget.isRedirectableAt(payload));

    assertThat(exception.getMessage()).isEqualTo("Current instant is required");
  }

  @Test
  void shouldNotBeExpiredBeforeExpiration() {
    Instant payload = Instant.parse("2026-06-01T00:00:00Z");

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isExpired(payload)).isFalse();
  }

  @Test
  void shouldBeExpiredAtExpirationInstant() {
    Instant payload = EXPIRES_AT;

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeExpiredAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeRedirectableBeforeExpiration() {
    Instant payload = Instant.parse("2026-06-01T00:00:00Z");

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isRedirectableAt(payload)).isTrue();
  }

  @Test
  void shouldNotBeRedirectableAtExpirationInstant() {
    Instant payload = EXPIRES_AT;

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isRedirectableAt(payload)).isFalse();
  }

  @Test
  void shouldNotBeRedirectableAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    RedirectTarget redirectTarget = new RedirectTarget(URL, EXPIRES_AT);

    assertThat(redirectTarget.isRedirectableAt(payload)).isFalse();
  }
}
