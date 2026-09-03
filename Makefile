SERVICE_DIR := services/fancy-url-shortener
PROJECT_JAVA_HOME ?= $(HOME)/.jdks/temurin-21
JAVA_ENV := JAVA_HOME="$(PROJECT_JAVA_HOME)" PATH="$(PROJECT_JAVA_HOME)/bin:$(PATH)"
MVNW := $(SERVICE_DIR)/mvnw -f $(SERVICE_DIR)/pom.xml

.PHONY: test
test:
	$(JAVA_ENV) $(MVNW) test

.PHONY: boot
boot:
	$(JAVA_ENV) $(MVNW) spring-boot:run

.PHONY: build
build:
	$(JAVA_ENV) $(MVNW) clean package

.PHONY: clean
clean:
	$(JAVA_ENV) $(MVNW) clean

.PHONY: cassandra-up
cassandra-up:
	./scripts/start-cassandra.sh

.PHONY: cassandra-migrate
cassandra-migrate:
	./scripts/apply-cassandra-migrations.sh

.PHONY: cassandra-setup
cassandra-setup: cassandra-up cassandra-migrate

.PHONY: redis-up
redis-up:
	./scripts/start-redis.sh

.PHONY: infra-up
infra-up: cassandra-up redis-up
