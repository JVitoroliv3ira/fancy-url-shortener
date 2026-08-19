package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;

public interface ShortUrlRepository {
  boolean saveIfAbsent(ShortUrl shortUrl);
}
