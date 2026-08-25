package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public class SecureRandomShortCodeGeneratorTest {
  @Test
  void shouldGenerateShortCodeWithSixCharacters() {
    SecureRandomShortCodeGenerator generator = new SecureRandomShortCodeGenerator();

    ShortCode result = generator.generate();

    assertThat(result.value()).hasSize(6);
  }

  @Test
  void shouldGenerateOnlyBase62Characters() {
    SecureRandomShortCodeGenerator generator = new SecureRandomShortCodeGenerator();

    ShortCode result = generator.generate();

    assertThat(result.value()).matches("[a-zA-Z0-9]{6}");
  }

  @Test
  void shouldGenerateDifferentShortCodes() {
    SecureRandomShortCodeGenerator generator = new SecureRandomShortCodeGenerator();

    Set<String> results = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      results.add(generator.generate().value());
    }

    assertThat(results).hasSizeGreaterThan(1);
  }
}
