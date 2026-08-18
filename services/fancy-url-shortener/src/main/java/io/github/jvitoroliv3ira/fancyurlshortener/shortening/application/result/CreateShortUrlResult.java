package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.result;

import java.time.Instant;

public record CreateShortUrlResult(String code, String originalUrl, Instant createdAt, Instant expiresAt) {
}
