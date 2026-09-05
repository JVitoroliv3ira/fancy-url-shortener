package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.event;

import java.time.Instant;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.ApplicationEvent;

public record RedirectResolvedEvent(String shortCode, String originalUrl, Instant expiresAt)
    implements ApplicationEvent {
  public String topic() {
    return "url-shortener.redirect-resolved";
  }

  public String key() {
    return shortCode;
  }
}
