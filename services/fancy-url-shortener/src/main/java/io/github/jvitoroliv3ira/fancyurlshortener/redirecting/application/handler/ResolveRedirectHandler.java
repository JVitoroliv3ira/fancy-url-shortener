package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.handler;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command.ResolveRedirectCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.event.RedirectExpiredEvent;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.event.RedirectNotFoundEvent;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.event.RedirectResolvedEvent;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetExpiredException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetNotFoundException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.result.ResolveRedirectResult;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.EventPublisher;

@Service
public class ResolveRedirectHandler {
  private final RedirectTargetLookup redirectTargetLookup;
  private final EventPublisher eventPublisher;
  private final Clock clock;

  public ResolveRedirectHandler(
      RedirectTargetLookup redirectTargetLookup,
      EventPublisher eventPublisher,
      Clock clock) {
    this.redirectTargetLookup = redirectTargetLookup;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  public ResolveRedirectResult handle(ResolveRedirectCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Command is required");
    }

    Instant now = clock.instant();

    Optional<RedirectTarget> result = redirectTargetLookup.findByShortCode(command.shortCode());

    if (result.isEmpty()) {
      eventPublisher.publish(new RedirectNotFoundEvent(command.shortCode().value()));
      throw new RedirectTargetNotFoundException();
    }

    RedirectTarget redirectTarget = result.get();

    if (!redirectTarget.isRedirectableAt(now)) {
      eventPublisher.publish(new RedirectExpiredEvent(command.shortCode().value(), redirectTarget.expiresAt()));
      throw new RedirectTargetExpiredException();
    }

    eventPublisher.publish(
        new RedirectResolvedEvent(command.shortCode().value(), redirectTarget.url(), redirectTarget.expiresAt()));
    return new ResolveRedirectResult(redirectTarget.url());
  }
}
