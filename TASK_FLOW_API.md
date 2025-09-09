# Task Flow API Documentation

## Overview
The Task Flow application provides a comprehensive task management system with authentication, task CRUD operations, and commenting functionality.

## Authentication
All task endpoints require authentication via JWT tokens. Use the `/api/auth/login` endpoint to obtain a token.

## Task Management Endpoints

### Create Task
```
POST /api/tasks
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Task Title",
  "description": "Task description",
  "assigneeId": "uuid-of-assignee",
  "dueDate": "2024-12-31",
  "priority": "HIGH"
}
```

### Get Task
```
GET /api/tasks/{id}
Authorization: Bearer <token>
```

### Update Task
```
PUT /api/tasks/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Updated Title",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM"
}
```

### Delete Task
```
DELETE /api/tasks/{id}
Authorization: Bearer <token>
```

### List Tasks
```
GET /api/tasks?page=0&size=20&status=TODO&priority=HIGH
Authorization: Bearer <token>
```

### My Tasks
```
GET /api/tasks/my-tasks
Authorization: Bearer <token>
```

### Tasks Assigned to Me
```
GET /api/tasks/assigned-to-me
Authorization: Bearer <token>
```

### Tasks Reported by Me
```
GET /api/tasks/reported-by-me
Authorization: Bearer <token>
```

### Search Tasks
```
GET /api/tasks/search?q=search-term
Authorization: Bearer <token>
```

### Overdue Tasks
```
GET /api/tasks/overdue
Authorization: Bearer <token>
```

### Tasks Due Today
```
GET /api/tasks/due-today
Authorization: Bearer <token>
```

## Task Comments

### Add Comment
```
POST /api/tasks/{taskId}/comments
Content-Type: application/json
Authorization: Bearer <token>

{
  "body": "Comment text"
}
```

### Get Task Comments
```
GET /api/tasks/{taskId}/comments
Authorization: Bearer <token>
```

### Update Comment
```
PUT /api/tasks/{taskId}/comments/{commentId}
Content-Type: application/json
Authorization: Bearer <token>

{
  "body": "Updated comment text"
}
```

### Delete Comment
```
DELETE /api/tasks/{taskId}/comments/{commentId}
Authorization: Bearer <token>
```

### My Comments
```
GET /api/comments/my-comments
Authorization: Bearer <token>
```

### Search Comments
```
GET /api/comments/search?q=search-term
Authorization: Bearer <token>
```

## Task Status Values
- `TODO`
- `IN_PROGRESS`
- `IN_REVIEW`
- `DONE`
- `CANCELLED`

## Priority Values
- `LOW` (1)
- `MEDIUM` (2)
- `HIGH` (3)
- `URGENT` (4)

## Features Implemented
- ✅ Task CRUD operations
- ✅ Task assignment and reporting
- ✅ Task status and priority management
- ✅ Due date tracking
- ✅ Task comments
- ✅ Search functionality
- ✅ Pagination support
- ✅ User-specific task views
- ✅ Overdue and due-today task filtering
- ✅ JWT authentication
- ✅ Swagger API documentation

## Database Schema
The application uses PostgreSQL with the following main tables:
- `tasks` - Main task information
- `task_comments` - Task comments
- `users` - User management
- `auth_refresh_tokens` - JWT refresh tokens

## Getting Started
1. Start the application: `mvn spring-boot:run`
2. Access Swagger UI: `http://localhost:8080/swagger-ui`
3. Create a user account via `/api/auth/signup`
4. Login to get a JWT token
5. Use the token to access task management endpoints
