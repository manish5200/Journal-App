JournalApp API
A robust, secure, and scalable REST API for a modern journaling application, built with Spring Boot and MongoDB.

Overview
JournalApp API provides the complete backend infrastructure for a feature-rich journaling platform. It supports individual user accounts, private and public journal entries, community interactions, and administrative moderation. The API is designed with a security-first mindset, using role-based access control and JWT for authentication.

Core Features
Secure Authentication: User registration and login system secured by JWT.

Role-Based Access Control: Clear distinction between USER and ADMIN roles.

Full CRUD Operations: Create, Read, Update, and Delete operations for personal journal entries.

Partial Updates: Supports PATCH requests for efficient partial updates to journal entries.

Community Features:

Ability to make journal entries public.

Browse a feed of all public entries.

Comment on and rate public entries.

Pagination & Sorting: All list endpoints are paginated and sortable for high performance.

Admin Panel: Secure endpoints for administrators to manage users (delete, ban, promote) and moderate content.

Scalable Design: Built on a clean, layered architecture (Controller-Service-Repository) with DTOs for a stable API contract.

Tech Stack
Java 17+

Spring Boot 3.x

Spring Security 6.x (for authentication & authorization)

Spring Data MongoDB (for database interaction)

MongoDB (NoSQL database)

JJwt (for JSON Web Token generation and validation)

Lombok (to reduce boilerplate code)

Maven (for dependency management)

Swagger / OpenAPI 3 (for API documentation)

Getting Started
Prerequisites
JDK 17 or higher

Maven 3.8+

A running MongoDB instance

Setup & Configuration
Clone the repository:

git clone [https://your-repository-url.com/journal-app.git](https://your-repository-url.com/journal-app.git)
cd journal-app

Configure the application:
Open src/main/resources/application.properties and configure your database connection and JWT secret.

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/journaldb

# JWT Configuration
jwt.secret=YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256
jwt.expiration.ms=86400000 # 24 hours

# Initial Admin User (for the very first run)
admin.username=superadmin
admin.name=Administrator
admin.email=admin@yourapp.com
admin.password=YourSecureInitialPassword123!

Run the application:

mvn spring-boot:run

The application will start on http://localhost:8080. On the very first run, it will automatically create the initial admin user specified in the properties file.

API Documentation
Once the application is running, you can access the interactive Swagger UI documentation at:
http://localhost:8080/swagger-ui.html

API Endpoint Summary
All secured endpoints require an Authorization: Bearer <JWT_TOKEN> header.

Controller

Endpoint

Method

Description

Access

Auth Controller

/api/auth/signup

POST

Register a new user.

Public



/api/auth/login

POST

Log in to get a JWT token.

Public

User Profile Controller

/api/me

GET

Get your own profile.

USER



/api/me

PUT

Update your profile details.

USER



/api/me/change-password

POST

Change your password.

USER

My Journal Controller

/api/me/journals

GET

Get a paginated list of your own journals.

USER



/api/me/journals

POST

Create a new journal entry for yourself.

USER



/api/me/journals/{id}

GET

Get a single one of your entries.

USER



/api/me/journals/{id}

PUT

Replace an entire entry.

USER



/api/me/journals/{id}

PATCH

Partially update an entry.

USER



/api/me/journals/{id}

DELETE

Delete one of your entries.

USER

Public Journal Controller

/api/journals/public

GET

Get a paginated list of all public entries.

USER



/api/journals/{id}

GET

Get a single public entry.

USER



/api/journals/{id}/comment

POST

Comment on a public entry.

USER



/api/journals/{id}/rate

POST

Rate a public entry.

USER

Admin Controller

/api/admin/users

GET

Get a list of all users.

ADMIN



/api/admin/users/{id}

DELETE

Delete a user and all their data.

ADMIN



/api/admin/users/{id}/promote

POST

Promote a user to Admin.

ADMIN



/api/admin/users/{id}/ban

POST

Ban a user's account.

ADMIN



/api/admin/journals/{id}

DELETE

Delete any journal entry for moderation.

ADMIN

License
This project is licensed under the MIT License.
