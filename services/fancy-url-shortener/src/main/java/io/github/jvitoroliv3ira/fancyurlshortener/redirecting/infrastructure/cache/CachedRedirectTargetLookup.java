package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.infrastructure.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.cache.CacheService;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

@Component
@Primary
public class CachedRedirectTargetLookup implements RedirectTargetLookup {
  private final RedirectTargetLookup delegate;
  private final CacheService cacheService;
  private final ObjectMapper objectMapper;

  private static final String CACHE_KEY = "redirect-target:";
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

  public CachedRedirectTargetLookup(
      RedirectTargetLookup delegate,
      CacheService cacheService,
      ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cacheService = cacheService;
    this.objectMapper = objectMapper;
  }

  public Optional<RedirectTarget> findByShortCode(ShortCode shortCode) {
    String cacheKey = CACHE_KEY + shortCode.value();

    Optional<RedirectTarget> cachedRedirectTarget = cacheService.get(cacheKey).flatMap(this::deserialize);

    if (cachedRedirectTarget.isPresent()) {
      return cachedRedirectTarget;
    }

    Optional<RedirectTarget> redirectTarget = delegate.findByShortCode(shortCode);

    redirectTarget.flatMap(this::serialize).ifPresent(target -> {
      cacheService.put(cacheKey, target, DEFAULT_TTL);
    });

    return redirectTarget;
  }

  private Optional<String> serialize(RedirectTarget redirectTarget) {
    try {
      return Optional.of(objectMapper.writeValueAsString(redirectTarget));
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
      return Optional.empty();
    }
  }

  private Optional<RedirectTarget> deserialize(String value) {
    try {
      return Optional.of(objectMapper.readValue(value, RedirectTarget.class));
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
      return Optional.empty();
    }
  }
}
