package io.github.jvitoroliv3ira.fancyurlshortener.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
  @Bean
  WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/urls")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("POST", "OPTIONS")
            .allowedHeaders("Content-Type")
            .maxAge(3600);
      }
    };
  }
}
