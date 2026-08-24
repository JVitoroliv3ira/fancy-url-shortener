package io.github.jvitoroliv3ira.fancyurlshortener.shortening.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;

import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.entity.ShortUrl;
import io.github.jvitoroliv3ira.fancyurlshortener.shortening.domain.repository.ShortUrlRepository;

@Repository
public class CassandraShortUrlRepository implements ShortUrlRepository {
  private static final String INSERT_IF_ABSENT_QUERY = """
        INSERT INTO short_urls_by_code (
          code,
          original_url,
          created_at,
          expires_at
        )
        VALUES (?, ?, ?, ?)
        IF NOT EXISTS
      """;

  private final CqlSession session;
  private final PreparedStatement insertIfAbsentStatement;

  public CassandraShortUrlRepository(CqlSession session) {
    this.session = session;
    this.insertIfAbsentStatement = session.prepare(INSERT_IF_ABSENT_QUERY);
  }

  @Override
  public boolean saveIfAbsent(ShortUrl shortUrl) {
    BoundStatement statement = insertIfAbsentStatement.bind(
        shortUrl.shortCode().value(),
        shortUrl.originalUrl().value(),
        shortUrl.createdAt(),
        shortUrl.expiresAt());

    Row row = session.execute(statement).one();

    return row != null && row.getBoolean("[applied]");
  }
}
