#!/usr/bin/env bash
if [ -z "${BASH_VERSION:-}" ]; then
  exec bash "$0" "$@"
fi

set -euo pipefail

container_name="fancy-cassandra"
keyspace="fancy_url_shortener"
migrations_dir="services/fancy-url-shortener/src/main/resources/db/cassandra"
max_attempts=60

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is required to apply Cassandra migrations." >&2
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running or is not accessible." >&2
  exit 1
fi

if ! docker container inspect "$container_name" >/dev/null 2>&1; then
  echo "Cassandra container does not exist: $container_name" >&2
  echo "Run: make cassandra-up" >&2
  exit 1
fi

if [[ "$(docker inspect -f '{{.State.Running}}' "$container_name")" != "true" ]]; then
  echo "Cassandra container is not running: $container_name" >&2
  echo "Run: make cassandra-up" >&2
  exit 1
fi

if [[ ! -d "$migrations_dir" ]]; then
  echo "Migrations directory not found: $migrations_dir" >&2
  exit 1
fi

echo "Waiting for Cassandra to accept CQL commands..."
for attempt in $(seq 1 "$max_attempts"); do
  if docker exec "$container_name" cqlsh -e "DESCRIBE KEYSPACE $keyspace" >/dev/null 2>&1; then
    break
  fi

  if [[ "$attempt" -eq "$max_attempts" ]]; then
    echo "Cassandra keyspace '$keyspace' was not ready after $max_attempts attempts." >&2
    exit 1
  fi

  sleep 2
done

shopt -s nullglob
migrations=("$migrations_dir"/*.cql)
shopt -u nullglob

if [[ "${#migrations[@]}" -eq 0 ]]; then
  echo "No Cassandra migrations found in: $migrations_dir"
  exit 0
fi

for migration in "${migrations[@]}"; do
  echo "Applying Cassandra migration: $migration"
  docker exec -i "$container_name" cqlsh -k "$keyspace" < "$migration"
done

echo "Cassandra migrations applied successfully."
