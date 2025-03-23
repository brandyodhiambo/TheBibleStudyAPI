# Bible API

A RESTful API for Bible study and user management with secure authentication and authorization.

## Overview

Bible API is a Spring Boot application that provides a robust backend for Bible study applications. It includes comprehensive user management features with secure authentication, email verification, and role-based access control.

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.1**
  - Spring Boot Web
  - Spring Boot Data JPA
  - Spring Boot Security
  - Spring Boot Validation
  - Spring Boot Mail
  - Spring Boot Data Redis
- **JWT (JSON Web Token)** for authentication
- **Redis** for OTP management and caching
- **PostgreSQL** for production database
- **H2 Database** for development/testing
- **Lombok** for reducing boilerplate code
- **SpringDoc OpenAPI** for API documentation
- **Docker** for containerization
- **MailHog** for email testing

## Features

### User Management
- User registration with email verification
- Secure authentication with JWT
- Password encryption
- User profile management
- Profile image upload and retrieval
- Role-based access control (Admin, Group Leader, User)

### Security
- JWT-based authentication
- Role-based authorization
- Secure password handling
- Email verification for new accounts

### Email Services
- Email verification for new accounts
- Resend verification emails
- OTP (One-Time Password) management

## API Endpoints

### Authentication
- `POST /api/v1/auth/signup` - Register a new user
- `POST /api/v1/auth/signin` - Authenticate a user
- `POST /api/v1/auth/email/resend-verification` - Resend verification email
- `GET /api/v1/auth/email/verify` - Verify email with token

### User Management
- `GET /api/v1/users/check-username` - Check username availability
- `GET /api/v1/users/check-email` - Check email availability
- `GET /api/v1/users/getUser` - Get user information
- `PUT /api/v1/users/update/user/{username}` - Update user information
- `DELETE /api/v1/users/delete/user/{username}` - Delete a user (Admin only)
- `POST /api/v1/users/{username}/giveAdmin` - Grant admin privileges (Admin only)
- `DELETE /api/v1/users/{username}/removeAdmin` - Revoke admin privileges (Admin only)
- `POST /api/v1/users/{username}/give-group-leader` - Assign group leader role (Admin only)
- `DELETE /api/v1/users/{username}/remove-group-leader` - Remove group leader role (Admin only)

### Profile Image Management
- `POST /api/profile/upload/{username}` - Upload profile image
- `GET /api/profile/getProfile/{username}` - Get profile image information
- `GET /api/profile/image/{name}` - Get profile image by name

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Docker and Docker Compose (optional, for Redis and MailHog)
- PostgreSQL (for production)

### Configuration
The application can be configured through the `application.properties` file:

```properties
# Application name and server configuration
spring.application.name=bibleApi
server.port=8005

# Database configuration
# For H2 (development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# For PostgreSQL (production)
# spring.datasource.url=jdbc:postgresql://localhost:5432/bibleapi
# spring.datasource.username=postgres
# spring.datasource.password=postgres
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
brandyodhiambo.app.jwtSecret=your-secret-key
brandyodhiambo.app.jwtExpirationMs=86400000

# Email verification
email-verification.required=true

# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
```

### Running with Docker
1. Clone the repository
2. Start Redis and MailHog using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

### Running without Docker
1. Clone the repository
2. Install and start Redis server
3. Configure email settings in application.properties
4. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

## Development

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### API Documentation
Once the application is running, you can access the API documentation at:
```
http://localhost:8005/swagger-ui.html
```

## License
[MIT License](LICENSE)

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## Author
Brandy Odhiambo