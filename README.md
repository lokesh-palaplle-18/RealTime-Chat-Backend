# Real-time Chat Backend

A full-featured real-time chat backend built with **Spring Boot 3**, **WebSocket (STOMP)**, **JWT Authentication**, and **Spring Data JPA**. Supports chat rooms, real-time messaging, message history with pagination, and secure user authentication.

---

## What This Project Does

- Users **register and login** — receive a JWT token
- Users **create and join chat rooms**
- Messages are sent and received in **real-time via WebSocket (STOMP)**
- REST fallback available for sending messages via HTTP
- Full **message history** with pagination
- All endpoints (except auth and WebSocket handshake) are **JWT protected**

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Real-time | WebSocket + STOMP + SockJS |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | H2 (dev) / MySQL (production) |
| ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Project Structure

```
real-time-chat/
├── src/
│   ├── main/
│   │   ├── java/com/chat/
│   │   │   ├── ChatApplication.java               # Entry point
│   │   │   ├── config/
│   │   │   │   ├── WebSocketConfig.java            # STOMP broker + endpoints
│   │   │   │   ├── SecurityConfig.java             # JWT filter chain
│   │   │   │   ├── JwtFilter.java                  # Validates JWT on every request
│   │   │   │   └── GlobalExceptionHandler.java     # Centralized error handling
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java             # Register + Login
│   │   │   │   ├── RoomController.java             # CRUD for rooms
│   │   │   │   └── ChatController.java             # WebSocket + REST messaging
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                # Registration + login logic
│   │   │   │   ├── RoomService.java                # Room business logic
│   │   │   │   ├── ChatService.java                # Message send + history
│   │   │   │   └── UserDetailsServiceImpl.java     # Spring Security user loader
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoomRepository.java
│   │   │   │   └── MessageRepository.java          # Paginated message queries
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Room.java
│   │   │   │   └── Message.java
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest / LoginRequest / AuthResponse
│   │   │   │   ├── RoomRequest / RoomResponse
│   │   │   │   ├── MessageRequest / MessageResponse
│   │   │   │   └── ErrorResponse.java              # Consistent error shape
│   │   │   ├── mapper/
│   │   │   │   ├── MessageMapper.java              # Message entity ↔ DTO
│   │   │   │   └── RoomMapper.java                 # Room entity ↔ DTO
│   │   │   ├── exception/
│   │   │   │   ├── UserNotFoundException.java
│   │   │   │   ├── RoomNotFoundException.java
│   │   │   │   └── UserAlreadyExistsException.java
│   │   │   └── util/
│   │   │       └── JwtUtil.java                    # Token generate, validate, extract
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/chat/ChatApplicationTests.java
└── pom.xml
```

---

## API Endpoints

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | None | Register a new user |
| POST | `/api/auth/login` | None | Login, receive JWT |

**Register:**
```json
POST /api/auth/register
{
  "username": "lokesh",
  "email": "lokesh@example.com",
  "password": "secret123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "lokesh",
  "email": "lokesh@example.com"
}
```

---

### Rooms

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/rooms` | JWT | Create a room |
| GET | `/api/rooms` | JWT | List all rooms |
| GET | `/api/rooms/{id}` | JWT | Get room details |
| DELETE | `/api/rooms/{id}` | JWT | Delete a room |

---

### Messages (REST)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/rooms/{id}/messages` | JWT | Send a message |
| GET | `/api/rooms/{id}/messages` | JWT | Get message history (paginated) |

---

## WebSocket — Real-time Messaging

### Connect
```
WebSocket URL: ws://localhost:8080/ws
Protocol: STOMP over SockJS
```

### Subscribe to receive messages in a room
```
SUBSCRIBE /topic/room/{roomId}
```

### Send a message to a room
```
SEND /app/chat/{roomId}
Body: { "content": "Hello everyone!" }
```

### Flow
```
Client → SEND /app/chat/1 → ChatController → saves to DB → broadcasts to /topic/room/1 → all subscribers receive it
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps
```bash
cd real-time-chat
mvn clean install
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

---

## Test with curl

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"lokesh","email":"lokesh@example.com","password":"pass123"}'

# Login (save the token)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"lokesh","password":"pass123"}' | jq -r '.token')

# Create a room
curl -X POST http://localhost:8080/api/rooms \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"general","description":"General chat"}'

# Send a message (REST)
curl -X POST http://localhost:8080/api/rooms/1/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello World!"}'

# Get message history
curl http://localhost:8080/api/rooms/1/messages?page=0&size=20 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Error Response Format

All errors return a consistent structure:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Room not found with id: 99",
  "timestamp": "2024-01-01T10:00:00"
}
```

| Status | Scenario |
|---|---|
| 400 | Validation failure, malformed JSON, missing/wrong-type parameter |
| 401 | Wrong credentials, disabled or locked account |
| 403 | Valid JWT but insufficient permissions |
| 404 | User or Room not found, unknown endpoint |
| 405 | Wrong HTTP method for endpoint |
| 409 | Username/email already taken, room name duplicate, DB constraint violation |
| 500 | Unexpected server error |

---

## Switch to MySQL (Production)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chatdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## Key Concepts You Learn

- **WebSocket + STOMP** — how real-time bidirectional communication works
- **JWT Authentication** — stateless auth with token validation on every request
- **Spring Security filter chain** — how requests are intercepted and authenticated
- **Mapper pattern** — `MessageMapper` and `RoomMapper` own all conversions, zero duplication
- **Custom exception hierarchy** — typed exceptions mapped to correct HTTP statuses
- **Pagination** — `PageRequest` for efficient message history retrieval
- **SimpMessagingTemplate** — broadcasting messages to WebSocket subscribers from REST
- **UserDetailsService** — how Spring Security loads users from the database

---

## Run Tests

```bash
mvn test
```
