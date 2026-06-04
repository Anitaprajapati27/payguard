# PayGuard 💳

A real-time payment processing backend with ML-based fraud detection.

## Tech Stack
- Java 21 + Spring Boot 4
- Spring Security + JWT
- MySQL + Spring Data JPA
- (Coming) Redis, Kafka, Python ML

## Features
- User registration & login with JWT auth
- BCrypt password hashing
- Digital wallet with balance management
- (Coming) ACID-compliant transfers
- (Coming) Rule-based + ML fraud detection

## API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login & get JWT token |
| GET | /api/wallet/balance | Get wallet balance |
| POST | /api/wallet/add-money | Add money to wallet |

## Setup
1. Clone the repo
2. Create MySQL database: `CREATE DATABASE payguard_db;`
3. Update `application.properties` with your DB password
4. Run: `./mvnw spring-boot:run`