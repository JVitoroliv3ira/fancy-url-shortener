package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.command.CreateShortUrlCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.handler.CreateShortUrlHandler;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.application.result.CreateShortUrlResult;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/urls")
public class CreateShortUrlController {
  private final CreateShortUrlHandler handler;

  public CreateShortUrlController(CreateShortUrlHandler handler) {
    this.handler = handler;
  }

  @PostMapping
  public ResponseEntity<CreateShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request) {
    CreateShortUrlCommand command = new CreateShortUrlCommand(request.url(), request.expiresAt());

    CreateShortUrlResult result = handler.handle(command);

    CreateShortUrlResponse response = new CreateShortUrlResponse(result.code(),
        "http://localhost:8000/" + result.code(),
        result.originalUrl(), result.expiresAt());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
