package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception;

public class RedirectTargetNotFoundException extends RuntimeException {
  public RedirectTargetNotFoundException() {
    super("Redirect target not found");
  }
}
