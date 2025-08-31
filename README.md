# 📓 JournalApp API

A **robust, secure, and scalable REST API** for a modern journaling application — built with **Spring Boot** and **MongoDB**. Designed with security-first principles (JWT + role-based access) and a layered architecture (Controller → Service → Repository).

---

## ✨ Overview

JournalApp API provides the complete backend infrastructure for a **feature-rich journaling platform**. It supports individual user accounts, private & public journal entries, community interactions (comments, ratings), and administrative moderation. The API uses role-based access control and JWT for authentication.

---

## 🚀 Core Features

* **🔐 Secure Authentication** — JWT-based registration and login
* **🛡️ Role-Based Access Control** — `USER` & `ADMIN` roles
* **✍️ Full CRUD** — Create / Read / Update / Delete journal entries
* **🧩 Partial Updates** — PATCH support for efficient updates
* **🌍 Community Features** — Public entries, comments, ratings, public feed
* **⚡ Pagination & Sorting** — For all list endpoints
* **🛠️ Admin Panel** — User management (delete, ban, promote) and content moderation
* **🏗️ Scalable Design** — Controller → Service → Repository with DTOs for stable contracts
* **📖 Swagger/OpenAPI** — Interactive API documentation

---

## 🛠 Tech Stack

* Java 17+
* Spring Boot 3.x
* Spring Security 6.x (JWT)
* Spring Data MongoDB
* MongoDB (local or Atlas)
* JJWT (JWT generation & validation)
* Lombok
* Maven
* Swagger / OpenAPI 3

---

## ⚙️ Getting Started

### Prerequisites

* JDK 17 or higher
* Maven 3.8+
* MongoDB instance (local or Atlas)

### Quick start

```bash
# clone
git clone https://your-repository-url.com/journal-app.git
cd journal-app

# run
mvn spring-boot:run
```

The app will start on: `http://localhost:8080`

> On first run the application will create an initial admin user if configured.

---

## 🔧 Configuration

Edit `src/main/resources/application.properties` (or `application.yml`) to set database and JWT details.

Example `application.properties`:

```properties
# MongoDB (local)
spring.data.mongodb.uri=mongodb://localhost:27017/journaldb

# JWT
jwt.secret=YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256
jwt.expiration.ms=86400000  # 24 hours

# Initial Admin (first-run)
admin.username=superadmin
admin.name=Administrator
admin.email=admin@yourapp.com
admin.password=YourSecureInitialPassword123!
```

**Note on MongoDB Atlas & SRV URIs:**
If you use `mongodb+srv://` (Atlas) and experience DNS TXT lookup issues, either use the standard (non-SRV) connection string from Atlas or ensure your environment/DNS can resolve SRV/TXT records.

---

## 📖 API Documentation (Swagger)

Interactive docs (when the app is running):

* `http://localhost:8080/swagger-ui.html`
* or `http://localhost:8080/swagger-ui/index.html` (depending on Springdoc/version)

All secured endpoints require:

```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🧭 Endpoint Summary

> Base URL: `http://localhost:8080`

### Auth

| Endpoint                | Method | Description         | Access |
| ----------------------- | -----: | ------------------- | ------ |
| `/api/auth/user-signup` |   POST | Register a new user | Public |
| `/api/auth/login`       |   POST | Login — returns JWT | Public |

### User Profile

| Endpoint                  | Method | Description                | Access |
| ------------------------- | -----: | -------------------------- | ------ |
| `/api/me`                 |    GET | Get current user's profile | USER   |
| `/api/me`                 |    PUT | Update profile details     | USER   |
| `/api/me/change-password` |   POST | Change password            | USER   |

### My Journals (current user)

| Endpoint                | Method | Description                       | Access |
| ----------------------- | -----: | --------------------------------- | ------ |
| `/api/me/journals`      |    GET | Get paginated list of my journals | USER   |
| `/api/me/journals`      |   POST | Create a new journal entry        | USER   |
| `/api/me/journals/{id}` |    GET | Get a single entry                | USER   |
| `/api/me/journals/{id}` |    PUT | Replace an entire entry           | USER   |
| `/api/me/journals/{id}` |  PATCH | Partially update an entry         | USER   |
| `/api/me/journals/{id}` | DELETE | Delete one of your entries        | USER   |

### Public Journals

| Endpoint                     | Method | Description                      | Access        |
| ---------------------------- | -----: | -------------------------------- | ------------- |
| `/api/journals/public`       |    GET | Paginated list of public entries | PUBLIC / USER |
| `/api/journals/{id}`         |    GET | Get a single public entry        | PUBLIC / USER |
| `/api/journals/{id}/comment` |   POST | Comment on a public entry        | USER          |
| `/api/journals/{id}/rate`    |   POST | Rate a public entry              | USER          |

### Admin Panel

| Endpoint                        | Method | Description                           | Access |
| ------------------------------- | -----: | ------------------------------------- | ------ |
| `/api/admin/users`              |    GET | List all users                        | ADMIN  |
| `/api/admin/users/{id}`         | DELETE | Delete user + data                    | ADMIN  |
| `/api/admin/users/{id}/promote` |   POST | Promote user to ADMIN                 | ADMIN  |
| `/api/admin/users/{id}/ban`     |   POST | Ban a user's account                  | ADMIN  |
| `/api/admin/journals/{id}`      | DELETE | Delete any journal entry (moderation) | ADMIN  |

---

## ✅ API Response Standard

The project uses a unified `ApiResponse<T>` JSON wrapper to keep responses consistent.

**Success example**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Error example**

```json
{
  "success": false,
  "message": "No journal entry to delete for this id.",
  "data": null
}
```

**Tip:** Use `204 No Content` with an empty body for successful deletes if you want strict REST semantics; otherwise return `200 OK` with the `ApiResponse`.

---

## 🧩 Pagination notes

* Controller list endpoints return `Page<T>` internally.
* For a stable JSON contract, convert `Page<T>` into a DTO (e.g., `PagedResponse<T>`) or use Spring HATEOAS `PagedModel` rather than directly serializing `PageImpl`.

Example `PagedResponse<T>`:

```json
{
  "content": [ /* items */ ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 123,
  "totalPages": 13,
  "last": false
}
```

---

## 🧰 Development Guidelines

* Keep controllers thin — put business logic in service layer.
* Validate incoming payloads with `@Valid` and DTOs.
* Use `@RestControllerAdvice` for global exception handling to return `ApiResponse`.
* Avoid returning `204` with a body — follow spec (use `204` no-body or `200` with API response).
* Secure endpoints with role-based checks using Spring Security.

---

## 🧪 Tests

Run unit and integration tests:

```bash
mvn test
```

---

## 📦 Packaging

Build and run the JAR:

```bash
mvn clean package
java -jar target/journalapp-0.0.1-SNAPSHOT.jar
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "feat: description"`
4. Push: `git push origin feature/your-feature`
5. Open a pull request with a clear description

Please follow code style and include tests for new features.

---

## 📄 License

This project is licensed under the **MIT License**. See `LICENSE.md` for details.

---

## ✉️ Contact

If you need help, improvements, or a review — open an issue or contact the repository owner.

---

If you want, I can also:

* create a ready-to-use `application.properties.example`,
* add a `CONTRIBUTING.md`, or
* generate a Postman collection for the main endpoints.
  Which one should I create next?

