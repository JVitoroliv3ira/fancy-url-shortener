URL_SHORTENER_SERVICE_DIR := services/fancy-url-shortener
ANALYTICS_SERVICE_DIR := services/fancy-url-shortener-analytics
WEB_APP_DIR := apps/fancy-web
PROJECT_JAVA_HOME ?= $(HOME)/.jdks/temurin-21
WEB_NODE_BIN ?= $(HOME)/.nvm/versions/node/v22.23.2/bin
JAVA_ENV := JAVA_HOME="$(PROJECT_JAVA_HOME)" PATH="$(PROJECT_JAVA_HOME)/bin:$(PATH)"
WEB_NODE_ENV := PATH="$(WEB_NODE_BIN):$(PATH)"
URL_SHORTENER_MVNW := $(URL_SHORTENER_SERVICE_DIR)/mvnw -f $(URL_SHORTENER_SERVICE_DIR)/pom.xml
ANALYTICS_MVNW := $(ANALYTICS_SERVICE_DIR)/mvnw -f $(ANALYTICS_SERVICE_DIR)/pom.xml

.PHONY: test
test:
	$(JAVA_ENV) $(URL_SHORTENER_MVNW) test

.PHONY: boot
boot:
	$(JAVA_ENV) $(URL_SHORTENER_MVNW) spring-boot:run

.PHONY: build
build:
	$(JAVA_ENV) $(URL_SHORTENER_MVNW) clean package

.PHONY: clean
clean:
	$(JAVA_ENV) $(URL_SHORTENER_MVNW) clean

.PHONY: analytics-test
analytics-test:
	$(JAVA_ENV) $(ANALYTICS_MVNW) test

.PHONY: analytics-boot
analytics-boot:
	$(JAVA_ENV) $(ANALYTICS_MVNW) spring-boot:run

.PHONY: analytics-build
analytics-build:
	$(JAVA_ENV) $(ANALYTICS_MVNW) clean package

.PHONY: analytics-clean
analytics-clean:
	$(JAVA_ENV) $(ANALYTICS_MVNW) clean

.PHONY: web-boot
web-boot:
	$(WEB_NODE_ENV) npm start --prefix $(WEB_APP_DIR)

.PHONY: web-build
web-build:
	$(WEB_NODE_ENV) npm run build --prefix $(WEB_APP_DIR)

.PHONY: web-test
web-test:
	$(WEB_NODE_ENV) npm test --prefix $(WEB_APP_DIR) -- --watch=false

.PHONY: test-all
test-all: test analytics-test

.PHONY: build-all
build-all: build analytics-build

.PHONY: clean-all
clean-all: clean analytics-clean

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

.PHONY: kafka-up
kafka-up:
	./scripts/start-kafka.sh

.PHONY: infra-up
infra-up: cassandra-setup redis-up kafka-up
