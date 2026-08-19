package io.github.jvitoroliv3ira.fancyurlshortener;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Enable when infrastructure adapters are implemented")
@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.cassandra.autoconfigure.CassandraAutoConfiguration")
class FancyUrlShortenerApplicationTests {

  @Test
  void contextLoads() {
  }

}
