# Task Flow - Task Management System

A comprehensive task management system built with Spring Boot, featuring JWT authentication, task CRUD operations, commenting system, and advanced filtering capabilities.

## 📖 About This Project

This project was created for the purpose of **leveling up** and gaining hands-on experience with modern Java development technologies, tools, and best practices. It serves as a learning platform to explore and master:

- **Backend Development**: Spring Boot, Spring Security, Spring Data JPA
- **Authentication & Security**: JWT tokens, role-based access control
- **Event-Driven Architecture**: Apache Kafka for asynchronous messaging
- **Observability**: Prometheus, Grafana, Loki, and Tempo for monitoring, logging, and distributed tracing
- **Containerization**: Docker and Docker Compose for containerized deployments
- **Database Management**: PostgreSQL with Flyway migrations
- **API Documentation**: Swagger/OpenAPI 3
- **DevOps Tools**: Kafka UI, monitoring dashboards, and observability stack

Through building this project, the goal is to gain practical experience with production-ready patterns, microservices concepts, and modern development workflows.

## 🚀 Features

- **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - User registration and login
  - Role-based access control

- **Task Management**
  - Create, read, update, and delete tasks
  - Task assignment and reporting
  - Status tracking (TODO, IN_PROGRESS, IN_REVIEW, DONE, CANCELLED)
  - Priority levels (LOW, MEDIUM, HIGH, URGENT)
  - Due date management

- **Advanced Features**
  - Task commenting system
  - Search functionality
  - Pagination support
  - User-specific task views
  - Overdue and due-today task filtering
  - RESTful API with Swagger documentation

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: PostgreSQL with Flyway migrations
- **Authentication**: JWT (JSON Web Tokens) with refresh tokens
- **Messaging**: Apache Kafka with Zookeeper
- **Observability**: 
  - Prometheus (metrics)
  - Grafana (visualization)
  - Loki (log aggregation)
  - Tempo (distributed tracing)
  - OpenTelemetry (tracing instrumentation)
- **Tools**: Kafka UI, Promtail
- **Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17 or higher**
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/valens10/task-flow.git
cd task_flow
```

### 2. Database Setup

#### Option A: Using Docker Compose (Recommended)

```bash
# Start PostgreSQL using Docker Compose
docker-compose up -d postgres
```

#### Option B: Manual PostgreSQL Setup

1. Install PostgreSQL on your system
2. Create a database named `taskflow`
3. Create a user `taskflow` with password `taskflow` (or update `application.yml`)

```sql
CREATE DATABASE taskflow;
CREATE USER taskflow WITH PASSWORD 'taskflow';
GRANT ALL PRIVILEGES ON DATABASE taskflow TO taskflow;
```

### 3. Configuration

The application uses environment variables for configuration. Create a `.env` file in the root directory:

```env
COMPOSE_PROJECT_NAME=task-flow

# Server
SERVER_PORT=8080

# Database
POSTGRES_HOST=localhost
POSTGRES_DB=taskflow
POSTGRES_USER=postgres
POSTGRES_PASSWORD=admin
POSTGRES_PORT=5433

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000

# JWT (use strong, 32+ chars for HS256)
JWT_ISSUER=task-flow
JWT_ACCESS_SECRET=change-me-to-a-strong-32+char-secret
JWT_REFRESH_SECRET=change-me-to-a-strong-32+char-secret
JWT_ACCESS_TTL_SECONDS=900
JWT_REFRESH_TTL_SECONDS=2592000

#Kafka UI Config
KAFKA_UI_USERNAME='kafka'
KAFKA_UI_PASSWORD='changeit'


# Optional (explicitly set Mailhog ports)
# MAILHOG_SMTP_PORT=1025
# MAILHOG_HTTP_PORT=8025
```

### 4. Build and Run

```bash
# Build the application
.\mvnw.cmd -q -DskipTests compile

# Run the application
.\mvnw.cmd -q spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Verify Installation

- **Health Check**: `http://localhost:8080/actuator/health`
- **API Documentation**: `http://localhost:8080/swagger-ui`
- **API Docs**: `http://localhost:8080/v3/api-docs`
- **Kafka UI**: `http://localhost:8081` (requires authentication - see configuration section)

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |
| POST | `/api/auth/refresh` | Refresh JWT token |
| POST | `/api/auth/logout` | Logout and invalidate token |
| GET | `/api/auth/me` | Get current user info |

### Task Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create a new task |
| GET | `/api/tasks` | List all tasks (paginated) |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/my-tasks` | Get current user's tasks |
| GET | `/api/tasks/assigned-to-me` | Get tasks assigned to current user |
| GET | `/api/tasks/reported-by-me` | Get tasks reported by current user |
| GET | `/api/tasks/overdue` | Get overdue tasks |
| GET | `/api/tasks/due-today` | Get tasks due today |
| GET | `/api/tasks/search?q={term}` | Search tasks |

### Comment Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks/{taskId}/comments` | Add comment to task |
| GET | `/api/tasks/{taskId}/comments` | Get task comments |
| PUT | `/api/tasks/{taskId}/comments/{commentId}` | Update comment |
| DELETE | `/api/tasks/{taskId}/comments/{commentId}` | Delete comment |

## 🔧 Usage Examples

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!",
    "fullName": "John Doe"
  }'
```

### 2. Login and Get Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }'
```

### 3. Create a Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication to the frontend",
    "assigneeId": "user-uuid-here",
    "dueDate": "2024-12-31",
    "priority": "HIGH"
  }'
```

### 4. Get Tasks with Pagination

```bash
curl -X GET "http://localhost:8080/api/tasks?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🗄️ Database Schema

The application uses the following main tables:

- **users**: User accounts and authentication
- **tasks**: Task information and metadata
- **task_comments**: Comments on tasks
- **auth_refresh_tokens**: JWT refresh token management
- **user_roles**: User role assignments

## 🔒 Security

- JWT-based authentication with configurable expiration
- Password hashing using BCrypt
- CORS configuration for frontend integration
- Role-based access control
- SQL injection protection via JPA
- XSS protection via input validation

## 🧪 Testing

```bash
# Run all tests
.\mvnw.cmd test

# Run tests with coverage
.\mvnw.cmd test jacoco:report
```

## 🐳 Docker Support

### Using Docker Compose

```bash
# Start the entire stack (PostgreSQL + Application)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the stack
docker-compose down
```

### Building Docker Image

```bash
# Build the application image
docker build -t task-flow:latest .

# Run the container
docker run -p 8080:8080 --env-file .env task-flow:latest
```

## 🌐 Accessing Monitoring Tools & Services

When running the application with Docker Compose, all monitoring tools and services are available at the following URLs:

### Application Endpoints

| Service | URL | Description |
|---------|-----|-------------|
| **Application** | `http://localhost:8080` | Main application API |
| **Swagger UI** | `http://localhost:8080/swagger-ui` | Interactive API documentation |
| **API Docs (JSON)** | `http://localhost:8080/v3/api-docs` | OpenAPI 3.0 specification |
| **Health Check** | `http://localhost:8080/actuator/health` | Application health status |
| **Metrics** | `http://localhost:8080/actuator/prometheus` | Prometheus metrics endpoint |

### Kafka & Messaging

| Service | URL | Credentials | Description |
|---------|-----|-------------|-------------|
| **Kafka UI** | `http://localhost:8081` | Username: `kafka` (default)<br>Password: `changeit` (default) | Web UI for managing Kafka topics, messages, and consumer groups |
| **Kafka Broker** | `localhost:9092` | N/A | Kafka broker for external clients |
| **Zookeeper** | `localhost:2181` | N/A | Zookeeper service for Kafka coordination |

**Note**: Update `KAFKA_UI_USERNAME` and `KAFKA_UI_PASSWORD` in your `.env` file for production use.

### Observability Stack

| Service | URL | Credentials | Description |
|---------|-----|-------------|-------------|
| **Grafana** | `http://localhost:3000` | Username: `admin`<br>Password: `admin` | Visualization and dashboards for metrics, logs, and traces |
| **Prometheus** | `http://localhost:9090` | N/A | Metrics collection and querying |
| **Loki** | `http://localhost:3100` | N/A | Log aggregation system |
| **Tempo** | `http://localhost:3200` | N/A | Distributed tracing backend |
| **Postgres Exporter** | `http://localhost:9187` | N/A | PostgreSQL metrics exporter |

### Email Testing (Optional)

| Service | URL | Description |
|---------|-----|-------------|
| **Mailpit Web UI** | `http://localhost:8025` | Email testing interface (when mail profile is active) |
| **Mailpit SMTP** | `localhost:1025` | SMTP server for sending test emails |

**Note**: Mailpit is only available when started with the `mail` profile:
```bash
docker compose --profile mail up -d
```

### Quick Access Guide

1. **Start all services**:
   ```bash
   docker compose up -d
   ```

2. **Access Grafana Dashboards**:
   - Open `http://localhost:3000`
   - Login with `admin`/`admin`
   - Pre-configured dashboards are available:
     - Spring Boot Observability
     - JVM Micrometer
     - PostgreSQL Metrics

3. **View Kafka Topics and Messages**:
   - Open `http://localhost:8081`
   - Login with credentials from your `.env` file
   - Browse topics, view messages, and monitor consumer groups

4. **Query Metrics in Prometheus**:
   - Open `http://localhost:9090`
   - Use PromQL to query application metrics
   - Example: `http_server_requests_seconds_count{application="task-flow"}`

5. **View Application Logs**:
   - Logs are automatically collected by Promtail and sent to Loki
   - Access logs through Grafana's Explore view
   - Select Loki as the data source

6. **Trace Requests**:
   - Distributed traces are sent to Tempo
   - View traces in Grafana's Explore view
   - Select Tempo as the data source

### Default Credentials

⚠️ **Security Warning**: The default credentials are for development only. Always change them in production!

- **Grafana**: `admin` / `admin`
- **Kafka UI**: Set via `KAFKA_UI_USERNAME` and `KAFKA_UI_PASSWORD` in `.env` (default: `kafka` / `changeit`)

### Service Dependencies

The services have the following startup order with their versions:

1. **PostgreSQL** (v15.8) → Database
2. **Zookeeper** (v7.6.0 - Confluent Platform) → Kafka coordination
3. **Kafka** (v7.6.0 - Confluent Platform) → Message broker
4. **Tempo** (v2.6.1) → Distributed tracing backend
5. **Application** (Spring Boot 3.4.9, Java 21) → Main Spring Boot app
6. **Prometheus** (latest) → Metrics collection
7. **Grafana** (latest) → Visualization and dashboards
8. **Loki** (v3.1.0) → Log aggregation system
9. **Promtail** (v3.1.0) → Log shipping agent
10. **Kafka UI** (latest) → Kafka management interface
11. **Postgres Exporter** (latest) → PostgreSQL metrics exporter
12. **Mailpit** (latest) → Email testing tool (optional, mail profile)

All dependencies are automatically handled by Docker Compose.

## 📁 Project Structure

```
src/
├── main/
│   ├── java/valens/example/task_flow/
│   │   ├── auth/                 # Authentication module
│   │   │   ├── config/          # JWT configuration
│   │   │   ├── controller/      # Auth endpoints
│   │   │   ├── dto/            # Auth DTOs
│   │   │   ├── service/        # Auth business logic
│   │   │   └── token/          # Refresh token management
│   │   ├── config/             # Global configuration
│   │   ├── tasks/              # Task management module
│   │   │   ├── controller/     # Task endpoints
│   │   │   ├── dto/           # Task DTOs
│   │   │   ├── entity/        # Task entities
│   │   │   ├── repository/    # Data access layer
│   │   │   └── service/       # Task business logic
│   │   ├── users/             # User management
│   │   └── TaskFlowApplication.java
│   └── resources/
│       ├── application.yml    # Application configuration
│       └── db/migration/     # Database migrations
└── test/                     # Test files
```

## 🚀 Deployment

### Environment Variables for Production

```env
# Database
POSTGRES_HOST=your-db-host
POSTGRES_PORT=5432
POSTGRES_DB=taskflow_prod
POSTGRES_USER=taskflow_user
POSTGRES_PASSWORD=secure-password

# Security
JWT_SECRET=your-production-jwt-secret-key
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com

# Server
SERVER_PORT=8080
```

### Build for Production

```bash
# Create production JAR
.\mvnw.cmd clean package -Pproduction

# Run the JAR
java -jar target/task-flow-*.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure PostgreSQL is running
   - Check database credentials in `.env` file
   - Verify database exists

2. **JWT Token Issues**
   - Check JWT secret configuration
   - Ensure token is not expired
   - Verify Authorization header format: `Bearer <token>`

3. **CORS Errors**
   - Update `CORS_ALLOWED_ORIGINS` in `.env`
   - Ensure frontend URL is included

4. **Port Already in Use**
   - Change `SERVER_PORT` in `.env` file
   - Or stop the process using port 8080

### Getting Help

- Check the [Issues](https://github.com/valens10/task-flow.git/issues) page
- Review the [API Documentation](http://localhost:8080/swagger-ui)
- Check application logs for detailed error messages

## 🎯 Roadmap

- [ ] Real-time notifications
- [ ] File attachments for tasks
- [ ] Task templates
- [ ] Advanced reporting and analytics
- [ ] Mobile app support
- [ ] Integration with external tools (Slack, GitHub, etc.)

## 🌐 API Gateway Preparation (Kong Gateway)

This application is prepared for integration with Kong API Gateway. The following features are implemented to ensure smooth migration:

### ✅ Completed Preparations

1. **Correlation IDs**
   - All API responses include `X-Correlation-ID` header
   - Correlation IDs are extracted from requests or generated automatically
   - Correlation IDs are included in all log entries for request tracing

2. **Request/Response Logging**
   - All HTTP requests and responses are logged with correlation IDs
   - Request duration is tracked for performance monitoring
   - Sensitive data (passwords) is automatically sanitized in logs

3. **API Documentation**
   - OpenAPI 3.0 documentation available at `/v3/api-docs`
   - Swagger UI available at `/swagger-ui`
   - All endpoints are documented with descriptions and examples

4. **Consistent Error Handling**
   - Standardized error response format (RFC 7807 Problem Details)
   - All errors include correlation IDs
   - Proper HTTP status codes for all error scenarios

5. **Health Checks**
   - Health endpoint: `/actuator/health`
   - Readiness probe: `/actuator/health/readiness`
   - Liveness probe: `/actuator/health/liveness`
   - Metrics endpoint: `/actuator/prometheus`

6. **Observability**
   - Distributed tracing with OpenTelemetry
   - Metrics exposed via Prometheus
   - Structured logging with trace and correlation IDs
   - All logs include correlation IDs for request tracking

### 📋 API Versioning Strategy

**Current Status**: All APIs are under `/api/*` (version 1)

**Future Strategy**:
- **URL-based versioning**: `/api/v1/*`, `/api/v2/*`
- **Header-based versioning**: `Accept: application/vnd.taskflow.v1+json` (optional)
- **Migration path**: Support multiple versions simultaneously during transition

**Implementation Plan**:
1. When breaking changes are needed, introduce `/api/v2/*`
2. Keep `/api/v1/*` for backward compatibility
3. Deprecate old versions with 6-month notice
4. Document version lifecycle in API docs

### 🔌 API Contracts

#### Authentication
- **Token Format**: Bearer tokens (JWT)
- **Header**: `Authorization: Bearer <token>`
- **Token Lifetime**: 15 minutes (access), 30 days (refresh)

#### Request Headers
- `X-Correlation-ID`: Optional, auto-generated if not provided
- `Authorization`: Required for protected endpoints
- `Content-Type`: `application/json`

#### Response Headers
- `X-Correlation-ID`: Always included for request tracking
- `Content-Type`: `application/json` or `application/problem+json` (errors)

### 📊 Error Codes

| HTTP Status | Error Code | Description |
|------------|------------|-------------|
| 400 | `VALIDATION_FAILED` | Request validation failed |
| 400 | `BAD_REQUEST` | Invalid request format |
| 401 | `AUTHENTICATION_FAILED` | Invalid credentials or missing token |
| 401 | `UNAUTHORIZED` | Token expired or invalid |
| 403 | `FORBIDDEN` | Insufficient permissions |
| 404 | `NOT_FOUND` | Resource not found |
| 409 | `CONFLICT` | Resource conflict (e.g., duplicate email) |
| 500 | `INTERNAL_SERVER_ERROR` | Unexpected server error |

### 🚀 Kong Gateway Integration Steps

When ready to integrate with Kong:

1. **Add Kong to Docker Compose**
   ```yaml
   kong:
     image: kong:latest
     ports:
       - "8000:8000"  # Proxy
       - "8001:8001"  # Admin API
   ```

2. **Configure Kong Routes**
   - Create services pointing to `http://app:8080`
   - Configure routes for `/api/*` endpoints
   - Set up authentication plugins (JWT validation)

3. **Update Client Applications**
   - Change base URL from `http://localhost:8080` to `http://localhost:8000`
   - Kong will proxy requests to your application

4. **Configure Rate Limiting**
   - Set up rate limiting plugins per consumer
   - Configure different limits for different API tiers

5. **Enable Analytics**
   - Configure Kong analytics plugins
   - Integrate with your existing Prometheus/Grafana setup

### 📝 API Best Practices

1. **Always include correlation IDs** in requests for better tracing
2. **Use proper HTTP methods** (GET, POST, PUT, DELETE, PATCH)
3. **Follow RESTful conventions** for resource naming
4. **Return appropriate status codes** for all scenarios
5. **Include error details** in error responses
6. **Use pagination** for list endpoints
7. **Validate all inputs** before processing

### 📊 Kafka UI

Kafka UI is available for managing and monitoring your Kafka cluster. It's protected with basic authentication for production use.

**Access:**
- URL: `http://localhost:8081`
- Username: Set via `KAFKA_UI_USERNAME` environment variable (default: `admin`)
- Password: Set via `KAFKA_UI_PASSWORD` environment variable (default: `changeme`)

**Features:**
- Browse topics and messages
- View consumer groups and their offsets
- Monitor broker metrics
- Create and manage topics
- Inspect message payloads

**Security:**
- For production, **always** set strong credentials in your `.env` file:
  ```env
  KAFKA_UI_USERNAME=your-username
  KAFKA_UI_PASSWORD=your-strong-password
  ```
- The default credentials (`admin`/`changeme`) should **never** be used in production.

**Starting Kafka UI:**
```bash
# Start all services including Kafka UI
docker compose up -d

# Or start just Kafka UI and dependencies
docker compose up -d zookeeper kafka kafka-ui
```

### 🔍 Monitoring & Analytics

All API calls are logged with:
- Correlation ID
- Trace ID (for distributed tracing)
- Request method and URI
- Response status code
- Response duration
- User information (when authenticated)

This data is available in:
- Application logs (with correlation IDs)
- Prometheus metrics (`/actuator/prometheus`)
- Grafana dashboards (traces, metrics, logs)
- Tempo (distributed traces)

---

**Happy Task Managing! 🎉**
