package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository;

import java.util.Optional;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.ShortCode;

public interface ShortUrlRepository {
  boolean existsByShortCode(ShortCode shortCode);

  Optional<ShortUrl> findByShortCode(ShortCode shortCode);
}
