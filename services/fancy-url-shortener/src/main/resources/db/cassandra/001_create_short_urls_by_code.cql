CREATE TABLE IF NOT EXISTS short_urls_by_code (
  code text PRIMARY KEY,
  original_url text,
  created_at timestamp,
  expires_at timestamp
);
