#!/usr/bin/env sh
set -eu

CONTAINER_NAME="fancy-redis"
IMAGE_NAME="redis:7-alpine"
REDIS_PORT="6379"

if docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  printf '%s\n' "Redis container is already running."
  exit 0
fi

if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  printf '%s\n' "Starting existing Redis container..."
  docker start "$CONTAINER_NAME" >/dev/null
  printf '%s\n' "Redis is running on localhost:${REDIS_PORT}."
  exit 0
fi

printf '%s\n' "Creating Redis container..."
docker run \
  --name "$CONTAINER_NAME" \
  --detach \
  --restart unless-stopped \
  --publish "${REDIS_PORT}:6379" \
  "$IMAGE_NAME" >/dev/null

printf '%s\n' "Redis is running on localhost:${REDIS_PORT}."
