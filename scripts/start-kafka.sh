#!/usr/bin/env sh
set -eu

CONTAINER_NAME="fancy-kafka"
IMAGE_NAME="apache/kafka:4.0.0"
KAFKA_PORT="9092"
MAX_ATTEMPTS="60"

if ! command -v docker >/dev/null 2>&1; then
  printf '%s\n' "Docker is required to run Kafka." >&2
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  printf '%s\n' "Docker is not running or is not accessible." >&2
  exit 1
fi

if docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  printf '%s\n' "Kafka container is already running."
else
  if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
    printf '%s\n' "Starting existing Kafka container..."
    docker start "$CONTAINER_NAME" >/dev/null
  else
    printf '%s\n' "Creating Kafka container..."
    docker run \
      --name "$CONTAINER_NAME" \
      --detach \
      --restart unless-stopped \
      --publish "${KAFKA_PORT}:9092" \
      --env KAFKA_NODE_ID="1" \
      --env KAFKA_PROCESS_ROLES="broker,controller" \
      --env KAFKA_LISTENERS="PLAINTEXT://:9092,CONTROLLER://:9093" \
      --env KAFKA_ADVERTISED_LISTENERS="PLAINTEXT://localhost:${KAFKA_PORT}" \
      --env KAFKA_CONTROLLER_LISTENER_NAMES="CONTROLLER" \
      --env KAFKA_LISTENER_SECURITY_PROTOCOL_MAP="CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT" \
      --env KAFKA_CONTROLLER_QUORUM_VOTERS="1@localhost:9093" \
      --env KAFKA_INTER_BROKER_LISTENER_NAME="PLAINTEXT" \
      --env KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR="1" \
      --env KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR="1" \
      --env KAFKA_TRANSACTION_STATE_LOG_MIN_ISR="1" \
      "$IMAGE_NAME" >/dev/null
  fi
fi

printf '%s\n' "Waiting for Kafka to accept commands..."
attempt=1
while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
  if docker exec "$CONTAINER_NAME" /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --list >/dev/null 2>&1; then
    printf '%s\n' "Kafka is running on localhost:${KAFKA_PORT}."
    exit 0
  fi

  attempt=$((attempt + 1))
  sleep 2
done

printf '%s\n' "Kafka did not become ready after ${MAX_ATTEMPTS} attempts." >&2
exit 1
