.PHONY: install migrate run test clean docker-build docker-run docker-stop docker-clean

IMAGE_NAME ?= student-api
IMAGE_TAG ?= 1.0.0
CONTAINER_NAME ?= student-api
HOST_PORT ?= 8080
CONTAINER_PORT ?= 8080

install:
	./mvnw clean install -DskipTests

migrate:
	./mvnw flyway:migrate

run:
	./mvnw spring-boot:run

test:
	./mvnw test

clean:
	./mvnw clean

docker-build:
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

docker-run:
	docker run -d --name $(CONTAINER_NAME) \
		-p $(HOST_PORT):$(CONTAINER_PORT) \
		-e DB_HOST=host.docker.internal \
		-e DB_PORT=5432 \
		-e DB_NAME=studentdb \
		-e DB_USERNAME=studentuser \
		-e DB_PASSWORD=password \
		-e SERVER_PORT=$(CONTAINER_PORT) \
		-e SPRING_PROFILES_ACTIVE=prod \
		-e LOG_LEVEL=INFO \
		$(IMAGE_NAME):$(IMAGE_TAG)

docker-stop:
	docker stop $(CONTAINER_NAME) 2>/dev/null || true

docker-clean: docker-stop
	docker rm $(CONTAINER_NAME) 2>/dev/null || true
	docker rmi $(IMAGE_NAME):$(IMAGE_TAG) 2>/dev/null || true
