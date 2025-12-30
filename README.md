# Fortis Application

**A secure, full-featured task management API built with Spring Boot.**

### Features
- User registration with email verification
- Secure JWT login + refresh token rotation
- Password reset with secure tokens
- Full task CRUD (create, read, update, delete, toggle complete)
- Strict ownership enforcement (users only access their own tasks)
- Protected endpoints with Spring Security
- Clean, documented code with separation of concerns

### Tech Stack
- Spring Boot 3.4.0
- Spring Security + JWT
- JPA/Hibernate
- H2 Database (in-memory for dev)
- Lombok
- Gradle

### Quick Start
```bash
./gradlew bootRun


API: http://localhost:8080
H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:fortisdb)
API Endpoints

Auth:
POST /api/auth/register
GET /api/auth/verify
POST /api/auth/login
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/refresh
POST /api/auth/logout

Tasks (JWT required)
POST /api/tasks
GET /api/tasks
GET /api/tasks/{id}
PUT /api/tasks/{id}
PATCH /api/tasks/{id}/toggle
DELETE /api/tasks/{id}