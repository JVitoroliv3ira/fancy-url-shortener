package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.handler;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command.ResolveRedirectCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetExpiredException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetNotFoundException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.result.ResolveRedirectTargetResult;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;

@Service
public class ResolveRedirectHandler {
  private final RedirectTargetLookup redirectTargetLookup;
  private final Clock clock;

  public ResolveRedirectHandler(RedirectTargetLookup redirectTargetLookup, Clock clock) {
    this.redirectTargetLookup = redirectTargetLookup;
    this.clock = clock;
  }

  public ResolveRedirectTargetResult handle(ResolveRedirectCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Command is required");
    }

    Instant now = clock.instant();

    Optional<RedirectTarget> result = redirectTargetLookup.findByShortCode(command.shortCode());

    if (result.isEmpty()) {
      throw new RedirectTargetNotFoundException();
    }

    RedirectTarget redirectTarget = result.get();

    if (!redirectTarget.isRedirectableAt(now)) {
      throw new RedirectTargetExpiredException();
    }

    return new ResolveRedirectTargetResult(redirectTarget.url());
  }
}
