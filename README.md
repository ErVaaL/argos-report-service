# Argos Report Service

Report microservice responsible for generating and serving report artifacts.
Consumes report requests from RabbitMQ, queries resource data over gRPC,
persists jobs in MongoDB, and stores artifacts on disk.

## Modules

- `report-core`: domain models and ports
- `report-application`: application services
- `report-adapters:mongo`: MongoDB persistence adapter
- `report-adapters:rabbitmq`: RabbitMQ publisher/consumer adapters
- `report-adapters:grpc`: gRPC resource query adapter
- `report-adapters:pdf`: PDF generator adapter
- `report-adapters:storage`: storage adapter for generated artifacts
- `report-bootstrap`: Spring Boot application and REST controllers

## Requirements

- Java 21
- Docker (for local Mongo + RabbitMQ + Keycloak via compose)
- Gradle (or use `./gradlew`)

## Configuration

The service reads configuration from environment variables (see
`report-bootstrap/src/main/resources/application.yaml`):

- `SPRING_MONGODB_URI` (required)
- `SPRING_RABBITMQ_HOST` (required)
- `SPRING_RABBITMQ_PORT` (required)
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` (required)
- `ARGOS_RESOURCE_GRPC_HOST` (required)
- `ARGOS_RESOURCE_GRPC_PORT` (required)
- `ARGOS_REPORT_STORAGE_DIR` (required)

Default HTTP port is `8083`.

## Run locally (Docker Compose)

```bash
export GH_USER=...          # if building image from source in compose
export GH_TOKEN=...

docker compose up --build
```

> Note:
>
> Environment variables can also be set in .env file for Docker Compose. (Don't commit sensitive info!)

Services:
- HTTP: http://localhost:8083
- Mongo: localhost:27017 (container)
- RabbitMQ: localhost:5672 (container)
- RabbitMQ Management: http://localhost:15672
- Keycloak: http://localhost:8080

Note: API endpoints require a JWT issued by the configured Keycloak realm.

## Run locally (Gradle)

```bash
export SPRING_MONGODB_URI=mongodb://root:root@localhost:27017/argos_reports?authSource=admin
export SPRING_RABBITMQ_HOST=localhost
export SPRING_RABBITMQ_PORT=5672
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8080/realms/master
export ARGOS_RESOURCE_GRPC_HOST=localhost
export ARGOS_RESOURCE_GRPC_PORT=9091
export ARGOS_REPORT_STORAGE_DIR=./data/reports

./gradlew :report-bootstrap:bootRun
```

## API

Base path is `/` when running the service directly. When behind the gateway,
use `/api/v1/report`.

Endpoints:
- `GET /jobs/{jobId}/pdf`
- `GET /jobs/list`

## gRPC dependency

The service calls the Resource gRPC server defined in `argos-contracts`.
Make sure `ARGOS_RESOURCE_GRPC_HOST` and `ARGOS_RESOURCE_GRPC_PORT` point to a
reachable Resource service.

## Tests

```bash
./gradlew test
```

Note: Mongo adapter tests use Testcontainers.
