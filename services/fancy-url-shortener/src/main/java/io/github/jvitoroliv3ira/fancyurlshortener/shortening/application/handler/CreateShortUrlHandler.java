package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.handler;

import java.time.Clock;

import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.command.CreateShortUrlCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.result.CreateShortUrlResult;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository.ShortUrlRepository;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.service.ShortCodeGenerator;

@Service
public class CreateShortUrlHandler {
  private final ShortUrlRepository shortUrlRepository;
  private final ShortCodeGenerator shortCodeGenerator;
  private final Clock clock;

  public CreateShortUrlHandler(ShortUrlRepository shortUrlRepository, ShortCodeGenerator shortCodeGenerator,
      Clock clock) {
    this.shortUrlRepository = shortUrlRepository;
    this.shortCodeGenerator = shortCodeGenerator;
    this.clock = clock;
  }

  public CreateShortUrlResult handle(CreateShortUrlCommand command) {
  }
}
