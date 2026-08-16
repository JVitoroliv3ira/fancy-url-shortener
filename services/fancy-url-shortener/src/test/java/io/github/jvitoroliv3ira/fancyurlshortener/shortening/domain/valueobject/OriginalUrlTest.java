package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class OriginalUrlTest {
  @ParameterizedTest
  @ValueSource(strings = { "http://example.com", "https://example.com" })
  void shouldCreateValidUrl(String payload) {
    String expected = payload;

    OriginalUrl result = new OriginalUrl(payload);

    assertThat(result.value()).isEqualTo(expected);
  }
}
