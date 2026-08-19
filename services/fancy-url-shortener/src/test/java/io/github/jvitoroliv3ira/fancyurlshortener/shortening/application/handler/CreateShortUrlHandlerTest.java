package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.command.CreateShortUrlCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.result.CreateShortUrlResult;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository.ShortUrlRepository;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.service.ShortCodeGenerator;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.ShortCode;

public class CreateShortUrlHandlerTest {
  private static final Instant NOW = Instant.parse("2026-08-18T10:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
  private static final Duration DEFAULT_EXPIRATION = Duration.ofDays(7);
  private static final String ORIGINAL_URL = "https://example.com.br";

  private ShortUrlRepository shortUrlRepository;
  private ShortCodeGenerator shortCodeGenerator;
  private CreateShortUrlHandler handler;

  @BeforeEach
  void setUp() {
    shortUrlRepository = mock(ShortUrlRepository.class);
    shortCodeGenerator = mock(ShortCodeGenerator.class);
    handler = new CreateShortUrlHandler(shortUrlRepository, shortCodeGenerator, CLOCK);
  }

  @Test
  void shouldCreateShortUrl() {
    CreateShortUrlCommand payload = commandWithoutExpiration();

    mockGeneratedCodes("abc123");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(true);

    CreateShortUrlResult result = handler.handle(payload);

    assertThat(result.code()).isEqualTo("abc123");
    assertThat(result.originalUrl()).isEqualTo(ORIGINAL_URL);
    assertThat(result.createdAt()).isEqualTo(NOW);
    assertThat(result.expiresAt()).isEqualTo(NOW.plus(DEFAULT_EXPIRATION));
  }

  @Test
  void shouldApplyDefaultExpirationWhenExpiresAtIsMissing() {
    Instant expected = NOW.plus(DEFAULT_EXPIRATION);
    CreateShortUrlCommand payload = commandWithoutExpiration();

    mockGeneratedCodes("abc123");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(true);

    CreateShortUrlResult result = handler.handle(payload);

    assertThat(result.expiresAt()).isEqualTo(expected);
    assertThat(capturedSavedShortUrl().expiresAt()).isEqualTo(expected);
  }

  @Test
  void shouldUseProvidedExpirationWhenPresent() {
    Instant expected = NOW.plus(Duration.ofDays(15));
    CreateShortUrlCommand payload = commandWithExpiration(expected);

    mockGeneratedCodes("abc123");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(true);

    CreateShortUrlResult result = handler.handle(payload);

    assertThat(result.expiresAt()).isEqualTo(expected);
    assertThat(capturedSavedShortUrl().expiresAt()).isEqualTo(expected);
  }

  @Test
  void shouldSaveShortUrl() {
    CreateShortUrlCommand payload = commandWithoutExpiration();

    mockGeneratedCodes("abc123");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(true);

    handler.handle(payload);

    ShortUrl saved = capturedSavedShortUrl();
    assertThat(saved.shortCode().value()).isEqualTo("abc123");
    assertThat(saved.originalUrl().value()).isEqualTo(ORIGINAL_URL);
    assertThat(saved.createdAt()).isEqualTo(NOW);
    assertThat(saved.expiresAt()).isEqualTo(NOW.plus(DEFAULT_EXPIRATION));
  }

  @Test
  void shouldRetryWhenGeneratedCodeAlreadyExists() {
    CreateShortUrlCommand payload = commandWithoutExpiration();

    mockGeneratedCodes("abc123", "xyz789");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(false).thenReturn(true);

    CreateShortUrlResult result = handler.handle(payload);

    assertThat(result.code()).isEqualTo("xyz789");
    verify(shortCodeGenerator, times(2)).generate();
    verify(shortUrlRepository, times(2)).saveIfAbsent(any(ShortUrl.class));
  }

  @Test
  void shouldFailWhenCodeGenerationKeepsColliding() {
    CreateShortUrlCommand payload = commandWithoutExpiration();

    mockGeneratedCodes("abc123", "def456", "ghi789", "jkl012", "mno345");
    when(shortUrlRepository.saveIfAbsent(any(ShortUrl.class))).thenReturn(false);

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> handler.handle(payload));

    assertThat(exception.getMessage()).isEqualTo("Could not generate a unique short URL");
    verify(shortCodeGenerator, times(5)).generate();
    verify(shortUrlRepository, times(5)).saveIfAbsent(any(ShortUrl.class));
  }

  @Test
  void shouldRejectNullCommand() {
    CreateShortUrlCommand payload = null;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> handler.handle(payload));

    assertThat(exception.getMessage()).isEqualTo("Command is required");
  }

  private void mockGeneratedCodes(String firstCode, String... remainingCodes) {
    OngoingStubbing<ShortCode> stubbing = when(shortCodeGenerator.generate()).thenReturn(new ShortCode(firstCode));

    for (String code : remainingCodes) {
      stubbing = stubbing.thenReturn(new ShortCode(code));
    }
  }

  private ShortUrl capturedSavedShortUrl() {
    ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
    verify(shortUrlRepository).saveIfAbsent(captor.capture());
    return captor.getValue();
  }

  private CreateShortUrlCommand commandWithoutExpiration() {
    return new CreateShortUrlCommand(ORIGINAL_URL, null);
  }

  private CreateShortUrlCommand commandWithExpiration(Instant expiresAt) {
    return new CreateShortUrlCommand(ORIGINAL_URL, expiresAt);
  }
}
