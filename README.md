<!-- Banner -->
<div align="center">
  <img src="https://picsum.photos/1200/300" alt="Manishfest-Journal API Banner" width="100%"/>
</div>


<h1 align="center">📓 Manishfest-Journal API 📝✨</h1>

<p align="center">
A powerful and secure backend API for a modern journaling application, built with <strong>Spring Boot</strong>, <strong>MongoDB</strong>, and <strong>Kafka</strong>.
</p>

---

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot 3.x"/>
  <img src="https://img.shields.io/badge/Database-MongoDB-blue?style=for-the-badge&logo=mongodb" alt="MongoDB"/>
  <img src="https://img.shields.io/badge/Messaging-Kafka-black?style=for-the-badge&logo=apachekafka" alt="Kafka"/>
  <img src="https://img.shields.io/badge/License-MIT-blueviolet?style=for-the-badge" alt="License MIT"/>
</div>

---

## 🚀 Overview

JournalApp API is a **production-ready backend** designed to power a rich journaling experience. It provides secure user authentication, full CRUD operations for journal entries, a public community feed, and an automated weekly summary system that analyzes mood and sentiment. An asynchronous pipeline using Apache Kafka is used for notifications and summary processing.

---

## ✨ Key Features

- **🔐 Secure Authentication** — JWT-based authentication & role-based authorization (`USER`, `ADMIN`).  
- **✍️ Journal Management** — Create, update, delete, and fetch journal entries (private & public).  
- **🏷️ Tagging & Search** — Add hashtags, filter/search by tags, date, mood, etc.  
- **🌐 Public Feed** — Optionally share entries to a public community feed (commenting & rating).  
- **🧠 Weekly Summaries** — Scheduled job that calculates average mood, dominant mood, average sentiment, and dominant sentiment.  
- **📨 Kafka Notifications** — Weekly summaries are published/consumed via Kafka and converted into personalized emails.  
- **👮 Admin Endpoints** — Manage users, moderate content, clean up orphaned data.  
- **📖 API Docs** — Swagger UI / OpenAPI (SpringDoc) integrated.

---

## 🛠️ Tech Stack

- **Language:** Java 17  
- **Framework:** Spring Boot 3  
- **Database:** MongoDB  
- **Messaging:** Apache Kafka  
- **Security:** Spring Security, JWT  
- **Build:** Maven  
- **API Docs:** SpringDoc OpenAPI / Swagger UI

---

## 🏁 Getting Started

### Prerequisites

Make sure you have installed:

- Java 17 or later  
- Apache Maven  
- MongoDB (local or Atlas)  
- Kafka (local or Confluent Cloud)  
- An SMTP service (SendGrid, Gmail SMTP, etc.) for email delivery

---

### 1. Clone the repository

```bash
git clone https://github.com/your-username/journal-app-api.git
cd journal-app-api
```

---

### 2. Configuration

Create `src/main/resources/application.properties` (or `application.yml`) and add your secrets/configs. Example `application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb+srv://<user>:<pass>@cluster0.mongodb.net/journalapp?retryWrites=true&w=majority

# JWT
jwt.secret=replace_with_a_very_long_random_string
jwt.expiration.ms=86400000

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
# Additional kafka properties (SASL etc.) as needed

# SMTP (Email)
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-smtp-user
spring.mail.password=your-smtp-pass

# SpringDoc / Swagger (optional)
springdoc.api-docs.path=/v3/api-docs
```

> Tip: For production, store secrets in environment variables or a secrets manager. Do **not** commit credentials.

---

### 3. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default (unless overridden).

---

## 📖 API Documentation

Once the app is running, open the interactive docs:

- Swagger UI: `http://localhost:8080/swagger-ui.html` or `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 🧭 Project Structure (high level)

```
src/main/java
 └── net.manifest.journalapp
     ├── config        # Kafka, security, app configs
     ├── controller    # REST controllers (Auth, Journal, Admin)
     ├── dto           # Request/response DTOs
     ├── entity        # MongoDB entities (JournalEntry, User, WeeklySummary)
     ├── enums         # Mood, Sentiment, Roles
     ├── repository    # Spring Data MongoDB repositories
     ├── scheduler     # WeeklySummaryScheduler
     └── services      # Business logic & integrations
```

---

## ✅ Common Operational Notes

- **Weekly summary job** — runs as a scheduled task using Spring `@Scheduled` (e.g., every Sunday) and aggregates mood/sentiment per user.
- **Kafka** — used for decoupled processing (produce weekly summary events, consume to format/send emails).
- **Mongo indexes** — ensure `userId` and frequently queried fields are indexed for performance.
- **Enum storage** — enums are stored as strings for readability and backward compatibility.

---

## 🧪 Testing

- Unit tests: `mvn test`  
- For integration tests, use embedded MongoDB (Flapdoodle) or a test container, and a Kafka test container.

---

## 📬 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repo  
2. Create a feature branch (`git checkout -b feat/your-feature`)  
3. Commit your changes (`git commit -m "feat: ..."` )  
4. Push (`git push origin feat/your-feature`)  
5. Open a Pull Request and describe the change

Please open an issue before working on large features so we can coordinate.

---

## 📝 License

This project is licensed under the **MIT License** — see the `LICENSE` file for details.

---

## 🙋 Support / Contact

If you need help, open an issue, or contact the maintainer at `your-email@example.com`.

---

Thank you for using **JournalApp API** — build something beautiful. ✨
