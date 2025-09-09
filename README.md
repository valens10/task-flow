# Task Flow - Task Management System

A comprehensive task management system built with Spring Boot, featuring JWT authentication, task CRUD operations, commenting system, and advanced filtering capabilities.

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
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Documentation**: Swagger/OpenAPI 3
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

---

**Happy Task Managing! 🎉**
