package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;

import org.junit.jupiter.api.Test;

public class RedirectTargetTest {
  private static final String url = "http://example.com.br";
  private static Instant expiresAt = Instant.parse("2026-07-01T00:00:00Z");

  @Test
  void shouldNotBeExpiredBeforeExpiration() {
    Instant payload = Instant.parse("2026-06-01T00:00:00Z");

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isExpired(payload)).isFalse();
  }

  @Test
  void shouldBeExpiredAtExpirationInstant() {
    Instant payload = expiresAt;

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeExpiredAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isExpired(payload)).isTrue();
  }

  @Test
  void shouldBeRedirectableBeforeExpiration() {
    Instant payload = Instant.parse("2026-06-01T00:00:00Z");

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isRedirectableAt(payload)).isTrue();
  }

  @Test
  void shouldNotBeRedirectableAtExpirationInstant() {
    Instant payload = expiresAt;

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isRedirectableAt(payload)).isFalse();
  }

  @Test
  void shouldNotBeRedirectableAfterExpiration() {
    Instant payload = Instant.parse("2026-08-15T11:00:01Z");

    RedirectTarget redirectTarget = new RedirectTarget(url, expiresAt);

    assertThat(redirectTarget.isRedirectableAt(payload)).isFalse();
  }
}
