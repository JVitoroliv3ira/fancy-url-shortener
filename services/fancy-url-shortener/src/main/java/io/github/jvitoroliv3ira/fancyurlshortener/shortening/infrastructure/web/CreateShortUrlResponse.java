package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.web;

import java.time.Instant;

public record CreateShortUrlResponse(String code, String shortUrl, String originalUrl, Instant expiresAt) {
}
