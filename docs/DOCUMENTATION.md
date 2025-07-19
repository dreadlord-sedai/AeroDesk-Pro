# AeroDesk Pro - Comprehensive Documentation

## Table of Contents
1. [Application Overview](#application-overview)
2. [System Architecture](#system-architecture)
3. [Application Flow](#application-flow)
4. [Core Components](#core-components)
5. [Database Design](#database-design)
6. [API Integrations](#api-integrations)
7. [User Interface Design](#user-interface-design)
8. [Security & Authentication](#security--authentication)
9. [Error Handling & Logging](#error-handling--logging)
10. [Configuration Management](#configuration-management)
11. [Deployment & Setup](#deployment--setup)
12. [Troubleshooting](#troubleshooting)

---

## Application Overview

### What is AeroDesk Pro?
AeroDesk Pro is a comprehensive airport management system built in Java that provides real-time monitoring and management of airport operations. It integrates multiple services including flight scheduling, passenger check-in, baggage handling, gate management, and live weather/flight data.

### Key Features
- **Flight Management**: Complete flight scheduling and status tracking
- **Passenger Services**: Check-in processing and passenger management
- **Baggage Operations**: Baggage tracking and handling
- **Gate Management**: Real-time gate assignment and monitoring
- **Live Dashboard**: Real-time KPI monitoring and operational metrics
- **Weather Integration**: Live weather data for airports
- **Flight Tracking**: Real-time flight status via AviationStack API
- **Reporting System**: Comprehensive reporting and logging
- **User Management**: Role-based access control

### Technology Stack
- **Language**: Java SE 21
- **UI Framework**: Java Swing with FlatLaf theming
- **Database**: MySQL with JDBC
- **APIs**: AviationStack (flights), OpenWeatherMap (weather)
- **Architecture**: MVC pattern with DAO design
- **Build Tool**: Maven/Gradle compatible
- **Logging**: Custom FileLogger implementation

---

## System Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    AeroDesk Pro System                      │
├─────────────────────────────────────────────────────────────┤
│  Presentation Layer (UI)                                    │
│  ├── LoginFrame                                            │
│  ├── MainMenuFrame                                         │
│  ├── DashboardFrame                                        │
│  ├── FlightSchedulingFrame                                 │
│  ├── CheckInFrame                                          │
│  ├── BaggageFrame                                          │
│  ├── GateManagementFrame                                   │
│  ├── FlightStatusFrame                                     │
│  ├── AviationStackFrame                                    │
│  ├── ReportsFrame                                          │
│  └── Various Panels                                        │
├─────────────────────────────────────────────────────────────┤
│  Business Logic Layer (Service)                            │
│  ├── AuthenticationService                                 │
│  ├── FlightService                                         │
│  ├── BookingService                                        │
│  ├── BaggageService                                        │
│  ├── GateService                                           │
│  ├── AviationStackService                                  │
│  ├── WeatherService                                        │
│  └── DashboardMetrics                                      │
├─────────────────────────────────────────────────────────────┤
│  Data Access Layer (DAO)                                   │
│  ├── UserDAO                                               │
│  ├── FlightDAO                                             │
│  ├── BookingDAO                                            │
│  ├── BaggageDAO                                            │
│  └── GateDAO                                               │
├─────────────────────────────────────────────────────────────┤
│  Data Layer (Database)                                     │
│  ├── MySQL Database                                        │
│  └── Configuration Files                                   │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used
1. **MVC (Model-View-Controller)**: Separation of concerns
2. **DAO (Data Access Object)**: Database abstraction
3. **Singleton**: Service classes (FileLogger, ConfigManager)
4. **Factory**: UI component creation
5. **Observer**: Real-time updates
6. **Strategy**: Different authentication methods

---

## Application Flow

### 1. Application Startup Flow
```
1. Main.java execution
   ↓
2. Database connection initialization
   ↓
3. Configuration loading (ConfigManager)
   ↓
4. Logging system initialization (FileLogger)
   ↓
5. LoginFrame display
   ↓
6. User authentication
   ↓
7. MainMenuFrame (if successful)
```

### 2. User Authentication Flow
```
1. User enters credentials
   ↓
2. Input validation
   ↓
3. Database query (UserDAO)
   ↓
4. Password verification
   ↓
5. Role determination
   ↓
6. Session creation
   ↓
7. MainMenuFrame initialization
```

### 3. Main Menu Navigation Flow
```
MainMenuFrame
   ↓
├── Flight Scheduling → FlightSchedulingFrame
├── Passenger Check-In → CheckInFrame
├── Baggage Handling → BaggageFrame
├── Gate Management → GateManagementFrame
├── Flight Status → FlightStatusFrame
├── Aviation Stack API → AviationStackFrame
├── Reports & Logs → ReportsFrame
├── Live Dashboard → DashboardFrame
└── Logout → LoginFrame
```

### 4. Data Flow in Operations
```
User Action (UI)
   ↓
Event Handler
   ↓
Service Layer (Business Logic)
   ↓
DAO Layer (Data Access)
   ↓
Database
   ↓
Response Processing
   ↓
UI Update
```

---

## Core Components

### 1. Model Classes (Data Entities)

#### User.java
```java
// Represents system users
- userId: int
- username: String
- password: String (hashed)
- role: String (ADMIN, OPERATOR, VIEWER)
- email: String
- createdAt: LocalDateTime
```

#### Flight.java
```java
// Represents flight information
- flightId: int
- flightNo: String
- origin: String (airport code)
- destination: String (airport code)
- departTime: LocalDateTime
- arriveTime: LocalDateTime
- aircraftType: String
- status: FlightStatus (SCHEDULED, ON_TIME, DELAYED, CANCELLED)
```

#### Booking.java
```java
// Represents passenger bookings
- bookingId: int
- passengerName: String
- flightNo: String
- seatNumber: String
- checkInTime: LocalDateTime
- status: BookingStatus
```

#### Baggage.java
```java
// Represents baggage information
- baggageId: int
- bookingId: int
- weight: double
- type: BaggageType
- status: BaggageStatus
- createdAt: LocalDateTime
```

#### GateAssignment.java
```java
// Represents gate assignments
- assignmentId: int
- flightNo: String
- gateNumber: String
- departureTime: LocalDateTime
- status: GateStatus
```

### 2. Service Layer (Business Logic)

#### AuthenticationService
**Purpose**: Handles user authentication and session management
**Key Methods**:
- `authenticateUser(String username, String password)`: Validates credentials
- `createUser(String username, String password, String role)`: Creates new users
- `validateSession(String sessionId)`: Validates active sessions

**Flow**:
```
1. Receive credentials
2. Hash password (if needed)
3. Query database via UserDAO
4. Compare passwords
5. Generate session token
6. Return authentication result
```

#### FlightService
**Purpose**: Manages flight operations and scheduling
**Key Methods**:
- `scheduleFlight(Flight flight)`: Creates new flight
- `updateFlightStatus(String flightNo, FlightStatus status)`: Updates flight status
- `getFlightsByDate(LocalDate date)`: Retrieves flights for specific date
- `searchFlights(String origin, String destination)`: Searches flights by route

**Flow**:
```
1. Validate flight data
2. Check for conflicts
3. Update database via FlightDAO
4. Trigger notifications
5. Update related systems
```

#### AviationStackService
**Purpose**: Integrates with AviationStack API for real-time flight data
**Key Methods**:
- `getFlightsByAirport(String airportCode, String type)`: Gets flights for airport
- `getFlightInfo(String flightNumber)`: Gets specific flight information
- `getLiveFlightTracking(String flightNumber)`: Real-time flight tracking

**API Integration Flow**:
```
1. Prepare API request
2. Send HTTP request to AviationStack
3. Parse JSON response
4. Convert to internal FlightInfo objects
5. Cache results (if applicable)
6. Return processed data
```

#### WeatherService
**Purpose**: Integrates with OpenWeatherMap API for weather data
**Key Methods**:
- `getWeatherData(double lat, double lon, String airportCode)`: Gets weather for location
- `getMajorAirportsWeather()`: Gets weather for major airports
- `clearCache()`: Clears weather cache

**Weather Data Flow**:
```
1. Check cache for existing data
2. If cache expired, make API call
3. Parse weather response
4. Extract temperature, humidity, wind, description
5. Generate weather icons
6. Cache results
7. Return Weather object
```

#### DashboardMetrics
**Purpose**: Collects and manages real-time KPI data
**Key Methods**:
- `updateAllMetrics()`: Updates all KPI metrics
- `getAllKPIs()`: Returns all KPI data
- `getKPISummary()`: Returns formatted KPI summary

**Metrics Collection Flow**:
```
1. Query database for current data
2. Calculate metrics (flights, check-ins, baggage, gates)
3. Determine status based on thresholds
4. Update KPI objects
5. Trigger UI updates
6. Log metric changes
```

### 3. Data Access Layer (DAO)

#### UserDAO
**Purpose**: Database operations for user management
**Key Methods**:
- `getUserByUsername(String username)`: Retrieves user by username
- `createUser(User user)`: Creates new user
- `updateUser(User user)`: Updates user information
- `deleteUser(int userId)`: Deletes user

#### FlightDAO
**Purpose**: Database operations for flight management
**Key Methods**:
- `getAllFlights()`: Retrieves all flights
- `getFlightsByDate(LocalDate date)`: Gets flights for specific date
- `createFlight(Flight flight)`: Creates new flight
- `updateFlight(Flight flight)`: Updates flight information

#### BookingDAO
**Purpose**: Database operations for booking management
**Key Methods**:
- `getAllBookings()`: Retrieves all bookings
- `getBookingsByFlight(String flightNo)`: Gets bookings for flight
- `createBooking(Booking booking)`: Creates new booking
- `updateBooking(Booking booking)`: Updates booking

### 4. User Interface Components

#### MainMenuFrame
**Purpose**: Main navigation hub for the application
**Features**:
- Professional dashboard-like design
- Quick stats panel showing live metrics
- Enhanced menu grid with descriptions
- Real-time system status display

**Layout Structure**:
```
┌─────────────────────────────────────────────────────────────┐
│                    Header Panel                             │
│  [Logo] AeroDesk Pro    [User Info] [Time] [Status]        │
├─────────────────────────────────────────────────────────────┤
│                    Quick Stats Panel                        │
│  [Active Flights] [Check-ins] [Gates] [System Status]      │
├─────────────────────────────────────────────────────────────┤
│                    Menu Grid (3x3)                          │
│  [Flight Scheduling] [Check-In] [Baggage]                  │
│  [Gate Management] [Flight Status] [Aviation Stack]        │
│  [Reports] [Dashboard] [Empty]                              │
├─────────────────────────────────────────────────────────────┤
│                    Footer Panel                             │
│  [Copyright]                                    [Logout]    │
└─────────────────────────────────────────────────────────────┘
```

#### DashboardFrame
**Purpose**: Comprehensive monitoring interface
**Features**:
- Tabbed interface (Live KPIs, Flight Map)
- Real-time data visualization
- Export capabilities
- Interactive charts and maps

**Tab Structure**:
```
┌─────────────────────────────────────────────────────────────┐
│  [Live KPIs] [Flight Map]                    [Refresh] [Export] │
├─────────────────────────────────────────────────────────────┤
│                    Tab Content                               │
│  - KPI Cards with live metrics                              │
│  - Interactive flight map                                   │
│  - Weather overlays                                         │
│  - Export functionality                                     │
└─────────────────────────────────────────────────────────────┘
```

#### AviationStackFrame
**Purpose**: Aviation API integration interface
**Features**:
- Flight tracking and search
- Airport information lookup
- Real-time flight status
- API usage monitoring

**Tab Structure**:
```
┌─────────────────────────────────────────────────────────────┐
│  [Flight Tracking] [Airport Info] [Route Search] [API Mgmt] │
├─────────────────────────────────────────────────────────────┤
│                    Tab Content                               │
│  - Live flight tracking                                     │
│  - Airport information display                              │
│  - Route search functionality                               │
│  - API usage statistics                                     │
└─────────────────────────────────────────────────────────────┘
```

---

## Database Design

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Flights Table
```sql
CREATE TABLE flights (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_no VARCHAR(20) UNIQUE NOT NULL,
    origin VARCHAR(10) NOT NULL,
    destination VARCHAR(10) NOT NULL,
    depart_time DATETIME NOT NULL,
    arrive_time DATETIME NOT NULL,
    aircraft_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Bookings Table
```sql
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    passenger_name VARCHAR(100) NOT NULL,
    flight_no VARCHAR(20) NOT NULL,
    seat_number VARCHAR(10),
    check_in_time DATETIME,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_no) REFERENCES flights(flight_no)
);
```

#### Baggage Table
```sql
CREATE TABLE baggage (
    baggage_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    weight DECIMAL(5,2),
    type VARCHAR(20),
    status VARCHAR(20) DEFAULT 'CHECKED_IN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
```

#### Gate_Assignments Table
```sql
CREATE TABLE gate_assignments (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_no VARCHAR(20) NOT NULL,
    gate_number VARCHAR(10) NOT NULL,
    departure_time DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'ASSIGNED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_no) REFERENCES flights(flight_no)
);
```

### Database Relationships
```
Users (1) ←→ (Many) Flights (created_by)
Flights (1) ←→ (Many) Bookings
Bookings (1) ←→ (Many) Baggage
Flights (1) ←→ (Many) Gate_Assignments
```

---

## API Integrations

### 1. AviationStack API Integration

#### Purpose
Provides real-time flight data including flight status, tracking, and airport information.

#### API Endpoints Used
- **Flight Information**: `/flights` - Get flight details
- **Airport Information**: `/airports` - Get airport details
- **Airline Information**: `/airlines` - Get airline details

#### Integration Flow
```
1. API Key Configuration
   ↓
2. Request Preparation
   ↓
3. HTTP Request Execution
   ↓
4. JSON Response Parsing
   ↓
5. Data Transformation
   ↓
6. Caching (if applicable)
   ↓
7. Return Processed Data
```

#### Error Handling
- **API Unavailable**: Fallback to mock data
- **Rate Limiting**: Implement request throttling
- **Invalid Responses**: Graceful error handling with user feedback

### 2. OpenWeatherMap API Integration

#### Purpose
Provides real-time weather data for airports and flight planning.

#### API Endpoints Used
- **Current Weather**: `/weather` - Get current weather conditions
- **Weather by Coordinates**: `/weather?lat={lat}&lon={lon}`

#### Weather Data Processing
```
1. Airport Coordinates Lookup
   ↓
2. Weather API Request
   ↓
3. Response Parsing
   ↓
4. Weather Icon Generation
   ↓
5. Data Caching (10 minutes)
   ↓
6. Return Weather Object
```

#### Cached Data Structure
```java
Map<String, Weather> weatherCache
Map<String, LocalDateTime> cacheTimestamps
```

---

## User Interface Design

### Design Principles
1. **Consistency**: Uniform design language across all screens
2. **Usability**: Intuitive navigation and clear information hierarchy
3. **Responsiveness**: Adaptable to different screen sizes
4. **Accessibility**: Clear contrast and readable fonts
5. **Professional Appearance**: Modern, clean design suitable for business use

### Color Scheme
```java
PRIMARY_BLUE: #1976D2      // Main brand color
SECONDARY_BLUE: #42A5F5    // Secondary elements
SUCCESS_GREEN: #4CAF50     // Success states
WARNING_AMBER: #FFC107     // Warning states
ERROR_RED: #F44336         // Error states
DARK_GRAY: #212121         // Text and borders
LIGHT_GRAY: #F5F5F5        // Backgrounds
WHITE: #FFFFFF             // Primary background
```

### Typography
```java
TITLE_FONT: Segoe UI, Bold, 28px
SUBHEADER_FONT: Segoe UI, Bold, 16px
BODY_FONT: Segoe UI, Regular, 12px
```

### Component Styling
```java
// Button Styling
- Background: Theme colors
- Border: None (flat design)
- Hover Effects: Brightened background + border
- Focus: No focus painting
- Cursor: Hand cursor

// Panel Styling
- Background: Light gray or white
- Border: Subtle borders with theme colors
- Padding: Consistent spacing (15-20px)
- Margins: Proper component separation
```

---

## Security & Authentication

### Authentication System
1. **User Registration**: Admin-only user creation
2. **Password Hashing**: Secure password storage
3. **Session Management**: Session-based authentication
4. **Role-Based Access**: Different permission levels

### Security Features
- **Input Validation**: All user inputs validated
- **SQL Injection Prevention**: Prepared statements
- **Password Security**: Hashed password storage
- **Session Security**: Secure session management

### User Roles
1. **ADMIN**: Full system access
2. **OPERATOR**: Operational functions access
3. **VIEWER**: Read-only access

---

## Error Handling & Logging

### Logging System (FileLogger)
```java
// Log Levels
- INFO: General information
- WARNING: Potential issues
- ERROR: Error conditions
- DEBUG: Debug information

// Log Format
[Timestamp] [Level] [Class] - Message
```

### Error Handling Strategy
1. **User-Friendly Messages**: Clear error messages for users
2. **Detailed Logging**: Comprehensive error logging
3. **Graceful Degradation**: System continues with reduced functionality
4. **Recovery Mechanisms**: Automatic retry and fallback options

### Exception Types
- **DatabaseException**: Database operation failures
- **APIException**: External API failures
- **ValidationException**: Input validation failures
- **AuthenticationException**: Authentication failures

---

## Configuration Management

### ConfigManager
**Purpose**: Centralized configuration management
**Configuration Sources**:
- Properties files
- Environment variables
- Default values

### Configuration Properties
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/aerodesk
db.username=aerodesk_user
db.password=aerodesk_pass

# API Configuration
aviationstack.api.key=your_api_key
openweathermap.api.key=your_api_key

# Application Configuration
app.name=AeroDesk Pro
app.version=2.0.0
app.log.level=INFO
```

### Configuration Loading Flow
```
1. Load default properties
2. Load configuration file
3. Override with environment variables
4. Validate configuration
5. Initialize components
```

---

## Deployment & Setup

### System Requirements
- **Java**: JDK 21 or higher
- **Database**: MySQL 8.0 or higher
- **Memory**: Minimum 2GB RAM
- **Storage**: 500MB free space
- **Network**: Internet connection for API access

### Installation Steps
1. **Database Setup**:
   ```sql
   CREATE DATABASE aerodesk;
   CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON aerodesk.* TO 'aerodesk_user'@'localhost';
   ```

2. **Application Setup**:
   ```bash
   # Compile application
   javac -cp "lib/*;src" src/aerodesk/Main.java
   
   # Run application
   java -cp "lib/*;src" aerodesk.Main
   ```

3. **Configuration**:
   - Update `config.properties` with database credentials
   - Add API keys for external services
   - Configure logging settings

### Build Process
```bash
# Compile all source files
javac -cp "lib/*;src" src/aerodesk/**/*.java

# Create JAR file (optional)
jar cvf AeroDesk.jar -C src .

# Run application
java -cp "lib/*;src" aerodesk.Main
```

---

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
**Symptoms**: "Database connection failed" error
**Solutions**:
- Verify database server is running
- Check database credentials in config
- Ensure database exists and is accessible
- Check network connectivity

#### 2. API Integration Issues
**Symptoms**: "API unavailable" or no data displayed
**Solutions**:
- Verify API keys are valid
- Check internet connectivity
- Review API rate limits
- Check API service status

#### 3. UI Display Issues
**Symptoms**: Components not displaying correctly
**Solutions**:
- Verify Java version compatibility
- Check system display settings
- Ensure sufficient memory allocation
- Review theme configuration

#### 4. Performance Issues
**Symptoms**: Slow response times
**Solutions**:
- Increase JVM memory allocation
- Optimize database queries
- Review caching strategies
- Check system resources

### Debug Mode
Enable debug logging by setting:
```properties
app.log.level=DEBUG
```

### Support Information
- **Log Files**: Check `logs/aerodesk.log` for detailed information
- **Configuration**: Review `config.properties` for settings
- **Database**: Check MySQL error logs for database issues

---

## Future Enhancements

### Planned Features
1. **Mobile Application**: iOS/Android companion app
2. **Advanced Analytics**: Machine learning for predictive analytics
3. **Multi-language Support**: Internationalization
4. **Cloud Deployment**: AWS/Azure cloud hosting
5. **Real-time Notifications**: Push notifications for critical events
6. **Advanced Reporting**: Custom report builder
7. **Integration APIs**: REST API for external integrations

### Technical Improvements
1. **Microservices Architecture**: Service decomposition
2. **Containerization**: Docker deployment
3. **CI/CD Pipeline**: Automated testing and deployment
4. **Performance Optimization**: Caching and optimization
5. **Security Enhancements**: Advanced security features

---

## Conclusion

AeroDesk Pro is a comprehensive airport management system that provides real-time monitoring, efficient operations management, and professional user experience. The system's modular architecture, robust error handling, and extensive integration capabilities make it suitable for modern airport operations.

The application successfully integrates multiple data sources, provides intuitive user interfaces, and maintains high performance standards while ensuring data security and system reliability.

For technical support or feature requests, please refer to the project documentation or contact the development team. 