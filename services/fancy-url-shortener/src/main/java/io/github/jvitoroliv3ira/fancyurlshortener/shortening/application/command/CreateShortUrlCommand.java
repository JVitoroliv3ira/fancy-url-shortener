package io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.command;

import java.time.Instant;

public record CreateShortUrlCommand(String originalUrl, Instant expiresAt) {
}
