package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception;

public class RedirectTargetExpiredException extends RuntimeException {
  public RedirectTargetExpiredException() {
    super("Redirect target expired");
  }
}
