package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.generator;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.service.ShortCodeGenerator;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.valueobject.ShortCode;

@Component
public class SecureRandomShortCodeGenerator implements ShortCodeGenerator {
  private static final int CODE_LENGTH = 6;
  private static final char[] B62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
      .toCharArray();
  private final SecureRandom random;

  public SecureRandomShortCodeGenerator() {
    this(new SecureRandom());
  }

  SecureRandomShortCodeGenerator(SecureRandom random) {
    this.random = random;
  }

  @Override
  public ShortCode generate() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);

    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = random.nextInt(B62_ALPHABET.length);
      code.append(B62_ALPHABET[index]);
    }

    return new ShortCode(code.toString());
  }
}
