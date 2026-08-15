package io.github.jvitoroliv3ira.fancyurlshortener;

import org.springframework.boot.SpringApplication;

public class TestFancyUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.from(FancyUrlShortenerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
