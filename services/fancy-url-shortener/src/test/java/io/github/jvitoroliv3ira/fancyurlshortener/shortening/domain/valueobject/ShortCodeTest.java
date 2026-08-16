package io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ShortCodeTest {
  @Test
  void shouldCreateValidShortCode() {
    String expected = "abc123";
    String payload = "abc123";

    ShortCode result = new ShortCode(payload);

    assertThat(result.value()).isEqualTo(expected);

  }
}
