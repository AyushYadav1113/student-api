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
- Docker Compose

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
├── src/main/resources/
│   ├── db/migration/
│   ├── application.yml
│   └── logback-spring.xml
├── src/test/
├── scripts/
│   ├── wait-for-db.sh
│   └── check-postgres.sh
├── postman/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── README.md
├── Makefile
├── pom.xml
└── .env.example
```

## Prerequisites

- Docker Desktop
- Docker Compose
- GNU Make

Optional (for native Java development without Docker):

- Java 21
- Maven 3.9+

## Local Setup

One-command setup for the full stack (PostgreSQL + Flyway migrations + Spring Boot API):

```bash
git clone <repository>
cd student-api
cp .env.example .env
make up
```

Within a few minutes you will have:

- PostgreSQL running with persistent storage
- Flyway migrations applied
- Spring Boot API running in Docker
- Health check at `http://localhost:8080/healthcheck`

### Start Only Database

```bash
make db-up
```

### Run Migrations

```bash
make migrate
```

Flyway skips migrations that have already been applied.

### Build Docker Image

```bash
make docker-build
```

Builds a semantic version tag such as `student-api:1.0.0`.

### Run API

```bash
make api-up
```

Starts the API container and connects it to PostgreSQL over the Docker Compose network.

### Stop Everything

```bash
make down
```

Stops and removes all containers and networks. Database data persists in the named Docker volume.

## Environment Variables

Copy `.env.example` to `.env` before running any Make target:

```bash
cp .env.example .env
```

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host (`postgres` inside Docker Compose) | `postgres` |
| `DB_PORT` | PostgreSQL port exposed on the host | `5432` |
| `DB_NAME` | Database name | `studentdb` |
| `DB_USERNAME` | Database user | `studentuser` |
| `DB_PASSWORD` | Database password | `password` |
| `APP_PORT` | API port on the host | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |
| `LOG_LEVEL` | Root log level | `INFO` |
| `IMAGE_NAME` | Docker image name | `student-api` |
| `IMAGE_TAG` | Docker image tag | `1.0.0` |

No credentials or ports are hardcoded in the application. All configuration is supplied through environment variables.

## Test API

### Health Check

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

### CRUD APIs

```http
POST   /api/v1/students
GET    /api/v1/students
GET    /api/v1/students/{id}
PUT    /api/v1/students/{id}
DELETE /api/v1/students/{id}
```

Import `postman/Student-API.postman_collection.json` for ready-to-use requests.

## Native Java Development (Optional)

If you prefer running the API outside Docker:

```bash
cp .env.example .env
# Set DB_HOST=localhost and start PostgreSQL locally
make db-up
make migrate
make install
make run
```

## Running Tests

```bash
make test
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

## Makefile Commands

| Command | Description |
|---------|-------------|
| `make up` | Start PostgreSQL, run migrations, build image, start API |
| `make down` | Stop and remove all containers and networks |
| `make db-up` | Start PostgreSQL only |
| `make db-down` | Stop PostgreSQL |
| `make migrate` | Run Flyway migrations via Docker |
| `make docker-build` | Build Docker image (`student-api:1.0.0`) |
| `make api-up` | Build image and start API container |
| `make api-down` | Stop API container |
| `make docker-image-clean` | Remove the Docker image |
| `make install` | Build the project with Maven (skip tests) |
| `make run` | Run the API locally with Maven |
| `make test` | Run unit tests |
| `make clean` | Clean Maven build artifacts |

## Docker Compose Services

| Service | Description |
|---------|-------------|
| `postgres` | PostgreSQL 16 Alpine with persistent volume and health check |
| `flyway` | One-off migration runner (invoked by `make migrate`) |
| `api` | Spring Boot Student API (multi-stage Docker build) |

Services communicate over a dedicated bridge network. The API connects to PostgreSQL using the service hostname `postgres`.

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
