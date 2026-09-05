package io.github.jvitoroliv3ira.fancyurlshorteneranalytics.infrastructure.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class AnalyticsMetrics {
  private final Counter shortUrlCreated;
  private final Counter redirectResolved;
  private final Counter redirectNotFound;
  private final Counter redirectExpired;

  public AnalyticsMetrics(MeterRegistry meterRegistry) {
    this.shortUrlCreated = Counter.builder("url_shortener_urls_created_total")
        .description("Total short URLs created")
        .register(meterRegistry);

    this.redirectResolved = Counter.builder("url_shortener_redirects_total")
        .description("Total redirects by result")
        .tag("result", "resolved")
        .register(meterRegistry);

    this.redirectNotFound = Counter.builder("url_shortener_redirects_total")
        .description("Total redirects by result")
        .tag("result", "not_found")
        .register(meterRegistry);

    this.redirectExpired = Counter.builder("url_shortener_redirects_total")
        .description("Total redirects by result")
        .tag("result", "expired")
        .register(meterRegistry);
  }

  public void incrementShortUrlCreated() {
    shortUrlCreated.increment();
  }

  public void incrementRedirectResolved() {
    redirectResolved.increment();
  }

  public void incrementRedirectNotFound() {
    redirectNotFound.increment();
  }

  public void incrementRedirectExpired() {
    redirectExpired.increment();
  }
}
