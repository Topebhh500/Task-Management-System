# Topsite Task Management (TTM) System

## Overview

Topsite Task Management (TTM) System is a web-based application built with Spring Boot that helps users organize and track their personal and professional tasks. The system supports task creation, status tracking, priority management, and maintains a history of completed and deleted tasks.

## Features

- **User Authentication**

  - Email/Password registration and login
  - Google OAuth2 integration for single sign-on
  - Secure session management

- **Task Management**

  - Create, read, update, and delete tasks
  - Set task priorities (HIGH, MEDIUM, LOW)
  - Track task status (TODO, IN_PROGRESS, COMPLETED)
  - Set due dates for tasks
  - Task history tracking
  - Real-time task status updates

- **Task Organization**

  - Kanban board style interface
  - Tasks organized by status
  - Visual priority indicators
  - Due date tracking
  - Task completion tracking

- **History Tracking**
  - Track completed tasks
  - Track deleted tasks
  - Maintain audit trail of task changes
  - Filter history by action type

## Technology Stack

- **Backend**

  - Java 17
  - Spring Boot 3.2.0
  - Spring Security
  - Spring Data JPA
  - PostgreSQL Database
  - Maven

- **Frontend**

  - Thymeleaf template engine
  - Bootstrap 5.1.3
  - Font Awesome 5.15.4
  - HTML5/CSS3
  - JavaScript

- **Security**
  - Spring Security
  - OAuth2 Client
  - CSRF Protection
  - Password Encryption

## Prerequisites

- JDK 17 or later
- Maven 3.6 or later
- PostgreSQL 12 or later
- Git

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Topebhh500/Task-Management-System.git task-management
cd task-management
```

### 2. Configure Database

Create a PostgreSQL database and update application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configure Google OAuth2 (Optional)

Update application.properties with your Google OAuth2 credentials:

```properties
spring.security.oauth2.client.registration.google.client-id=your_client_id
spring.security.oauth2.client.registration.google.client-secret=your_client_secret
```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8074`

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    full_name VARCHAR(255),
    google_id VARCHAR(255),
    enabled BOOLEAN DEFAULT true
);
```

### Tasks Table

```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20),
    status VARCHAR(20),
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    completion_date TIMESTAMP,
    user_id BIGINT REFERENCES users(id)
);
```

### Task History Table

```sql
CREATE TABLE task_history (
    id BIGSERIAL PRIMARY KEY,
    original_task_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20),
    status VARCHAR(20),
    due_date TIMESTAMP,
    action_type VARCHAR(20) NOT NULL,
    action_date TIMESTAMP NOT NULL,
    action_by VARCHAR(255) NOT NULL,
    completion_date TIMESTAMP
);
```

## API Endpoints

### Authentication

- `GET /login` - Login page
- `POST /login` - Process login
- `GET /register` - Registration page
- `POST /register` - Process registration
- `POST /logout` - Logout

### Tasks

- `GET /tasks` - List all tasks
- `GET /tasks/new` - New task form
- `POST /tasks` - Create new task
- `GET /tasks/{id}/edit` - Edit task form
- `POST /tasks/{id}` - Update task
- `DELETE /tasks/{id}` - Delete task
- `POST /tasks/{id}/complete` - Complete task
- `POST /tasks/{id}/start` - Start task

### Task History

- `GET /task-history` - View task history
- `GET /task-history?filter=completed` - View completed tasks
- `GET /task-history?filter=deleted` - View deleted tasks

## Security

- CSRF protection enabled
- Password encryption using BCrypt
- OAuth2 authentication
- Session management
- Secured endpoints
- User-specific data isolation

## Deployment

The application can be deployed to various platforms:

- Railway.app (Recommended)
- Render.com
- DigitalOcean
- Heroku
- AWS

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE.md file for details

## Contact

Temitope Ajayi - topsitech@gmail.com
Project Link: [https://github.com/Topebhh500/Task-Management-System](https://github.com/Topebhh500/Task-Management-System)

## Acknowledgments

- Spring Boot Documentation
- Bootstrap Documentation
- Font Awesome Icons
- Google OAuth2 Documentation
