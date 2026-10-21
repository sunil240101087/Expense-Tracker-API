# Expense Tracker API

A secure, multi-user expense tracking REST API built with **Java 21** and **Spring Boot 3**. Each user manages their own expenses behind **stateless JWT authentication**, with **role-based access control**, dynamic filtering, pagination, spending analytics, database migrations, and a full automated test suite.

[![CI](https://github.com/nataliachs/expense-tracker-api/actions/workflows/ci.yml/badge.svg)](https://github.com/nataliachs/expense-tracker-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue)
![License](https://img.shields.io/badge/License-MIT-blue)

## Highlights

- **JWT authentication** — stateless login/registration issuing signed Bearer tokens (jjwt)
- **Role-based access control** — `ROLE_USER` and `ROLE_ADMIN`, enforced at the URL and ready for method level
- **Per-user data ownership** — every query is scoped to the authenticated user, so no one can read or modify another user's data (covered by tests)
- **BCrypt password hashing** — credentials are never stored in plain text
- **Dynamic filtering** — filter expenses by category and date range using composable JPA Specifications
- **Pagination & sorting** — standard Spring `Pageable` support
- **Spending analytics** — aggregate totals grouped by category over a date range
- **Database migrations** — schema managed by Flyway (runs on H2 and PostgreSQL)
- **RFC 7807 error responses** — consistent `application/problem+json` for validation, auth, and not-found errors
- **Interactive API docs** — Swagger UI with an "Authorize" button for JWT
- **Containerized** — multi-stage Docker build plus a `docker-compose` stack with PostgreSQL
- **Tested** — unit tests and full end-to-end integration tests (security, ownership, validation, RBAC)

## Tech Stack

| Concern          | Choice                                       |
|------------------|----------------------------------------------|
| Language         | Java 21                                       |
| Framework        | Spring Boot 3.3 (Web, Data JPA, Security)     |
| Auth             | Spring Security + JWT (jjwt 0.12)             |
| Persistence      | Spring Data JPA, Hibernate                    |
| Migrations       | Flyway                                         |
| Database         | H2 (dev/test), PostgreSQL (compose/prod)      |
| API Docs         | springdoc-openapi (Swagger UI)                |
| Testing          | JUnit 5, Spring MockMvc, Spring Security Test |
| Build            | Maven                                          |
| Container        | Docker (multi-stage), Docker Compose          |
| CI               | GitHub Actions                                |

## Architecture

The codebase is organized by **feature package** (auth, expense, user, admin, security, config, common), each with its own controller, service, repository, and DTOs:

```
client ──► JwtAuthenticationFilter ──► Controller ──► Service ──► Repository ──► DB
                  │                                       │
            validates JWT                       business logic, scoped
            sets SecurityContext                to the authenticated user
```

- **security** — JWT issuing/validation, the auth filter, `UserDetails`, security configuration
- **auth** — registration and login endpoints + service
- **expense** — expense entity, CRUD/search/summary, JPA Specifications
- **user** — `/users/me` profile endpoint
- **admin** — admin-only endpoints guarded by `ROLE_ADMIN`
- **common** — shared exceptions and the global RFC 7807 exception handler
- **config** — OpenAPI setup and demo data seeding

## Getting Started

### Prerequisites

- JDK 21+
- Maven 3.9+

### Run locally (in-memory H2, zero setup)

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. A demo admin account is seeded on first run:

- email: `admin@example.com`
- password: `Admin12345`

### Run the tests

```bash
mvn clean verify
```

### Run with PostgreSQL via Docker Compose

```bash
docker compose up --build
```

This starts PostgreSQL and the API (using the `postgres` profile) together.

## Try It in 30 Seconds

```bash
# 1. Register (returns a JWT)
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"Password123","displayName":"Jane"}' \
  | sed -E 's/.*"accessToken":"([^"]+)".*/\1/')

# 2. Create an expense
curl -X POST http://localhost:8080/api/v1/expenses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"Team lunch","amount":42.50,"category":"FOOD","spentOn":"2026-05-15"}'

# 3. List your expenses
curl http://localhost:8080/api/v1/expenses -H "Authorization: Bearer $TOKEN"
```

The `requests.http` file contains the full set of ready-to-run requests for IDE HTTP clients.

## API Documentation

Once running:

- Swagger UI: http://localhost:8080/swagger-ui.html (click **Authorize** and paste your JWT)
- OpenAPI spec: http://localhost:8080/v3/api-docs

## API Reference

### Authentication (public)

| Method | Path                        | Description                       |
|--------|-----------------------------|-----------------------------------|
| POST   | `/api/v1/auth/register`     | Create an account, returns a JWT  |
| POST   | `/api/v1/auth/login`        | Authenticate, returns a JWT       |

### Expenses (JWT required)

| Method | Path                        | Description                              |
|--------|-----------------------------|------------------------------------------|
| GET    | `/api/v1/expenses`          | List your expenses (filters + paging)    |
| GET    | `/api/v1/expenses/{id}`     | Get one of your expenses                 |
| POST   | `/api/v1/expenses`          | Create an expense                        |
| PUT    | `/api/v1/expenses/{id}`     | Update one of your expenses              |
| DELETE | `/api/v1/expenses/{id}`     | Delete one of your expenses              |
| GET    | `/api/v1/expenses/summary`  | Totals by category for a date range      |

Query parameters for `GET /api/v1/expenses`: `category`, `from` (ISO date), `to` (ISO date), `page`, `size`, `sort` (e.g. `spentOn,desc`).

### Account & Admin (JWT required)

| Method | Path                   | Description                          |
|--------|------------------------|--------------------------------------|
| GET    | `/api/v1/users/me`     | The authenticated user's profile     |
| GET    | `/api/v1/admin/users`  | List all users (`ROLE_ADMIN` only)   |

### Example error response (RFC 7807)

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "One or more fields are invalid",
  "errors": {
    "amount": "amount must be greater than 0"
  }
}
```

## Configuration

Key settings (see `application.yml`), overridable via environment variables:

| Variable                    | Description                          | Default                |
|-----------------------------|--------------------------------------|------------------------|
| `SECURITY_JWT_SECRET`       | HMAC signing key (≥ 32 bytes)        | dev placeholder        |
| `SPRING_DATASOURCE_URL`     | JDBC URL (postgres profile)          | local PostgreSQL       |
| `SPRING_PROFILES_ACTIVE`    | `default` (H2) or `postgres`         | default                |

> In production, always set a strong `SECURITY_JWT_SECRET`. The default is for local development only.

## Project Structure

```
expense-tracker-api/
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/expensetracker/
│   │   │   ├── ExpenseTrackerApplication.java
│   │   │   ├── auth/        # register/login endpoints + service
│   │   │   ├── expense/     # entity, service, specs, DTOs
│   │   │   ├── user/        # user entity, repo, /me endpoint
│   │   │   ├── admin/       # admin-only endpoints
│   │   │   ├── security/    # JWT, filter, UserDetails, config
│   │   │   ├── config/      # OpenAPI + data seeding
│   │   │   └── common/      # exceptions + global handler
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-postgres.yml
│   │       └── db/migration/V1__init_schema.sql
│   └── test/                # unit + integration tests
├── .github/workflows/ci.yml
├── Dockerfile
├── docker-compose.yml
├── requests.http
└── pom.xml
```

## Security Notes

- Passwords are hashed with BCrypt and never returned by any endpoint.
- Authentication is stateless; the server stores no session.
- Every expense operation is scoped to the authenticated user's id, preventing horizontal privilege escalation (verified by `usersCannotAccessEachOthersExpenses`).
- The JWT secret and database credentials are externalized as environment variables.

## License

Released under the [MIT License](LICENSE).
