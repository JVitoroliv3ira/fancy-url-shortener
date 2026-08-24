package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.web;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlRequest(@NotBlank String url, Instant expiresAt) {
}
