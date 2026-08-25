package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command;

import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public record ResolveRedirectTargetCommand(ShortCode shortCode) {
}
