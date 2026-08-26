package io.github.jvitoroliv3ira.fancyurlshortener.shared.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetExpiredException;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.exception.RedirectTargetNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RedirectTargetNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRedirectTargetNotFound(
      RedirectTargetNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(RedirectTargetExpiredException.class)
  public ResponseEntity<ErrorResponse> handleRedirectTargetExpired(
      RedirectTargetExpiredException ex) {
    return ResponseEntity.status(HttpStatus.GONE).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
  }
}
