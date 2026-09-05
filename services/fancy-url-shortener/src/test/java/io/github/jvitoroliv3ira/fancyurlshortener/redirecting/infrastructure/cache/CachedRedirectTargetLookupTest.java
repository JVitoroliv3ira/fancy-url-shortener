package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.cache.CacheService;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public class CachedRedirectTargetLookupTest {
  private static final ShortCode SHORT_CODE = new ShortCode("abc123");
  private static final String CACHE_KEY = "redirect-target:abc123";
  private static final String TARGET_URL = "https://example.com.br";
  private static final Instant EXPIRES_AT = Instant.parse("2026-08-18T10:00:00Z");
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

  private RedirectTargetLookup delegate;
  private CacheService cacheService;
  private ObjectMapper objectMapper;
  private CachedRedirectTargetLookup lookup;

  @BeforeEach
  void setUp() {
    delegate = mock(RedirectTargetLookup.class);
    cacheService = mock(CacheService.class);
    objectMapper = JsonMapper.builder().findAndAddModules().build();
    lookup = new CachedRedirectTargetLookup(delegate, cacheService, objectMapper);
  }

  @Test
  void shouldReturnCachedRedirectTarget() {
    String cachedValue = "{\"url\":\"https://example.com.br\",\"expiresAt\":\"2026-08-18T10:00:00Z\"}";

    when(cacheService.get(CACHE_KEY)).thenReturn(Optional.of(cachedValue));

    Optional<RedirectTarget> result = lookup.findByShortCode(SHORT_CODE);

    assertThat(result).contains(new RedirectTarget(TARGET_URL, EXPIRES_AT));
    verifyNoInteractions(delegate);
  }

  @Test
  void shouldFindRedirectTargetFromDelegateWhenCacheMisses() {
    RedirectTarget redirectTarget = new RedirectTarget(TARGET_URL, EXPIRES_AT);

    when(cacheService.get(CACHE_KEY)).thenReturn(Optional.empty());
    when(delegate.findByShortCode(SHORT_CODE)).thenReturn(Optional.of(redirectTarget));

    Optional<RedirectTarget> result = lookup.findByShortCode(SHORT_CODE);

    assertThat(result).contains(redirectTarget);
    verify(delegate).findByShortCode(SHORT_CODE);
  }

  @Test
  void shouldStoreRedirectTargetInCacheWhenDelegateFindsIt() throws Exception {
    RedirectTarget redirectTarget = new RedirectTarget(TARGET_URL, EXPIRES_AT);

    when(cacheService.get(CACHE_KEY)).thenReturn(Optional.empty());
    when(delegate.findByShortCode(SHORT_CODE)).thenReturn(Optional.of(redirectTarget));

    lookup.findByShortCode(SHORT_CODE);

    ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
    verify(cacheService).put(eq(CACHE_KEY), valueCaptor.capture(), eq(DEFAULT_TTL));
    assertThat(objectMapper.readValue(valueCaptor.getValue(), RedirectTarget.class)).isEqualTo(redirectTarget);
  }

  @Test
  void shouldReturnEmptyWhenDelegateDoesNotFindRedirectTarget() {
    when(cacheService.get(CACHE_KEY)).thenReturn(Optional.empty());
    when(delegate.findByShortCode(SHORT_CODE)).thenReturn(Optional.empty());

    Optional<RedirectTarget> result = lookup.findByShortCode(SHORT_CODE);

    assertThat(result).isEmpty();
    verify(cacheService, never()).put(anyString(), anyString(), any(Duration.class));
  }

  @Test
  void shouldIgnoreInvalidCachedValueAndFindFromDelegate() {
    RedirectTarget redirectTarget = new RedirectTarget(TARGET_URL, EXPIRES_AT);
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;

    when(cacheService.get(CACHE_KEY)).thenReturn(Optional.of("not-json"));
    when(delegate.findByShortCode(SHORT_CODE)).thenReturn(Optional.of(redirectTarget));

    Optional<RedirectTarget> result;
    try {
      System.setErr(new PrintStream(stderr));
      result = lookup.findByShortCode(SHORT_CODE);
    } finally {
      System.setErr(originalErr);
    }

    assertThat(result).contains(redirectTarget);
    assertThat(stderr.toString()).isEmpty();
    verify(delegate).findByShortCode(SHORT_CODE);
  }
}
