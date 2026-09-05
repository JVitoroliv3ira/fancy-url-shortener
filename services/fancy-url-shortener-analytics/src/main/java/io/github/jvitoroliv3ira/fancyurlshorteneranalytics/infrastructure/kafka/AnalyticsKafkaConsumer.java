package io.github.jvitoroliv3ira.fancyurlshorteneranalytics.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.github.jvitoroliv3ira.fancyurlshorteneranalytics.infrastructure.metrics.AnalyticsMetrics;

@Component
public class AnalyticsKafkaConsumer {
  private final AnalyticsMetrics metrics;

  public AnalyticsKafkaConsumer(AnalyticsMetrics metrics) {
    this.metrics = metrics;
  }

  @KafkaListener(topics = "url-shortener.short-url-created", groupId = "${spring.kafka.consumer.group-id}")
  public void onShortUrlCreated(String payload) {
    metrics.incrementShortUrlCreated();
  }

  @KafkaListener(topics = "url-shortener.redirect-resolved", groupId = "${spring.kafka.consumer.group-id}")
  public void onRedirectResolved(String payload) {
    metrics.incrementRedirectResolved();
  }

  @KafkaListener(topics = "url-shortener.redirect-not-found", groupId = "${spring.kafka.consumer.group-id}")
  public void onRedirectNotFound(String payload) {
    metrics.incrementRedirectNotFound();
  }

  @KafkaListener(topics = "url-shortener.redirect-expired", groupId = "${spring.kafka.consumer.group-id}")
  public void onRedirectExpired(String payload) {
    metrics.incrementRedirectExpired();
  }
}
