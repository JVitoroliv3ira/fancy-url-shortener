package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.service;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public interface ShortCodeGenerator {
  ShortCode generate();
}
