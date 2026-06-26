# Student CRUD REST API

Production-ready Student CRUD REST API built with **Java 21**, **Spring Boot 3**, and **PostgreSQL**. The application follows layered architecture, REST best practices, the Twelve-Factor App methodology, and Clean Architecture principles.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway Migration
- Maven
- Lombok
- Spring Validation
- JUnit 5
- Mockito
- SLF4J + Logback
- Docker

## Architecture

```
Controller
     ↓
Service
     ↓
Repository
     ↓
PostgreSQL
```

- **Controller**: Validates requests, delegates to services, returns HTTP responses
- **Service**: Contains all business logic
- **Repository**: Data access via Spring Data JPA
- **Mapper**: Converts between entities and DTOs (entities are never exposed directly)

## Folder Structure

```
student-api/
├── src/main/java/com/example/student_api/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── logging/
│   ├── mapper/
│   ├── repository/
│   ├── service/
│   │   └── impl/
│   ├── util/
│   └── StudentApiApplication.java
├── src/main/resources/
│   ├── db/migration/
│   ├── application.yml
│   └── logback-spring.xml
├── src/test/
├── postman/
├── Dockerfile
├── .dockerignore
├── README.md
├── Makefile
├── pom.xml
└── .env.example
```

## Local Setup

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 14+
- Docker (for containerized deployment)

### PostgreSQL Setup

```sql
CREATE DATABASE studentdb;
CREATE USER studentuser WITH ENCRYPTED PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE studentdb TO studentuser;
```

### Environment Variables

Copy `.env.example` and export the variables before running the application:

```bash
cp .env.example .env
export $(grep -v '^#' .env | xargs)
```

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `studentdb` |
| `DB_USERNAME` | Database user | `studentuser` |
| `DB_PASSWORD` | Database password | `password` |
| `SERVER_PORT` | HTTP server port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |
| `LOG_LEVEL` | Root log level | `INFO` |

## Running Migrations

Flyway runs automatically on application startup. To run migrations manually:

```bash
make migrate
```

Or:

```bash
./mvnw flyway:migrate
```

## Running the Application

```bash
make install
make run
```

Or:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Docker

The application can run entirely from a Docker image using a multi-stage build. Configuration is supplied at runtime via environment variables — no secrets are baked into the image.

### Prerequisites

- Docker
- PostgreSQL (running on the host or another container)

Ensure PostgreSQL is accessible from the container. On macOS and Windows, use `host.docker.internal` as `DB_HOST` to reach PostgreSQL on the host machine.

### Build Image

```bash
make docker-build
```

Or build with an explicit semantic version tag:

```bash
docker build -t student-api:1.0.0 .
```

Supported tags follow semantic versioning, for example:

- `student-api:1.0.0`
- `student-api:1.0.1`
- `student-api:1.1.0`

Override the tag when building:

```bash
make docker-build IMAGE_TAG=1.0.1
```

### Run Container

Pass database and server configuration through environment variables:

```bash
docker run \
  -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=studentdb \
  -e DB_USERNAME=studentuser \
  -e DB_PASSWORD=password \
  -e SERVER_PORT=8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  student-api:1.0.0
```

Or use the Makefile target (runs detached):

```bash
make docker-run
```

Stop and remove the container:

```bash
make docker-stop
make docker-clean
```

### Verify

Health check:

```http
GET http://localhost:8080/healthcheck
```

Expected response:

```json
{
  "status": "UP",
  "service": "student-api"
}
```

Test CRUD endpoints at `http://localhost:8080/api/v1/students` or import the Postman collection.

## Running Tests

```bash
make test
```

Or:

```bash
./mvnw test
```

## API Documentation

Base URL: `http://localhost:8080`

OpenAPI/Swagger UI: `http://localhost:8080/swagger-ui.html`

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/students` | Create a student |
| `GET` | `/api/v1/students` | Get all students |
| `GET` | `/api/v1/students/{id}` | Get student by ID |
| `PUT` | `/api/v1/students/{id}` | Update a student |
| `DELETE` | `/api/v1/students/{id}` | Delete a student |
| `GET` | `/healthcheck` | Health check |

### Sample Request — Create Student

```http
POST /api/v1/students
Content-Type: application/json

{
  "firstName": "Ayush",
  "lastName": "Yadav",
  "email": "ayush@example.com",
  "age": 22
}
```

### Sample Response — Create Student

```json
{
  "id": 1,
  "firstName": "Ayush",
  "lastName": "Yadav",
  "email": "ayush@example.com",
  "age": 22,
  "createdAt": "2026-06-26T10:30:00",
  "updatedAt": "2026-06-26T10:30:00"
}
```

### Sample Response — Health Check

```json
{
  "status": "UP",
  "service": "student-api"
}
```

### Sample Error Response — Validation

```json
{
  "timestamp": "2026-06-26T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/v1/students",
  "errors": {
    "firstName": "First name is required",
    "email": "Invalid email format"
  }
}
```

### HTTP Status Codes

| Code | Meaning |
|------|---------|
| `200` | Success |
| `201` | Created |
| `204` | No Content (delete) |
| `400` | Validation error |
| `404` | Student not found |
| `409` | Duplicate email / database conflict |
| `500` | Internal server error |

## Postman Collection

Import `postman/Student-API.postman_collection.json` into Postman. The collection includes all CRUD endpoints and the health check with sample request bodies.

## Makefile Commands

| Command | Description |
|---------|-------------|
| `make install` | Build the project (skip tests) |
| `make migrate` | Run Flyway migrations |
| `make run` | Start the application |
| `make test` | Run unit tests |
| `make clean` | Clean build artifacts |
| `make docker-build` | Build Docker image (`student-api:1.0.0` by default) |
| `make docker-run` | Run container with environment variables |
| `make docker-stop` | Stop the running container |
| `make docker-clean` | Stop container and remove container/image |

## Logging

Structured logging is configured via Logback. The application logs:

- Incoming HTTP requests and response status
- Successful CRUD operations
- Validation failures
- Database errors
- Unexpected exceptions
- Server startup and shutdown

## License

MIT
