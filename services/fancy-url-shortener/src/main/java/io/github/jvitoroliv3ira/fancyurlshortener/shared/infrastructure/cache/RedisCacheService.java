package io.github.jvitoroliv3ira.fancyurlshortener.shared.infrastructure.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.cache.CacheService;

@Service
public class RedisCacheService implements CacheService {

  private final StringRedisTemplate redisTemplate;

  public RedisCacheService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Optional<String> get(String key) {
    String result = redisTemplate.opsForValue().get(key);
    return result == null ? Optional.empty() : Optional.of(result);
  }

  public void put(String key, String value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl);
  }
}
