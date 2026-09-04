package io.github.jvitoroliv3ira.fancyurlshortener.shared.application.event;

public interface ApplicationEvent {
  String topic();

  String key();
}
