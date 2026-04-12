# Transfera


A full-stack mobile payment application inspired by Venmo and Cash App, built with React Native and Spring Boot.

## Tech Stack

**Frontend**
- React Native / Expo (TypeScript)
- Expo Router for file-based navigation
- JWT authentication via SecureStore

**Backend**
- Spring Boot 4 / Java 17
- PostgreSQL
- Spring Security + JWT
- Hibernate / JPA
- Google OAuth2

## Features

- Google OAuth and local email/password authentication
- Transfera Wallet with auto-generated wallet numbers
- Link bank accounts (checking/savings)
- Add money to wallet from linked bank accounts
- Password reset via Gmail SMTP
- Profile creation flow
- JWT token blacklisting on logout

## Architecture

The backend follows a **Command/Query pattern** — each use case is a dedicated service class implementing either `Command<I, O>` or `Query<I, O>`, keeping controllers thin and business logic isolated.

```
controller/        → thin HTTP layer, delegates to services
service/           → one class per use case (Command or Query)
domain/            → JPA entities + repositories
dto/               → request and response objects
exceptions/        → custom exceptions + global handler
security/          → JWT filter, UserDetailsService
```

## Running Locally

**Backend**
```bash
cd backend
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Fill in your PostgreSQL and JWT config
./gradlew bootRun
```

**Frontend**
```bash
cd frontend
npm install
npx expo start
```

## Testing

```bash
cd backend
./gradlew test
```

Unit and concurrency tests cover the core wallet service layer, including:
- Add money happy path and edge cases
- Exception handling for missing accounts and wallets
- Concurrent requests from the same and different users