package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository;

import java.util.Optional;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

public interface RedirectTargetLookup {
  Optional<RedirectTarget> findByShortCode(ShortCode shortCode);
}
