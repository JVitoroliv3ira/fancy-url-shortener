package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.handler;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command.ResolveRedirectTargetCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.result.ResolveRedirectTargetResult;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;

@Service
public class ResolveRedirectTargetHandler {
  private final RedirectTargetLookup redirectTargetLookup;
  private final Clock clock;

  public ResolveRedirectTargetHandler(RedirectTargetLookup redirectTargetLookup, Clock clock) {
    this.redirectTargetLookup = redirectTargetLookup;
    this.clock = clock;
  }

  public ResolveRedirectTargetResult handle(ResolveRedirectTargetCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Command is required");
    }

    Instant now = clock.instant();

    Optional<RedirectTarget> result = redirectTargetLookup.findByShortCode(command.shortCode());

    if (result.isEmpty()) {
      throw new IllegalStateException("Redirect target not found");
    }

    RedirectTarget redirectTarget = result.get();

    if (!redirectTarget.isRedirectableAt(now)) {
      throw new IllegalStateException("Redirect target is expired");
    }

    return new ResolveRedirectTargetResult(redirectTarget.url());
  }
}
