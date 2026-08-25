package io.github.jvitoroliv3ira.fancyurlshortener.redirecting.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;

import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.repository.RedirectTargetLookup;
import io.github.jvitoroliv3ira.fancyurlshortener.redirecting.domain.valueobject.RedirectTarget;
import io.github.jvitoroliv3ira.fancyurlshortener.shared.domain.valueobject.ShortCode;

@Repository
public class CassandraRedirectTargetLookup implements RedirectTargetLookup {
  private static final String FIND_BY_SHORT_CODE_QUERY = """
      SELECT
        original_url,
        expires_at
      FROM
        short_urls_by_code
      WHERE
        code = ?;
      """;
  private final CqlSession session;
  private final PreparedStatement findByShortCodeStatement;

  public CassandraRedirectTargetLookup(CqlSession session) {
    this.session = session;
    this.findByShortCodeStatement = session.prepare(FIND_BY_SHORT_CODE_QUERY);
  }

  @Override
  public Optional<RedirectTarget> findByShortCode(ShortCode shortCode) {

    BoundStatement statement = findByShortCodeStatement.bind(
        shortCode.value());

    Row row = session.execute(statement).one();

    if (row == null) {
      return Optional.empty();
    }

    return Optional.of(
        new RedirectTarget(row.getString("original_url"), row.getInstant("expires_at")));
  }
}
