
# PayGuard 💳

> Real-Time Payment Processing Engine with ML-Based Fraud Detection

A production-grade fintech backend built with Spring Boot, featuring ACID-compliant transactions, dual-layer fraud detection (rule-based + ML), Redis caching, and a Python ML microservice.

---

## 🏗️ Architecture
Client

↓

Spring Boot (Port 8080)

↓

JWT Security Filter

↓

┌─────────────────────────────────┐

│          Wallet Service          │

│  ┌─────────────────────────┐    │

│  │   Balance Check          │    │

│  │   Rule-Based Fraud Engine│    │

│  │   ML Fraud Service ──────│────│──→ Flask ML (Port 5000)

│  │   Transaction Engine     │    │

│  └─────────────────────────┘    │

└─────────────────────────────────┘

↓                    ↓

MySQL DB           Redis Cache

---

## ✨ Features

### 💰 Payment Engine
- Wallet management with balance in paise (no floating point errors)
- ACID-compliant transfers using Spring @Transactional
- Idempotency keys to prevent duplicate charges
- Transaction history with pagination

### 🔐 Security
- JWT-based authentication
- BCrypt password hashing
- Role-based access control
- Spring Security filter chain

### 🚨 Dual-Layer Fraud Detection
**Layer 1 — Rule Engine (Strategy Pattern):**
- Large Amount Rule (> ₹50,000)
- Rapid Fire Rule (5+ transactions in 5 mins)
- Odd Hours Rule (1AM-4AM + large amount)
- High Balance Percentage Rule (90%+ of balance)

**Layer 2 — ML Model (Isolation Forest):**
- Anomaly detection using Python scikit-learn
- 4 features: amount, hour, recent txn count, balance %
- Fraud probability score (0.0 to 1.0)
- Blocks if score > 0.75

### ⚡ Performance
- Redis caching on wallet balance (5 min TTL)
- Cache invalidation on balance change

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21 + Spring Boot 4 |
| Security | Spring Security + JWT |
| Database | MySQL + Spring Data JPA |
| Cache | Redis + Spring Cache |
| ML Service | Python + Flask + scikit-learn |
| ML Algorithm | Isolation Forest (anomaly detection) |

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login & get JWT token |

### Wallet
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/wallet/balance | Get wallet balance |
| POST | /api/wallet/add-money | Add money to wallet |
| POST | /api/wallet/transfer | Transfer to another user |
| GET | /api/wallet/transactions | Transaction history |
| GET | /api/wallet/fraud-alerts | View fraud alerts |

---

## 🚀 Setup

### Prerequisites
- Java 21
- MySQL 8
- Redis
- Python 3.8+

### Spring Boot
```bash
# Create database
mysql -u root -p
CREATE DATABASE payguard_db;

# Update application.properties with your DB password

# Run
./mvnw spring-boot:run
```

### Flask ML Service
```bash
cd payguard-ml
pip install flask scikit-learn pandas numpy joblib
python train_model.py
python app.py
```

### API Docs
http://localhost:8080/swagger-ui.html

---

## 🎯 Key Design Decisions

1. **Paise not Rupees** — Stored as Long to avoid floating point errors
2. **Strategy Pattern** — Each fraud rule is isolated and independently testable
3. **REQUIRES_NEW propagation** — Fraud alerts persist even when transaction rolls back
4. **Graceful degradation** — If ML service is down, rule-based detection continues
5. **Cache invalidation** — Balance cache cleared on every transfer

## 👨‍💻 Author
Anita Prajapati — [GitHub](https://github.com/Anitaprajapati27)