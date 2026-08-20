package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository.ShortUrlRepository;

@Repository
public class CassandraShortUrlRepository implements ShortUrlRepository {
  public boolean saveIfAbsent(ShortUrl shortUrl) {
    return true;
  }
}
