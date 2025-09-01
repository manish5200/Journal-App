<!-- A placeholder for a beautiful banner image. You can create one easily on canva.com -->

<div align="center">
<img src="https://www.google.com/search?q=https://placehold.co/1200x300/6366f1/FFFFFF%3Ftext%3DJournalApp%2520API%26font%3Draleway" alt="JournalApp API Banner">
</div>

<h1 align="center">JournalApp API 📝✨</h1>

<div align="center">
A powerful and secure backend API for a modern journaling application, built with Spring Boot, MongoDB, and Kafka.
</div>

<div align="center">
<!-- Professional Badges -->
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Java-17-orange" alt="Java 17">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Spring%2520Boot-3.x-brightgreen" alt="Spring Boot 3.x">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Database-MongoDB-blue" alt="MongoDB">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Messaging-Kafka-black" alt="Kafka">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/License-MIT-blueviolet" alt="License MIT">
</div>

🚀 Overview
JournalApp API is a complete, production-ready backend designed to power a feature-rich journaling experience. It provides secure user authentication, full CRUD operations for journal entries, a public feed for community interaction, and an intelligent weekly summary system that analyzes user sentiment and mood.

The entire system is built on a modern, scalable architecture, featuring a decoupled, asynchronous notification pipeline powered by Apache Kafka.

✨ Key Features
🔐 Secure Authentication: JWT-based authentication and authorization with distinct roles for USER and ADMIN.

✍️ Full Journal Management: Complete GET, POST, PUT, PATCH, and DELETE operations for personal journal entries.

🏷️ Powerful Tagging System: Users can add tags to their entries and filter their entire journal history by any tag.

🌐 Public Community Feed: Users can choose to make entries public, allowing for community discovery, commenting, and rating.

🧠 Intelligent Weekly Summaries:

An automated background job runs weekly to analyze user activity.

It calculates the user's average mood and dominant mood.

It performs rule-based Sentiment Analysis on the journal text to determine the emotional tone.

📨 Asynchronous Notifications: A decoupled system using Kafka to produce and consume weekly summary reports, which are then formatted into personalized, empathetic emails.

👮‍♂️ Admin Panel: Secure endpoints for administrators to manage users (delete, ban, promote) and moderate content.

📖 Interactive API Documentation: Full API documentation powered by Swagger UI.

🛠️ Tech Stack
Backend: Java 17, Spring Boot 3

Database: MongoDB

Security: Spring Security, JSON Web Tokens (JWT)

Messaging: Apache Kafka

Build Tool: Maven

API Docs: SpringDoc (OpenAPI 3 / Swagger UI)

🏁 Getting Started
Follow these instructions to get the project up and running on your local machine.

Prerequisites
Java JDK 17 or later

Apache Maven

MongoDB (running locally or a cloud instance like MongoDB Atlas)

An active Kafka instance (e.g., from Confluent Cloud or a local setup)

An SMTP server or service (like SendGrid) for sending emails.

1. Clone the Repository
git clone [https://github.com/your-username/journal-app-api.git](https://github.com/your-username/journal-app-api.git)
cd journal-app-api

2. Configure the Application
The heart of the application's configuration is in src/main/resources/application.properties.

Create the file: If it doesn't exist, create it.

Add your secrets: Copy the contents of application.properties.example (if you have one) or use the template below and fill in your own credentials for MongoDB, Kafka, JWT, and your email service.

# src/main/resources/application.properties

# MongoDB Configuration
spring.data.mongodb.uri=<YOUR_MONGODB_CONNECTION_STRING>

# JWT Secret (use a long, random string)
jwt.secret=<YOUR_SUPER_SECRET_JWT_KEY>
jwt.expiration.ms=86400000

# Kafka Configuration
spring.kafka.bootstrap-servers=<YOUR_KAFKA_BOOTSTRAP_SERVER>
# ... other Kafka properties (SASL, etc.)

# Email (SMTP) Configuration
spring.mail.host=<YOUR_SMTP_HOST>
spring.mail.port=<YOUR_SMTP_PORT>
spring.mail.username=<YOUR_SMTP_USERNAME>
spring.mail.password=<YOUR_SMTP_PASSWORD>

3. Build and Run
Use Maven to build and run the application.

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
