package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command.ResolveRedirectCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetExpiredException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetNotFoundException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.result.ResolveRedirectResult;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public class ResolveRedirectHandlerTest {
  private static final Instant NOW = Instant.parse("2026-08-18T10:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
  private static final ShortCode SHORT_CODE = new ShortCode("abc123");
  private static final String TARGET_URL = "https://example.com.br";

  private RedirectTargetLookup redirectTargetLookup;
  private ResolveRedirectHandler handler;

  @BeforeEach
  void setUp() {
    redirectTargetLookup = mock(RedirectTargetLookup.class);
    handler = new ResolveRedirectHandler(redirectTargetLookup, CLOCK);
  }

  @Test
  void shouldResolveRedirect() {
    ResolveRedirectCommand payload = new ResolveRedirectCommand(SHORT_CODE);
    RedirectTarget redirectTarget = new RedirectTarget(TARGET_URL, NOW.plusSeconds(60));

    when(redirectTargetLookup.findByShortCode(SHORT_CODE)).thenReturn(Optional.of(redirectTarget));

    ResolveRedirectResult result = handler.handle(payload);

    assertThat(result.url()).isEqualTo(TARGET_URL);
    verify(redirectTargetLookup).findByShortCode(SHORT_CODE);
  }

  @Test
  void shouldRejectNullCommand() {
    ResolveRedirectCommand payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> handler.handle(payload));

    assertThat(exception.getMessage()).isEqualTo("Command is required");
  }

  @Test
  void shouldThrowNotFoundWhenRedirectTargetDoesNotExist() {
    ResolveRedirectCommand payload = new ResolveRedirectCommand(SHORT_CODE);

    when(redirectTargetLookup.findByShortCode(SHORT_CODE)).thenReturn(Optional.empty());

    RedirectTargetNotFoundException exception = assertThrows(RedirectTargetNotFoundException.class,
        () -> handler.handle(payload));

    assertThat(exception.getMessage()).isEqualTo("Redirect target not found");
  }

  @Test
  void shouldThrowExpiredWhenRedirectTargetIsExpired() {
    ResolveRedirectCommand payload = new ResolveRedirectCommand(SHORT_CODE);
    RedirectTarget redirectTarget = new RedirectTarget(TARGET_URL, NOW);

    when(redirectTargetLookup.findByShortCode(SHORT_CODE)).thenReturn(Optional.of(redirectTarget));

    RedirectTargetExpiredException exception = assertThrows(RedirectTargetExpiredException.class,
        () -> handler.handle(payload));

    assertThat(exception.getMessage()).isEqualTo("Redirect target expired");
  }
}
