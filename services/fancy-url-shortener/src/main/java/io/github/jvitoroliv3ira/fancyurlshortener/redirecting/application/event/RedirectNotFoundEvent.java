package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.event;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.ApplicationEvent;

public record RedirectNotFoundEvent(String shortCode) implements ApplicationEvent {
  public String topic() {
    return "url-shortener.redirect-not-found";
  }

  public String key() {
    return shortCode;
  }
}
