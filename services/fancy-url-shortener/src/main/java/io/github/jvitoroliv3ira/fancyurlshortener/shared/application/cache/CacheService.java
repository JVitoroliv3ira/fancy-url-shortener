package io.github.jvitoroliv3ira.fancyurlshortener.shared.application.cache;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
  Optional<String> get(String key);

  void put(String key, String value, Duration ttl);
}
