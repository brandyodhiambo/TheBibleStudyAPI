# Bible API

A comprehensive RESTful API for Bible study applications with advanced user management, group management, and robust security features.

## Overview

Bible API is a modern Spring Boot application designed to provide a secure and scalable backend for Bible study applications. It offers a complete set of features including user authentication, email verification, role-based access control, group management, and profile image handling. Built with Java 17 and Spring Boot 3, this API follows best practices for security, performance, and code organization.

## System Architecture

The Bible API follows a layered architecture pattern with clear separation of concerns:

```
+----------------+     +----------------+     +------------------------+
|                |     |                |     |  Spring Boot App       |
|     Client     +---->+  API Gateway   +---->+                        |
|                |     |                |     |  +------------------+  |
+----------------+     +----------------+     |  | Authentication   |  |
                                              |  +------------------+  |
                                              |                        |
                                              |  +------------------+  |
                                              |  | User Management  |  |
                                              |  +------------------+  |
                                              |                        |
                                              |  +------------------+  |
                                              |  | Group Management |  |
                                              |  +------------------+  |
                                              |                        |
                                              |  +------------------+  |
                                              |  | Email Service    |  |
                                              |  +------------------+  |
                                              |                        |
                                              |  +------------------+  |
                                              |  | Profile Image    |  |
                                              |  +------------------+  |
                                              |                        |
                                              |  +------------------+  |
                                              |  | API Documentation|  |
                                              |  +------------------+  |
                                              +------------------------+
                                                         |
                                                         v
                       +------------+    +----------+    +------------+
                       | PostgreSQL |    |  Redis   |    | Email      |
                       | (Database) |    | (Cache)  |    | Server     |
                       +------------+    +----------+    +------------+
```

**System Architecture Diagram**

The system consists of the following components:
- **Client Applications**: Web, mobile, or desktop applications that consume the API
- **API Gateway**: Entry point for all client requests
- **Spring Boot Application**: Core application with various modules:
  - Authentication Service: Handles user authentication and JWT token management
  - User Management: Manages user accounts, roles, and profile information
  - Group Management: Handles Bible study groups, memberships, and leadership
  - Email Service: Manages email verification and notifications
  - Profile Image Service: Handles user profile image upload and retrieval
  - API Documentation: Swagger/OpenAPI documentation
- **Databases**:
  - PostgreSQL: Primary database for storing application data
  - Redis: For caching and OTP management
- **External Services**:
  - Email Server: For sending verification emails and notifications

## Security Architecture

The API implements a comprehensive security model:

```
+----------------+
|     Client     |
+-------+--------+
        |
        v
+-------+------------------------------------------+
|                Spring Security Framework         |
|                                                  |
|  +-------------------+    +-------------------+  |
|  | JWT Authentication|    | Authentication    |  |
|  | Filter            |    | Provider          |  |
|  +--------+----------+    +---------+---------+  |
|           |                         |            |
|           v                         v            |
|  +-------------------+    +-------------------+  |
|  | User Details      |    | BCrypt Password   |  |
|  | Service           |    | Encoder           |  |
|  +--------+----------+    +---------+---------+  |
|           |                         |            |
|           v                         v            |
|  +-------------------+    +-------------------+  |
|  | Role-Based Access |    | Method Security   |  |
|  | Control           |    |                   |  |
|  +--------+----------+    +---------+---------+  |
|           |                         |            |
|           v                         v            |
|  +-------------------+                           |
|  | Email Verification|                           |
|  |                   |                           |
|  +-------------------+                           |
+-------+------------------------------------------+
        |
        v
+-------+--------+    +-------------------+
| User & Role     |    | Redis            |
| Database        |    | (OTP Storage)    |
+----------------+    +-------------------+
```

**Security Architecture Diagram**

Key security features include:
- **JWT-based Authentication**: Secure, stateless authentication using JSON Web Tokens
- **Role-Based Access Control**: Three-tiered role system (Admin, Group Leader, Member)
- **Password Encryption**: BCrypt password hashing
- **Email Verification**: Required email verification for new accounts
- **Method-Level Security**: Fine-grained access control at the method level

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

### Bible Management
- Access to Bible translations, books, chapters, and verses
- Bookmark verses for quick reference
- Highlight verses with different colors
- Search for verses by keyword or reference
- View bookmarked and highlighted verses

### User Management
- User registration with email verification
- Secure authentication with JWT
- Password encryption
- User profile management
- Profile image upload and retrieval
- Role-based access control (Admin, Group Leader, User)

### Group Management
- Create and manage Bible study groups
- Join or leave groups
- View group details (leader, members, location, time, etc.)
- Add or remove members from groups
- Group chat for members to discuss study topics and share insights

### Event Management
- Schedule recurring or one-time Bible study sessions
- RSVP to study sessions to indicate attendance
- Send reminders for upcoming sessions
- View session details and attendance

### Study Material Management
- Upload and share study guides, reading plans, and related resources
- Assign specific Bible readings or study topics for each session
- Search for study material by keywords, topics, or Bible verses
- Comment on study materials to share insights and ask questions

### Prayer Requests
- Submit prayer requests within a group
- Track prayer requests and mark them as answered
- Share testimonies for answered prayers
- View prayer requests by group or by user

### Reporting and Analytics
- Track Participation: Generate reports on group participation, including member attendance, engagement levels, and prayer request activity
- Progress Tracking: Monitor group or individual progress through reading plans and study guides
- Trend Analysis: Compare metrics over time to identify trends in participation and engagement
- Customizable Reports: Generate specific reports for attendance, engagement, prayer activity, and reading progress

### Security
- JWT-based authentication
- Role-based authorization
- Secure password handling
- Email verification for new accounts

### Email Services
- Email verification for new accounts
- Resend verification emails
- OTP (One-Time Password) management
- Session reminders and notifications

## API Endpoints

### Bible Management
- `GET /api/v1/bibles` - Get all Bible translations
- `GET /api/v1/bibles/{id}` - Get a Bible by ID
- `GET /api/v1/bibles/name/{name}` - Get a Bible by name
- `GET /api/v1/bibles/abbreviation/{abbreviation}` - Get a Bible by abbreviation
- `GET /api/v1/bibles/{bibleId}/books` - Get all books in a Bible
- `GET /api/v1/bibles/books/{id}` - Get a book by ID
- `GET /api/v1/bibles/{bibleId}/books/name/{name}` - Get a book by name in a Bible
- `GET /api/v1/bibles/{bibleId}/books/abbreviation/{abbreviation}` - Get a book by abbreviation in a Bible
- `GET /api/v1/bibles/books/{bookId}/chapters` - Get all chapters in a book
- `GET /api/v1/bibles/chapters/{id}` - Get a chapter by ID
- `GET /api/v1/bibles/books/{bookId}/chapters/{number}` - Get a chapter by number in a book
- `GET /api/v1/bibles/chapters/{chapterId}/verses` - Get all verses in a chapter
- `GET /api/v1/bibles/verses/{id}` - Get a verse by ID
- `GET /api/v1/bibles/chapters/{chapterId}/verses/{number}` - Get a verse by number in a chapter
- `GET /api/v1/bibles/verses/reference` - Get a verse by reference (book name, chapter number, verse number)
- `GET /api/v1/bibles/search` - Search for verses by keyword

### Bookmark Management
- `POST /api/v1/bookmarks/verses/{verseId}` - Create a bookmark for a verse
- `GET /api/v1/bookmarks` - Get all bookmarks for the current user
- `GET /api/v1/bookmarks/all` - Get all bookmarks (admin only)
- `GET /api/v1/bookmarks/{id}` - Get a bookmark by ID
- `GET /api/v1/bookmarks/verses/{verseId}` - Get a bookmark for a specific verse
- `PUT /api/v1/bookmarks/{id}` - Update the notes for a bookmark
- `DELETE /api/v1/bookmarks/{id}` - Delete a bookmark
- `GET /api/v1/bookmarks/verses/{verseId}/check` - Check if a verse is bookmarked by the current user

### Highlight Management
- `POST /api/v1/highlights/verses/{verseId}` - Create a highlight for a verse
- `GET /api/v1/highlights` - Get all highlights for the current user
- `GET /api/v1/highlights/all` - Get all highlights (admin only)
- `GET /api/v1/highlights/{id}` - Get a highlight by ID
- `GET /api/v1/highlights/verses/{verseId}` - Get a highlight for a specific verse
- `PUT /api/v1/highlights/{id}` - Update the color for a highlight
- `DELETE /api/v1/highlights/{id}` - Delete a highlight
- `GET /api/v1/highlights/verses/{verseId}/check` - Check if a verse is highlighted by the current user

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

### Group Management
- `POST /api/v1/groups` - Create a new group (Leader or Admin only)
- `PUT /api/v1/groups/{groupId}` - Update a group (Leader or Admin only)
- `DELETE /api/v1/groups/{groupId}` - Delete a group (Leader or Admin only)
- `GET /api/v1/groups/{groupId}` - Get a group by ID
- `GET /api/v1/groups` - Get all groups
- `GET /api/v1/groups/leader` - Get groups led by the current user
- `GET /api/v1/groups/member` - Get groups the current user is a member of
- `POST /api/v1/groups/{groupId}/members` - Add a member to a group (Leader or Admin only)
- `DELETE /api/v1/groups/{groupId}/members` - Remove a member from a group
- `POST /api/v1/groups/{groupId}/join` - Join a group
- `POST /api/v1/groups/{groupId}/leave` - Leave a group

### Study Session Management
- `POST /api/v1/sessions` - Create a new study session (Leader or Admin only)
- `PUT /api/v1/sessions/{sessionId}` - Update a study session (Leader or Admin only)
- `DELETE /api/v1/sessions/{sessionId}` - Delete a study session (Leader or Admin only)
- `GET /api/v1/sessions/{sessionId}` - Get a study session by ID
- `GET /api/v1/sessions/group/{groupId}` - Get all study sessions for a group
- `GET /api/v1/sessions/group/{groupId}/upcoming` - Get upcoming study sessions for a group
- `GET /api/v1/sessions/user/upcoming` - Get upcoming study sessions for the current user
- `GET /api/v1/sessions/date-range` - Get study sessions by date range
- `GET /api/v1/sessions/group/{groupId}/date-range` - Get study sessions by date range for a group
- `POST /api/v1/sessions/send-reminders` - Manually trigger session reminders (Admin only)

### RSVP Management
- `POST /api/v1/rsvps/sessions/{sessionId}` - Submit an RSVP for a session
- `PUT /api/v1/rsvps/{rsvpId}` - Update an existing RSVP
- `DELETE /api/v1/rsvps/{rsvpId}` - Delete an RSVP
- `GET /api/v1/rsvps/{rsvpId}` - Get an RSVP by ID
- `GET /api/v1/rsvps/sessions/{sessionId}` - Get all RSVPs for a session
- `GET /api/v1/rsvps/sessions/{sessionId}/status/{status}` - Get RSVPs by status
- `GET /api/v1/rsvps/user` - Get all RSVPs by the current user
- `GET /api/v1/rsvps/sessions/{sessionId}/user` - Get user's RSVP for a session
- `GET /api/v1/rsvps/sessions/{sessionId}/count/{status}` - Count RSVPs by status

### Study Material Management
- `POST /api/v1/study-materials/upload` - Upload a study material (Leader or Admin only)
- `GET /api/v1/study-materials/{materialId}` - Get a study material by ID
- `GET /api/v1/study-materials/group/{groupId}` - Get all study materials for a group
- `GET /api/v1/study-materials/user` - Get all study materials uploaded by the current user
- `GET /api/v1/study-materials/search` - Search study materials by keywords
- `GET /api/v1/study-materials/search/group/{groupId}` - Search study materials within a group
- `PUT /api/v1/study-materials/{materialId}` - Update a study material
- `DELETE /api/v1/study-materials/{materialId}` - Delete a study material
- `GET /api/v1/study-materials/download/{materialId}` - Download a study material file

### Reading Plan Management
- `POST /api/v1/reading-plans` - Create a new reading plan (Leader or Admin only)
- `GET /api/v1/reading-plans/{planId}` - Get a reading plan by ID
- `GET /api/v1/reading-plans/group/{groupId}` - Get all reading plans for a group
- `GET /api/v1/reading-plans/user` - Get all reading plans created by the current user
- `GET /api/v1/reading-plans/search` - Search reading plans by keywords
- `GET /api/v1/reading-plans/search/group/{groupId}` - Search reading plans within a group
- `GET /api/v1/reading-plans/date-range` - Get reading plans within a date range
- `GET /api/v1/reading-plans/group/{groupId}/date-range` - Get reading plans within a date range for a group
- `PUT /api/v1/reading-plans/{planId}` - Update a reading plan
- `DELETE /api/v1/reading-plans/{planId}` - Delete a reading plan

### Group Chat Management
- `POST /api/v1/chat/groups/{groupId}/messages` - Send a message to a group
- `GET /api/v1/chat/groups/{groupId}/messages` - Get all messages for a group
- `GET /api/v1/chat/messages/{messageId}` - Get a specific message by ID
- `DELETE /api/v1/chat/messages/{messageId}` - Delete a message

### Comment Management
- `POST /api/v1/comments/study-materials/{studyMaterialId}` - Add a comment to a study material
- `GET /api/v1/comments/study-materials/{studyMaterialId}` - Get all comments for a study material
- `GET /api/v1/comments/{commentId}` - Get a specific comment by ID
- `PUT /api/v1/comments/{commentId}` - Update a comment
- `DELETE /api/v1/comments/{commentId}` - Delete a comment
- `GET /api/v1/comments/user` - Get all comments by the current user

### Prayer Request Management
- `POST /api/v1/prayer-requests/groups/{groupId}` - Create a new prayer request for a group
- `GET /api/v1/prayer-requests/groups/{groupId}` - Get all prayer requests for a group
- `GET /api/v1/prayer-requests/groups/{groupId}/answered/{answered}` - Get prayer requests for a group by answered status
- `GET /api/v1/prayer-requests/{prayerRequestId}` - Get a specific prayer request by ID
- `PUT /api/v1/prayer-requests/{prayerRequestId}` - Update a prayer request
- `PUT /api/v1/prayer-requests/{prayerRequestId}/mark-answered` - Mark a prayer request as answered and add testimony
- `DELETE /api/v1/prayer-requests/{prayerRequestId}` - Delete a prayer request
- `GET /api/v1/prayer-requests/user` - Get all prayer requests by the current user
- `GET /api/v1/prayer-requests/user/answered/{answered}` - Get prayer requests by the current user by answered status

### Analytics and Reporting Management
- `POST /api/v1/analytics/reading-plans/{readingPlanId}/progress` - Track user's reading plan progress
- `GET /api/v1/analytics/reading-plans/{readingPlanId}/progress` - Get reading progress for current user and reading plan
- `GET /api/v1/analytics/reading-plans/progress` - Get all reading progress entries for the current user
- `GET /api/v1/analytics/reading-plans/{readingPlanId}/all-progress` - Get all reading progress entries for a reading plan (Leader/Admin)
- `GET /api/v1/analytics/reading-plans/{readingPlanId}/average-completion` - Calculate average completion percentage (Leader/Admin)
- `POST /api/v1/analytics/groups/{groupId}/participation` - Update participation metrics for the current user
- `GET /api/v1/analytics/groups/{groupId}/participation` - Get participation metrics for the current user
- `GET /api/v1/analytics/participation` - Get all participation metrics for the current user
- `GET /api/v1/analytics/groups/{groupId}/all-participation` - Get all participation metrics for a group (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/analytics` - Get aggregated analytics for a group (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/analytics/trends` - Get analytics with trend comparison (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/reports/attendance` - Generate attendance report (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/reports/engagement` - Generate engagement report (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/reports/prayer-activity` - Generate prayer activity report (Leader/Admin)
- `GET /api/v1/analytics/groups/{groupId}/reports/reading-progress` - Generate reading progress report (Leader/Admin)

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

## Database Schema

The database schema is visualized in the following Entity-Relationship Diagram (ERD):

```
+----------------+       +----------------+       +----------------+
|     Users      |       |   user_roles   |       |     Role       |
|----------------|       |----------------|       |----------------|
| id (PK)        |<----->| user_id (FK)   |<----->| role_id (PK)   |
| firstName      |       | role_id (FK)   |       | name           |
| lastName       |       +----------------+       +----------------+
| username       |                                        |
| email          |                                        |
| password       |                                        v
| emailVerified  |                                +----------------+
| createdAt      |                                |   RoleName     |
| updatedAt      |                                |----------------|
+-------+--------+                                | ROLE_ADMIN     |
        |                                         | ROLE_LEADER    |
        |                                         | ROLE_MEMBER    |
        |                                         +----------------+
        |
        |       +----------------+       +----------------+       +----------------+
        |       |     Bible      |       |     Book       |       |    Chapter     |
        +------>|----------------|       |----------------|       |----------------|
        |       | id (PK)        |------>| id (PK)        |------>| id (PK)        |
        |       | name           |       | name           |       | number         |
        |       | abbreviation   |       | abbreviation   |       | book_id (FK)   |
        |       | description    |       | position       |       | createdAt      |
        |       | language       |       | description    |       | updatedAt      |
        |       | createdAt      |       | bible_id (FK)  |       +-------+--------+
        |       | updatedAt      |       | createdAt      |               |
        |       +----------------+       | updatedAt      |               |
        |                                +----------------+               |
        |                                                                 |
        |                                                                 v
        |                                                        +----------------+
        |                                                        |     Verse      |
        |                                                        |----------------|
        |                                                        | id (PK)        |
        |                                                        | number         |
        |                                                        | text           |
        |                                                        | chapter_id (FK)|
        |                                                        | createdAt      |
        |                                                        | updatedAt      |
        |                                                        +-------+--------+
        |                                                                |
        |       +----------------+                                       |
        |       |    Bookmark    |                                       |
        +------>|----------------|                                       |
        |       | id (PK)        |<--------------------------------------+
        |       | verse_id (FK)  |
        |       | user_id (FK)   |
        |       | notes          |
        |       | createdAt      |
        |       | updatedAt      |
        |       +----------------+
        |
        |       +----------------+
        |       |   Highlight    |
        +------>|----------------|
                | id (PK)        |<--------------------------------------+
                | verse_id (FK)  |
                | user_id (FK)   |
                | color          |
                | createdAt      |
                | updatedAt      |
                +----------------+
        |
        |       +----------------+
        |       |   UserImage    |
        +------>|----------------|
        |       | id (PK)        |
        |       | user_id (FK)   |
        |       | imageData      |
        |       +----------------+
        |
        |       +----------------+       +----------------+
        |       |     Group      |       |   GroupType    |
        +------>|----------------|       |----------------|
        |       | id (PK)        |       | VIRTUAL        |
        |       | name           |       | IN_PERSON      |
        |       | description    |       +----------------+
        |       | location       |               ^
        |       | meetingTime    |               |
        |       | type           |---------------+
        |       | leader_id (FK) |
        |       | createdAt      |
        |       | updatedAt      |
        |       +-------+--------+
        |               |
        |               |
        |       +-------v--------+
        |       | group_members  |
        +------>|----------------|
        |       | group_id (FK)  |
        |       | user_id (FK)   |
        |       +----------------+
        |
        |       +----------------+
        |       | chat_messages  |
        +------>|----------------|
        |       | id (PK)        |
        |       | content        |
        |       | group_id (FK)  |
        |       | sender_id (FK) |
        |       | createdAt      |
        |       | updatedAt      |
        |       +----------------+
        |
        |       +----------------+       +----------------+
        |       | study_sessions |       | SessionType    |
        +------>|----------------|       |----------------|
        |       | id (PK)        |       | VIRTUAL        |
        |       | title          |       | IN_PERSON      |
        |       | description    |       | HYBRID         |
        |       | sessionDate    |       +----------------+
        |       | startTime      |               ^
        |       | endTime        |               |
        |       | location       |               |
        |       | type           |---------------+
        |       | recPattern     |               |
        |       | recEndDate     |               |
        |       | group_id (FK)  |       +----------------+
        |       | created_by (FK)|       | RecurrencePattern|
        |       | createdAt      |       |----------------|
        |       | updatedAt      |       | NONE           |
        |       +-------+--------+       | DAILY          |
                        |                | WEEKLY         |
                        |                | BI_WEEKLY      |
                        |                | MONTHLY        |
                        |                | CUSTOM         |
                        |                +----------------+
                        |                        ^
                        |                        |
                        |                        |
                +-------v--------+       +----------------+
                | session_rsvps  |       | RSVPStatus     |
                |----------------|       |----------------|
                | id (PK)        |       | ATTENDING      |
                | session_id (FK)|       | NOT_ATTENDING  |
                | user_id (FK)   |       | MAYBE          |
                | status         |-------+----------------+
                | comment        |
                | createdAt      |
                | updatedAt      |
                +----------------+

        |       +----------------+
        |       | study_materials|
        +------>|----------------|
        |       | id (PK)        |
        |       | title          |
        |       | description    |
        |       | fileName       |
        |       | fileType       |
        |       | fileSize       |
        |       | fileData       |
        |       | group_id (FK)  |
        |       | uploaded_by (FK)|
        |       | keywords       |
        |       | createdAt      |
        |       | updatedAt      |
        |       +-------+--------+
        |               |
        |               |
        |       +-------v--------+
        |       |    comments    |
        +------>|----------------|
        |       | id (PK)        |
        |       | content        |
        |       | study_material_id (FK) |
        |       | user_id (FK)   |
        |       | createdAt      |
        |       | updatedAt      |
        |       +----------------+
        |
        |       +----------------+
        |       | reading_plans  |
        +------>|----------------|
        |       | id (PK)        |
        |       | title          |
        |       | description    |
        |       | bibleReferences|
        |       | startDate      |
        |       | endDate        |
        |       | group_id (FK)  |
        |       | created_by (FK)|
        |       | topics         |
        |       | createdAt      |
        |       | updatedAt      |
        |       +----------------+
        |
        |       +----------------+
        |       | prayer_requests|
        +------>|----------------|
        |       | id (PK)        |
        |       | title          |
        |       | description    |
        |       | group_id (FK)  |
        |       | user_id (FK)   |
        |       | answered       |
        |       | testimony      |
        |       | createdAt      |
        |       | updatedAt      |
        |       +----------------+
```

**Database Schema Diagram**

The diagram shows the main entities and their relationships in the system:
- **Users**: Core entity storing user information
- **Roles**: Available roles in the system (Admin, Leader, Member)
- **Groups**: Bible study groups with their properties
- **User Images**: Profile images for users

### Tables

#### Bible Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| name           | VARCHAR(255) | NOT NULL, UNIQUE          |
| abbreviation   | VARCHAR(50)  | NOT NULL                  |
| description    | TEXT         |                           |
| language       | VARCHAR(50)  | NOT NULL                  |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Book Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| name           | VARCHAR(255) | NOT NULL                  |
| abbreviation   | VARCHAR(50)  | NOT NULL                  |
| position       | INTEGER      | NOT NULL                  |
| description    | TEXT         |                           |
| bible_id       | BIGINT       | FK -> bibles.id, NOT NULL |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Chapter Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| number         | INTEGER      | NOT NULL                  |
| book_id        | BIGINT       | FK -> books.id, NOT NULL  |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Verse Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| number         | INTEGER      | NOT NULL                  |
| text           | TEXT         | NOT NULL                  |
| chapter_id     | BIGINT       | FK -> chapters.id, NOT NULL |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Bookmark Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| verse_id       | BIGINT       | FK -> verses.id, NOT NULL |
| user_id        | BIGINT       | FK -> users.id, NOT NULL  |
| notes          | TEXT         |                           |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Highlight Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| verse_id       | BIGINT       | FK -> verses.id, NOT NULL |
| user_id        | BIGINT       | FK -> users.id, NOT NULL  |
| color          | VARCHAR(50)  | NOT NULL                  |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Users Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| first_name     | TEXT         | NOT NULL                  |
| last_name      | TEXT         | NOT NULL                  |
| username       | TEXT         | NOT NULL                  |
| email          | TEXT         | NOT NULL, UNIQUE          |
| password       | TEXT         | NOT NULL                  |
| email_verified | BOOLEAN      | NOT NULL                  |
| created_at     | DATE         | NOT NULL                  |
| updated_at     | DATE         | NOT NULL                  |

#### Roles Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| role_id        | BIGINT       | PK, AUTO_INCREMENT        |
| name           | VARCHAR(255) | NOT NULL, ENUM            |

#### User Roles Table (Join Table)
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| user_id        | BIGINT       | FK -> users.id            |
| role_id        | BIGINT       | FK -> roles.role_id       |

#### Groups Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| name           | VARCHAR(255) | NOT NULL                  |
| description    | TEXT         |                           |
| location       | VARCHAR(255) |                           |
| meeting_time   | TIME         |                           |
| type           | VARCHAR(255) | ENUM                      |
| leader_id      | BIGINT       | FK -> users.id, NOT NULL  |
| created_at     | DATE         | NOT NULL                  |
| updated_at     | DATE         | NOT NULL                  |

#### Group Members Table (Join Table)
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| group_id       | BIGINT       | FK -> groups.id           |
| user_id        | BIGINT       | FK -> users.id            |

#### Study Sessions Table
| Column           | Type         | Constraints                |
|------------------|--------------|----------------------------|
| id               | BIGINT       | PK, AUTO_INCREMENT        |
| title            | VARCHAR(255) | NOT NULL                  |
| description      | TEXT         |                           |
| session_date     | DATE         | NOT NULL                  |
| start_time       | TIME         | NOT NULL                  |
| end_time         | TIME         |                           |
| location         | VARCHAR(255) |                           |
| type             | VARCHAR(255) | ENUM                      |
| recurrence_pattern | VARCHAR(255) | ENUM                    |
| recurrence_end_date | DATE      |                           |
| group_id         | BIGINT       | FK -> groups.id, NOT NULL |
| created_by       | BIGINT       | FK -> users.id, NOT NULL  |
| created_at       | TIMESTAMP    | NOT NULL                  |
| updated_at       | TIMESTAMP    | NOT NULL                  |

#### Session RSVPs Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| session_id     | BIGINT       | FK -> study_sessions.id, NOT NULL |
| user_id        | BIGINT       | FK -> users.id, NOT NULL  |
| status         | VARCHAR(255) | ENUM, NOT NULL            |
| comment        | TEXT         |                           |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Study Materials Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| title          | VARCHAR(255) | NOT NULL                  |
| description    | TEXT         |                           |
| file_name      | VARCHAR(255) | NOT NULL                  |
| file_type      | VARCHAR(255) | NOT NULL                  |
| file_size      | BIGINT       | NOT NULL                  |
| file_data      | LONGBLOB     | NOT NULL                  |
| group_id       | BIGINT       | FK -> groups.id, NOT NULL |
| uploaded_by    | BIGINT       | FK -> users.id, NOT NULL  |
| keywords       | VARCHAR(255) | NOT NULL                  |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Reading Plans Table
| Column           | Type         | Constraints                |
|------------------|--------------|----------------------------|
| id               | BIGINT       | PK, AUTO_INCREMENT        |
| title            | VARCHAR(255) | NOT NULL                  |
| description      | TEXT         |                           |
| bible_references | TEXT         | NOT NULL                  |
| start_date       | DATE         | NOT NULL                  |
| end_date         | DATE         | NOT NULL                  |
| group_id         | BIGINT       | FK -> groups.id, NOT NULL |
| created_by       | BIGINT       | FK -> users.id, NOT NULL  |
| topics           | VARCHAR(255) | NOT NULL                  |
| created_at       | TIMESTAMP    | NOT NULL                  |
| updated_at       | TIMESTAMP    | NOT NULL                  |

#### Chat Messages Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| content        | TEXT         | NOT NULL                  |
| group_id       | BIGINT       | FK -> groups.id, NOT NULL |
| sender_id      | BIGINT       | FK -> users.id, NOT NULL  |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### Comments Table
| Column           | Type         | Constraints                |
|------------------|--------------|----------------------------|
| id               | BIGINT       | PK, AUTO_INCREMENT        |
| content          | TEXT         | NOT NULL                  |
| study_material_id| BIGINT       | FK -> study_materials.id, NOT NULL |
| user_id          | BIGINT       | FK -> users.id, NOT NULL  |
| created_at       | TIMESTAMP    | NOT NULL                  |
| updated_at       | TIMESTAMP    | NOT NULL                  |

#### Prayer Requests Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| title          | VARCHAR(255) | NOT NULL                  |
| description    | TEXT         | NOT NULL                  |
| group_id       | BIGINT       | FK -> groups.id, NOT NULL |
| user_id        | BIGINT       | FK -> users.id, NOT NULL  |
| answered       | BOOLEAN      | NOT NULL                  |
| testimony      | TEXT         |                           |
| created_at     | TIMESTAMP    | NOT NULL                  |
| updated_at     | TIMESTAMP    | NOT NULL                  |

#### User Images Table
| Column         | Type         | Constraints                |
|----------------|--------------|----------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT        |
| user_id        | BIGINT       | FK -> users.id, NOT NULL  |
| imagedata      | BLOB         |                           |

### Entity Relationships

```
Bible (1) <----> (N) Book               (One-to-Many)
Book (1) <----> (N) Chapter             (One-to-Many)
Chapter (1) <----> (N) Verse            (One-to-Many)
Users (1) <----> (N) Bookmark           (One-to-Many)
Verse (1) <----> (N) Bookmark           (One-to-Many)
Users (1) <----> (N) Highlight          (One-to-Many)
Verse (1) <----> (N) Highlight          (One-to-Many)
Users (1) <----> (0..1) UserImage       (One-to-One)
Users (N) <----> (M) Role               (Many-to-Many through user_roles)
Users (1) <----> (N) Group              (One-to-Many as leader)
Users (N) <----> (M) Group              (Many-to-Many through group_members)
Users (1) <----> (N) ChatMessage        (One-to-Many as sender)
Group (1) <----> (N) ChatMessage        (One-to-Many)
Users (1) <----> (N) StudySession       (One-to-Many as creator)
Users (N) <----> (M) StudySession       (Many-to-Many through session_rsvps)
Group (1) <----> (N) StudySession       (One-to-Many)
StudySession (1) <----> (N) SessionRSVP (One-to-Many)
Users (1) <----> (N) StudyMaterial      (One-to-Many as uploader)
Group (1) <----> (N) StudyMaterial      (One-to-Many)
StudyMaterial (1) <----> (N) Comment    (One-to-Many)
Users (1) <----> (N) Comment            (One-to-Many)
Users (1) <----> (N) ReadingPlan        (One-to-Many as creator)
Group (1) <----> (N) ReadingPlan        (One-to-Many)
Users (1) <----> (N) PrayerRequest      (One-to-Many)
Group (1) <----> (N) PrayerRequest      (One-to-Many)
```


## Continuous Integration

This project uses GitHub Actions for continuous integration. The CI pipeline automatically builds the project and runs tests on every push to the main branch and on pull requests.

The workflow includes:
- Building the project with Gradle
- Running tests
- Setting up Redis for integration tests

You can view the CI workflow configuration in the `.github/workflows/build.yml` file.

## License
[MIT License](LICENSE)

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## Author
Brandy Odhiambo
