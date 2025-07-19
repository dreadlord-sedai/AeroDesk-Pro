# AeroDesk Pro - Technical Architecture Guide

## Table of Contents
1. [System Architecture Overview](#system-architecture-overview)
2. [Component Interaction Diagrams](#component-interaction-diagrams)
3. [Data Flow Architecture](#data-flow-architecture)
4. [Database Schema Details](#database-schema-details)
5. [API Integration Architecture](#api-integration-architecture)
6. [Security Architecture](#security-architecture)
7. [Performance Architecture](#performance-architecture)
8. [Deployment Architecture](#deployment-architecture)

---

## System Architecture Overview

### Layered Architecture Pattern
```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Frames    │ │   Panels    │ │ Components  │           │
│  │             │ │             │ │             │           │
│  │ • Login     │ │ • Dashboard │ │ • Tables    │           │
│  │ • MainMenu  │ │ • Map       │ │ • Forms     │           │
│  │ • Dashboard │ │ • Reports   │ │ • Charts    │           │
│  │ • Aviation  │ │ • Weather   │ │ • Buttons   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│                   BUSINESS LOGIC LAYER                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Services  │ │   Managers  │ │   Utils     │           │
│  │             │ │             │ │             │           │
│  │ • Auth      │ │ • Theme     │ │ • FileLogger│           │
│  │ • Flight    │ │ • Config    │ │ • Validator │           │
│  │ • Booking   │ │ • Database  │ │ • Helper    │           │
│  │ • Baggage   │ │ • Cache     │ │ • Converter │           │
│  │ • Weather   │ │ • Session   │ │ • Parser    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│                    DATA ACCESS LAYER                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │     DAO     │ │  Database   │ │   Cache     │           │
│  │             │ │             │ │             │           │
│  │ • UserDAO   │ │ • MySQL     │ │ • Weather   │           │
│  │ • FlightDAO │ │ • JDBC      │ │ • Flight    │           │
│  │ • BookingDAO│ │ • Connection│ │ • Session   │           │
│  │ • BaggageDAO│ │ • Pool      │ │ • Config    │           │
│  │ • GateDAO   │ │ • Transactions│ • Metrics  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│                      DATA LAYER                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Database  │ │   External  │ │   Files     │           │
│  │             │ │     APIs    │ │             │           │
│  │ • MySQL     │ │ • Aviation  │ │ • Config    │           │
│  │ • Tables    │ │ • Weather   │ │ • Logs      │           │
│  │ • Indexes   │ │ • HTTP      │ │ • Reports   │           │
│  │ • Views     │ │ • JSON      │ │ • Exports   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Implementation

#### 1. MVC Pattern
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    VIEW     │    │ CONTROLLER  │    │    MODEL    │
│             │    │             │    │             │
│ • Frames    │◄──►│ • Services  │◄──►│ • Entities  │
│ • Panels    │    │ • Event     │    │ • DAOs      │
│ • UI        │    │   Handlers  │    │ • Database  │
└─────────────┘    └─────────────┘    └─────────────┘
```

#### 2. DAO Pattern
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   SERVICE   │    │     DAO     │    │  DATABASE   │
│             │    │             │    │             │
│ • Business  │───►│ • Data      │───►│ • Tables    │
│   Logic     │    │   Access    │    │ • Queries   │
│ • Validation│    │ • CRUD      │    │ • Results   │
└─────────────┘    └─────────────┘    └─────────────┘
```

#### 3. Singleton Pattern
```java
// FileLogger Singleton
public class FileLogger {
    private static FileLogger instance;
    private FileLogger() {}
    
    public static FileLogger getInstance() {
        if (instance == null) {
            instance = new FileLogger();
        }
        return instance;
    }
}

// ConfigManager Singleton
public class ConfigManager {
    private static ConfigManager instance;
    private Properties config;
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}
```

---

## Component Interaction Diagrams

### 1. Application Startup Sequence
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Main   │    │ ConfigMgr   │    │ FileLogger  │    │ Database    │
│         │    │             │    │             │    │             │
│  start  │───►│ loadConfig  │───►│ initialize  │───►│ connect     │
│         │    │             │    │             │    │             │
│         │    │             │    │             │    │             │
│         │◄───│ configReady │◄───│ loggerReady │◄───│ connected   │
│         │    │             │    │             │    │             │
│ show    │    │             │    │             │    │             │
│ Login   │    │             │    │             │    │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### 2. User Authentication Flow
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Login   │    │ AuthService │    │   UserDAO   │    │ Database    │
│ Frame   │    │             │    │             │    │             │
│         │    │             │    │             │    │             │
│ submit  │───►│ authenticate│───►│ getUser     │───►│ SELECT      │
│ creds   │    │             │    │             │    │             │
│         │    │             │    │             │    │             │
│         │◄───│ authResult  │◄───│ userData    │◄───│ userRecord  │
│         │    │             │    │             │    │             │
│ show    │    │             │    │             │    │             │
│ MainMenu│    │             │    │             │    │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### 3. Flight Management Flow
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Flight  │    │ Flight      │    │ FlightDAO   │    │ Database    │
│ Frame   │    │ Service     │    │             │    │             │
│         │    │             │    │             │    │             │
│ create  │───►│ validate    │───►│ insert      │───►│ INSERT      │
│ flight  │    │ flight      │    │ flight      │    │             │
│         │    │             │    │             │    │             │
│         │◄───│ success     │◄───│ flightId    │◄───│ newId       │
│         │    │             │    │             │    │             │
│ update  │    │             │    │             │    │             │
│ UI      │    │             │    │             │    │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### 4. API Integration Flow
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ UI      │    │ API Service │    │ HTTP Client │    │ External    │
│         │    │             │    │             │    │ API         │
│         │    │             │    │             │    │             │
│ request │───►│ prepare     │───►│ send        │───►│ process     │
│ data    │    │ request     │    │ request     │    │ request     │
│         │    │             │    │             │    │             │
│         │◄───│ process     │◄───│ receive     │◄───│ send        │
│ display │    │ response    │    │ response    │    │ response    │
│ data    │    │             │    │             │    │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

---

## Data Flow Architecture

### 1. Real-time Data Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   External  │    │   Cache     │    │   UI        │
│     APIs    │    │   Layer     │    │   Update    │
│             │    │             │    │             │
│ • Aviation  │───►│ • Weather   │───►│ • Dashboard │
│ • Weather   │    │ • Flight    │    │ • Map       │
│             │    │ • Session   │    │ • Charts    │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Parser    │    │   Manager   │    │   Renderer  │
│             │    │             │    │             │
│ • JSON      │    │ • Cache     │    │ • Swing     │
│ • XML       │    │ • Update    │    │ • Graphics  │
│ • Response  │    │ • Expiry    │    │ • Layout    │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 2. Database Transaction Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Service   │    │   DAO       │    │ Database    │
│             │    │             │    │             │
│ 1. Begin    │───►│ 2. Prepare  │───►│ 3. Execute  │
│    TX       │    │    Query    │    │    Query    │
│             │    │             │    │             │
│ 4. Commit   │◄───│ 5. Process  │◄───│ 6. Return   │
│    TX       │    │   Result    │    │   Result    │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 3. Event-Driven Architecture
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Event     │    │   Event     │    │   Event     │
│  Source     │    │  Handler    │    │  Listener   │
│             │    │             │    │             │
│ • Button    │───►│ • Action    │───►│ • UI        │
│ • Timer     │    │ • Mouse     │    │   Update    │
│ • API       │    │ • Key       │    │ • Log       │
│ • Database  │    │ • Window    │    │ • Notify    │
└─────────────┘    └─────────────┘    └─────────────┘
```

---

## Database Schema Details

### Complete ERD (Entity Relationship Diagram)
```
┌─────────────────────────────────────────────────────────────┐
│                        USERS                                │
├─────────────────────────────────────────────────────────────┤
│ user_id (PK) │ username │ password │ role │ email │ created │
│ INT          │ VARCHAR  │ VARCHAR  │ ENUM │ VARCHAR│ TIMESTAMP│
└─────────────────────────────────────────────────────────────┘
                                │
                                │ 1:N
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                       FLIGHTS                               │
├─────────────────────────────────────────────────────────────┤
│ flight_id │ flight_no │ origin │ dest │ depart │ arrive │ status │
│ INT (PK)  │ VARCHAR   │ VARCHAR│ VARCHAR│ DATETIME│ DATETIME│ ENUM   │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ 1:N
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                      BOOKINGS                               │
├─────────────────────────────────────────────────────────────┤
│ booking_id │ passenger │ flight_no │ seat │ check_in │ status │
│ INT (PK)   │ VARCHAR   │ VARCHAR   │ VARCHAR│ DATETIME │ ENUM   │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ 1:N
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                       BAGGAGE                               │
├─────────────────────────────────────────────────────────────┤
│ baggage_id │ booking_id │ weight │ type │ status │ created │
│ INT (PK)   │ INT (FK)   │ DECIMAL│ ENUM │ ENUM   │ TIMESTAMP│
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   GATE_ASSIGNMENTS                          │
├─────────────────────────────────────────────────────────────┤
│ assignment_id │ flight_no │ gate │ departure │ status │ created │
│ INT (PK)      │ VARCHAR   │ VARCHAR│ DATETIME │ ENUM   │ TIMESTAMP│
└─────────────────────────────────────────────────────────────┘
```

### Index Strategy
```sql
-- Primary Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_flights_flight_no ON flights(flight_no);
CREATE INDEX idx_flights_depart_time ON flights(depart_time);
CREATE INDEX idx_bookings_flight_no ON bookings(flight_no);
CREATE INDEX idx_baggage_booking_id ON baggage(booking_id);
CREATE INDEX idx_gate_assignments_flight_no ON gate_assignments(flight_no);

-- Composite Indexes
CREATE INDEX idx_flights_route ON flights(origin, destination);
CREATE INDEX idx_flights_date_status ON flights(depart_time, status);
CREATE INDEX idx_bookings_passenger ON bookings(passenger_name, flight_no);
```

### Query Optimization
```sql
-- Optimized Flight Search
SELECT f.*, COUNT(b.booking_id) as passenger_count
FROM flights f
LEFT JOIN bookings b ON f.flight_no = b.flight_no
WHERE f.depart_time BETWEEN ? AND ?
  AND f.status = ?
GROUP BY f.flight_id
ORDER BY f.depart_time;

-- Optimized Dashboard Metrics
SELECT 
    COUNT(*) as total_flights,
    SUM(CASE WHEN status = 'ON_TIME' THEN 1 ELSE 0 END) as on_time_flights,
    SUM(CASE WHEN status = 'DELAYED' THEN 1 ELSE 0 END) as delayed_flights
FROM flights 
WHERE DATE(depart_time) = CURDATE();
```

---

## API Integration Architecture

### 1. AviationStack API Integration
```java
// API Configuration
public class AviationStackService {
    private static final String BASE_URL = "http://api.aviationstack.com/v1";
    private static final String API_KEY = ConfigManager.getInstance()
        .getProperty("aviationstack.api.key");
    
    // Request Builder Pattern
    private HttpRequest buildRequest(String endpoint, Map<String, String> params) {
        StringBuilder url = new StringBuilder(BASE_URL + endpoint);
        url.append("?access_key=").append(API_KEY);
        
        params.forEach((key, value) -> 
            url.append("&").append(key).append("=").append(value));
        
        return HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .header("Accept", "application/json")
            .GET()
            .build();
    }
    
    // Response Processing
    private FlightInfo parseFlightResponse(String jsonResponse) {
        // JSON parsing with error handling
        try {
            // Parse JSON and extract flight data
            return new FlightInfo(/* parsed data */);
        } catch (Exception e) {
            FileLogger.getInstance().logError("API parsing error: " + e.getMessage());
            return createMockFlightInfo();
        }
    }
}
```

### 2. OpenWeatherMap API Integration
```java
// Weather Service Architecture
public class WeatherService {
    private final Map<String, Weather> weatherCache;
    private final Map<String, LocalDateTime> cacheTimestamps;
    private static final int CACHE_DURATION_MINUTES = 10;
    
    // Caching Strategy
    public CompletableFuture<Weather> getWeatherData(double lat, double lon, String airportCode) {
        return CompletableFuture.supplyAsync(() -> {
            // Check cache first
            if (isCacheValid(airportCode)) {
                return weatherCache.get(airportCode);
            }
            
            // Fetch from API
            Weather weather = fetchFromAPI(lat, lon, airportCode);
            cacheWeatherData(airportCode, weather);
            return weather;
        });
    }
    
    // Cache Management
    private boolean isCacheValid(String airportCode) {
        LocalDateTime timestamp = cacheTimestamps.get(airportCode);
        return timestamp != null && 
               LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES)
                   .isBefore(timestamp);
    }
}
```

### 3. Error Handling Strategy
```java
// Comprehensive Error Handling
public class APIExceptionHandler {
    
    public static <T> T handleAPIRequest(Supplier<T> apiCall, 
                                       Supplier<T> fallback) {
        try {
            return apiCall.get();
        } catch (HttpTimeoutException e) {
            FileLogger.getInstance().logError("API timeout: " + e.getMessage());
            return fallback.get();
        } catch (HttpResponseException e) {
            FileLogger.getInstance().logError("API error: " + e.getStatusCode());
            return fallback.get();
        } catch (Exception e) {
            FileLogger.getInstance().logError("Unexpected API error: " + e.getMessage());
            return fallback.get();
        }
    }
}
```

---

## Security Architecture

### 1. Authentication Flow
```java
// Secure Authentication Implementation
public class AuthenticationService {
    
    public AuthenticationResult authenticateUser(String username, String password) {
        // Input validation
        if (!isValidInput(username) || !isValidInput(password)) {
            return new AuthenticationResult(false, "Invalid input");
        }
        
        try {
            // Database query with prepared statement
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                return new AuthenticationResult(false, "User not found");
            }
            
            // Password verification (hashed)
            if (verifyPassword(password, user.getPassword())) {
                String sessionToken = generateSessionToken();
                return new AuthenticationResult(true, sessionToken, user.getRole());
            } else {
                return new AuthenticationResult(false, "Invalid password");
            }
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Database error: " + e.getMessage());
            return new AuthenticationResult(false, "System error");
        }
    }
    
    // Password Hashing
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    // Session Management
    private String generateSessionToken() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }
}
```

### 2. Input Validation
```java
// Comprehensive Input Validation
public class InputValidator {
    
    public static boolean isValidUsername(String username) {
        return username != null && 
               username.matches("^[a-zA-Z0-9_]{3,20}$") &&
               !username.contains("'") && 
               !username.contains("\"");
    }
    
    public static boolean isValidFlightNumber(String flightNo) {
        return flightNo != null && 
               flightNo.matches("^[A-Z]{2,3}[0-9]{3,4}$");
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

### 3. SQL Injection Prevention
```java
// Prepared Statement Usage
public class FlightDAO {
    
    public Flight getFlightByNumber(String flightNo) throws DatabaseException {
        String sql = "SELECT * FROM flights WHERE flight_no = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, flightNo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToFlight(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving flight", e);
        }
    }
}
```

---

## Performance Architecture

### 1. Caching Strategy
```java
// Multi-level Caching
public class CacheManager {
    
    // L1 Cache (Memory)
    private final Map<String, Object> memoryCache = new ConcurrentHashMap<>();
    
    // L2 Cache (Database)
    private final Map<String, LocalDateTime> cacheTimestamps = new ConcurrentHashMap<>();
    
    public <T> T getCachedData(String key, Supplier<T> dataLoader, 
                              Duration expiry) {
        // Check memory cache first
        if (memoryCache.containsKey(key)) {
            CacheEntry entry = (CacheEntry) memoryCache.get(key);
            if (!entry.isExpired()) {
                return (T) entry.getData();
            }
        }
        
        // Load fresh data
        T data = dataLoader.get();
        memoryCache.put(key, new CacheEntry(data, expiry));
        return data;
    }
}
```

### 2. Connection Pooling
```java
// Database Connection Pool
public class DatabaseManager {
    
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_POOL_SIZE = 2;
    private final BlockingQueue<Connection> connectionPool;
    
    public DatabaseManager() {
        this.connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
    }
    
    public Connection getConnection() throws DatabaseException {
        try {
            Connection conn = connectionPool.poll(5, TimeUnit.SECONDS);
            if (conn == null || conn.isClosed()) {
                conn = createNewConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Connection timeout", e);
        }
    }
    
    public void releaseConnection(Connection conn) {
        if (conn != null && !conn.isClosed()) {
            connectionPool.offer(conn);
        }
    }
}
```

### 3. Asynchronous Processing
```java
// Background Task Management
public class BackgroundTaskManager {
    
    private final ScheduledExecutorService scheduler = 
        Executors.newScheduledThreadPool(4);
    
    public void schedulePeriodicTask(Runnable task, long initialDelay, 
                                   long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    
    public CompletableFuture<Void> executeAsync(Runnable task) {
        return CompletableFuture.runAsync(task, scheduler);
    }
    
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## Deployment Architecture

### 1. Application Packaging
```
AeroDesk-Pro/
├── lib/                          # External libraries
│   ├── mysql-connector-java.jar
│   ├── flatlaf.jar
│   └── other-dependencies.jar
├── src/                          # Source code
│   └── aerodesk/
│       ├── Main.java
│       ├── model/
│       ├── service/
│       ├── dao/
│       ├── ui/
│       └── util/
├── config/                       # Configuration
│   ├── config.properties
│   └── database.sql
├── logs/                         # Log files
│   └── aerodesk.log
├── reports/                      # Generated reports
├── AeroDesk.jar                  # Executable JAR
└── README.md
```

### 2. Runtime Environment
```bash
# JVM Configuration
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -Dfile.encoding=UTF-8 \
     -cp "lib/*:AeroDesk.jar" \
     aerodesk.Main
```

### 3. Database Setup Script
```sql
-- Database initialization
CREATE DATABASE IF NOT EXISTS aerodesk 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aerodesk;

-- Create tables with proper constraints
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'OPERATOR', 'VIEWER') NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- Insert default admin user
INSERT INTO users (username, password, role, email) 
VALUES ('admin', '$2a$10$hashed_password', 'ADMIN', 'admin@aerodesk.com');
```

### 4. Configuration Management
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/aerodesk?useSSL=false&serverTimezone=UTC
db.username=aerodesk_user
db.password=secure_password
db.pool.size=10
db.pool.timeout=30

# API Configuration
aviationstack.api.key=your_aviation_stack_key
aviationstack.api.timeout=30
openweathermap.api.key=your_weather_api_key
openweathermap.api.timeout=15

# Application Configuration
app.name=AeroDesk Pro
app.version=2.0.0
app.log.level=INFO
app.log.file=logs/aerodesk.log
app.cache.enabled=true
app.cache.duration=600

# UI Configuration
ui.theme=flatlaf
ui.language=en
ui.timezone=UTC
```

This comprehensive technical architecture provides a complete understanding of how AeroDesk Pro is structured, how components interact, and how the system handles data flow, security, and performance optimization. 