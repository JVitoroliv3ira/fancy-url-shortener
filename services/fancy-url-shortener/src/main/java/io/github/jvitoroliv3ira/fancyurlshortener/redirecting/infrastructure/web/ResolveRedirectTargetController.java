package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.infrastructure.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.command.ResolveRedirectTargetCommand;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.handler.ResolveRedirectTargetHandler;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.application.result.ResolveRedirectTargetResult;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

@RestController
@RequestMapping("/r")
public class ResolveRedirectTargetController {
  private final ResolveRedirectTargetHandler handler;

  public ResolveRedirectTargetController(ResolveRedirectTargetHandler handler) {
    this.handler = handler;
  }

  @GetMapping("/{code}")
  public ResponseEntity<Void> redirect(@PathVariable String code) {
    ResolveRedirectTargetCommand command = new ResolveRedirectTargetCommand(new ShortCode(code));

    ResolveRedirectTargetResult result = handler.handle(command);

    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(URI.create(result.url()))
        .build();
  }
}
