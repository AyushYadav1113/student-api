.PHONY: install run test clean check-env wait-for-db \
        db-up db-down migrate \
        docker-build docker-image-clean \
        api-up api-down up down

COMPOSE := docker compose
FLYWAY_PROFILE := --profile tools

# Load environment variables from .env when present
ifneq (,$(wildcard .env))
include .env
export
endif

DB_HOST ?= postgres
DB_PORT ?= 5432
DB_NAME ?= studentdb
DB_USERNAME ?= studentuser
DB_PASSWORD ?= password
APP_PORT ?= 8080
IMAGE_NAME ?= student-api
IMAGE_TAG ?= 1.0.0
SPRING_PROFILES_ACTIVE ?= dev
LOG_LEVEL ?= INFO

install:
	./mvnw clean install -DskipTests

run:
	./mvnw spring-boot:run

test:
	./mvnw test

clean:
	./mvnw clean

check-env:
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "Created .env from .env.example"; \
	fi

wait-for-db: check-env
	@chmod +x scripts/wait-for-db.sh
	@./scripts/wait-for-db.sh

db-up: check-env
	@if docker compose ps postgres 2>/dev/null | grep -q "Up"; then \
		echo "PostgreSQL is already running."; \
	else \
		echo "Starting PostgreSQL..."; \
		$(COMPOSE) up -d postgres; \
	fi
	@$(MAKE) wait-for-db

db-down:
	$(COMPOSE) stop postgres

migrate: check-env
	@if ! docker compose ps postgres 2>/dev/null | grep -q "Up"; then \
		echo "PostgreSQL is not running. Starting database..."; \
		$(MAKE) db-up; \
	else \
		$(MAKE) wait-for-db; \
	fi
	@echo "Applying Flyway migrations (already-applied migrations are skipped automatically)..."
	@$(COMPOSE) $(FLYWAY_PROFILE) run --rm flyway

docker-build: check-env
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

docker-image-clean:
	docker rmi $(IMAGE_NAME):$(IMAGE_TAG) 2>/dev/null || true

api-up: check-env docker-build
	$(COMPOSE) up -d api

api-down:
	$(COMPOSE) stop api

up: check-env docker-build
	@echo "Starting local development stack..."
	@echo "Step 1/4: Starting PostgreSQL..."
	@$(COMPOSE) up -d postgres
	@$(MAKE) wait-for-db
	@echo "Step 2/4: Running Flyway migrations..."
	@$(COMPOSE) $(FLYWAY_PROFILE) run --rm flyway
	@echo "Step 3/4: Docker image ready ($(IMAGE_NAME):$(IMAGE_TAG))"
	@echo "Step 4/4: Starting API..."
	@$(COMPOSE) up -d api
	@echo ""
	@echo "Student API is running."
	@echo "Health check: http://localhost:$(APP_PORT)/healthcheck"
	@echo "API base URL: http://localhost:$(APP_PORT)/api/v1/students"

down:
	$(COMPOSE) down --remove-orphans
