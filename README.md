# 🏨 Hotel Booking Platform – Backend

A fully functional **role-based Hotel Booking Platform backend** built using **Spring Boot**, implementing booking lifecycle management, monetization, monitoring dashboards, dispute handling, and JWT-based authentication.

This project demonstrates clean layered architecture, business rule enforcement, object-level authorization, and scalable backend design.

---

## 🚀 Project Overview

The system supports four roles:

- 👤 **USER**
- 🏨 **HOTEL OWNER**
- 🛠 **ADMIN**
- 🌍 **PUBLIC (Unauthenticated)**

It handles:

- Hotel & Room Management
- Booking Lifecycle (Book → Cancel → Checkout → Review)
- Platform Commission & Revenue Tracking
- Monitoring & Dispute Handling
- JWT-based Authentication
- Role-Based Authorization
- Ownership Validation (Object-Level Security)

---

## 🏗 Architecture

The application follows a clean layered architecture:

```
Controller Layer
        ↓
Service Layer
        ↓
Repository Layer
        ↓
Database (MySQL)
```

### 🔐 Security Flow

```
JWT Authentication
        ↓
Role-Based Authorization
        ↓
Ownership Validation
```

---

## 📦 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok

---

## 🔐 Security Implementation

### ✅ Authentication
- JWT-based login
- Stateless session management

### ✅ Roles

```
USER
HOTEL
ADMIN
```

### ✅ Authorization Rules

| Endpoint | Access |
|-----------|--------|
| `/api/v1/public/**` | Public |
| `/api/v1/users/**` | USER |
| `/api/v1/owner/**` | HOTEL |
| `/api/v1/admin/**` | ADMIN |

### ✅ Ownership Validation

- Users can access **only their own bookings**
- Hotel owners can manage **only their own hotels**
- Admin has unrestricted access
- Resource-level validation is implemented in the **service layer**

---

## 👤 User Features

- Register & Login
- View hotels and rooms
- Book room
- Cancel booking (10% deduction rule)
- Checkout booking
- Add rating & review
- View own bookings

---

## 🏨 Hotel Owner Features

- Register as hotel owner
- Create hotel
- Add rooms
- View bookings of own hotel
- Cancel booking with reason
- Checkout bookings
- Monitor hotel performance

---

## 🛠 Admin Features

- View all users
- View all hotels
- View all bookings
- View rooms (hotel-wise)
- Monitor revenue dashboard
- Handle disputes
- View commission analytics

---

## 💰 Monetization Model

### 🟢 Registration Commission
- ₹100 per room added
- Stored in `Commission` entity

### 🟢 Booking Commission
- 10% of completed booking amount
- Generated only when booking status = `COMPLETED`

### 📊 Admin Dashboard Displays
- Total completed bookings
- Total registration commission
- Total booking commission
- Total platform earnings
- Hotel-wise revenue

---

## 📌 Booking Lifecycle

```
CONFIRMED → CANCELLED
CONFIRMED → COMPLETED
```

### Rules
- Only CONFIRMED bookings can be cancelled
- Only CONFIRMED bookings can be checked out
- Cancellation applies 10% deduction
- Commission generated only for COMPLETED bookings

---

## 🗂 Project Structure

```
HotelBookingSystem
│
├── controller
├── service
├── repository
├── entity
├── dto
├── Enum
├── exception
├── mapper
└── security
```

---

## ▶️ Running the Project

1. Clone the repository
2. Configure MySQL in `application.properties`
3. Run:

```bash
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080/api/v1/
```

---

## 🧠 Key Concepts Implemented

- Clean layered architecture
- Role-based endpoint protection
- JWT authentication
- Object-level ownership validation
- Booking lifecycle management
- Commission ledger system
- Dispute management
- Admin monitoring dashboard

---

## 🚀 Future Enhancements

- Payment Integration
- Advanced Availability Engine (Date Overlap Detection)
- Refresh Token System
- Analytics & Reporting
- Caching & Performance Optimization

---

## 👨‍💻 Author

**Ashutosh Raj**  
Backend Developer | Java & Spring Boot

---