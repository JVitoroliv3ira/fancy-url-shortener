package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.handler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.command.CreateShortUrlCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.result.CreateShortUrlResult;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository.ShortUrlRepository;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.service.ShortCodeGenerator;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.OriginalUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

@Service
public class CreateShortUrlHandler {
  private final ShortUrlRepository shortUrlRepository;
  private final ShortCodeGenerator shortCodeGenerator;
  private final Clock clock;

  private static final int MAX_GENERATION_ATTEMPTS = 5;
  private static final Duration DEFAULT_EXPIRATION = Duration.ofDays(7);

  public CreateShortUrlHandler(ShortUrlRepository shortUrlRepository, ShortCodeGenerator shortCodeGenerator,
      Clock clock) {
    this.shortUrlRepository = shortUrlRepository;
    this.shortCodeGenerator = shortCodeGenerator;
    this.clock = clock;
  }

  public CreateShortUrlResult handle(CreateShortUrlCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Command is required");
    }

    Instant createdAt = clock.instant();
    Instant expiresAt = command.expiresAt() != null ? command.expiresAt() : createdAt.plus(DEFAULT_EXPIRATION);
    OriginalUrl originalUrl = new OriginalUrl(command.originalUrl());

    for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
      ShortCode shortCode = shortCodeGenerator.generate();
      ShortUrl shortUrl = ShortUrl.create(shortCode, originalUrl, createdAt, expiresAt);

      if (shortUrlRepository.saveIfAbsent(shortUrl)) {
        return new CreateShortUrlResult(
            shortUrl.shortCode().value(),
            shortUrl.originalUrl().value(),
            createdAt,
            expiresAt);
      }
    }

    throw new IllegalStateException("Could not generate a unique short URL");
  }
}
