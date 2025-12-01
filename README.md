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
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=taskflow
POSTGRES_USER=taskflow
POSTGRES_PASSWORD=taskflow

# Server Configuration
SERVER_PORT=8080

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000

# JWT Configuration (optional - defaults provided)
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-secure
JWT_ACCESS_TTL_SECONDS=3600
JWT_REFRESH_TTL_SECONDS=604800

# Kafka UI Authentication (for production)
KAFKA_UI_USERNAME=admin
KAFKA_UI_PASSWORD=your-secure-password-here
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
