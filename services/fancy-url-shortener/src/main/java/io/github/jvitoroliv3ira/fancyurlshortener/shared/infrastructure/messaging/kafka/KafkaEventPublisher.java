package io.github.jvitoroliv3ira.fancyurlshortener.shared.infrastructure.messaging.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.ApplicationEvent;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event.EventPublisher;

@Component
public class KafkaEventPublisher implements EventPublisher {
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(ApplicationEvent event) {
    this.kafkaTemplate.send(event.topic(), event.key(), event);
  }
}
