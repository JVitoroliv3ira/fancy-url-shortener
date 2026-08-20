#!/usr/bin/env bash
if [ -z "${BASH_VERSION:-}" ]; then
  exec bash "$0" "$@"
fi

set -euo pipefail

container_name="fancy-cassandra"
image_name="cassandra:latest"
keyspace="fancy_url_shortener"
port_mapping="9042:9042"
max_attempts=60

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is required to run Cassandra." >&2
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running or is not accessible." >&2
  exit 1
fi

if docker container inspect "$container_name" >/dev/null 2>&1; then
  if [[ "$(docker inspect -f '{{.State.Running}}' "$container_name")" != "true" ]]; then
    echo "Starting existing Cassandra container: $container_name"
    docker start "$container_name" >/dev/null
  else
    echo "Cassandra container already running: $container_name"
  fi
else
  echo "Creating Cassandra container: $container_name"
  docker run --detach \
    --name "$container_name" \
    --publish "$port_mapping" \
    "$image_name" >/dev/null
fi

echo "Waiting for Cassandra to accept CQL commands..."
for attempt in $(seq 1 "$max_attempts"); do
  if docker exec "$container_name" cqlsh -e "DESCRIBE KEYSPACES" >/dev/null 2>&1; then
    break
  fi

  if [[ "$attempt" -eq "$max_attempts" ]]; then
    echo "Cassandra did not become ready after $max_attempts attempts." >&2
    exit 1
  fi

  sleep 2
done

docker exec "$container_name" cqlsh -e "CREATE KEYSPACE IF NOT EXISTS $keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"

cat <<INFO
Cassandra is ready.
Container: $container_name
Host: localhost
Port: 9042
Keyspace: $keyspace
INFO
