# AeroDesk Pro - Advanced Airport Management System

A comprehensive Java SE application for managing airport operations including flight scheduling, passenger check-in, baggage handling, gate management, real-time flight status monitoring, and **Aviation Stack API integration** for live flight data.

## 🚀 Features

### ✅ Core Airport Management
- **Flight Scheduling**: Create, edit, and manage flight schedules
- **Passenger Check-In**: Streamlined passenger check-in process with boarding pass generation
- **Baggage Handling**: Track baggage from check-in to delivery with real-time simulation
- **Gate Management**: Assign and monitor gate availability with conflict detection
- **Real-Time Flight Status**: Live flight status updates with weather integration
- **Reporting & Logging**: Comprehensive reporting and system logging

### ✅ **NEW: Aviation Stack API Integration** 🛫
- **Real-time Flight Tracking**: Live position, altitude, speed, and direction data
- **Airport Information**: Complete airport details, statistics, and contact information
- **Airline Information**: Airline profiles, fleet data, and company details
- **Route Search**: Find flights between airports with comprehensive results
- **Live Flight Monitoring**: Real-time flight status with live tracking capabilities
- **Enhanced Flight Data**: Aircraft information, gate details, and weather data
- **API Data Synchronization**: Automatic sync between Aviation Stack and local database

### ✅ Technical Features
- **Multi-threaded Operations**: Background processing for real-time updates
- **Database Integration**: MySQL database with JDBC connectivity
- **Modern UI**: FlatLaf themed Swing interface
- **API Integration**: HTTP client for external data with error handling
- **Comprehensive Logging**: Detailed system logging with multiple levels
- **Configuration Management**: Flexible configuration system

## 🛠 Technology Stack

- **Language**: Java SE 21
- **UI Framework**: Java Swing with FlatLaf theming
- **Database**: MySQL 8.0+
- **Database Driver**: MySQL Connector/J 8.2.0
- **Build System**: NetBeans Ant-based build
- **Multithreading**: Java Concurrency Framework
- **File I/O**: Java NIO for logging and reports
- **API Integration**: HTTP client for Aviation Stack API
- **External APIs**: Aviation Stack API for real-time flight data

## 📋 Prerequisites

- Java Development Kit (JDK) 21 or higher
- MySQL Server 8.0 or higher
- NetBeans IDE (recommended) or any Java IDE
- Internet connection for Aviation Stack API features
- Aviation Stack API key (included in config)

## 🚀 Quick Start

### 1. Database Setup

1. Start MySQL Server
2. Create the database and tables:
   ```sql
   mysql -u root -p < db/schema.sql
   ```
3. Update database credentials in `config.properties`

### 2. Dependencies Setup

Download the required JAR files to the `lib/` directory:

- **MySQL Connector/J**: [Download](https://dev.mysql.com/downloads/connector/j/)
- **FlatLaf**: [Download](https://github.com/JFormDesigner/FlatLaf/releases)

Place the JAR files in the `lib/` directory:
```
lib/
├── mysql-connector-j-8.2.0.jar
└── flatlaf-3.4.jar
```

### 3. Configuration

Edit `config.properties` with your database settings:
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/aerodesk_pro
db.username=your_username
db.password=your_password

# Aviation Stack API Configuration
aviationstack.api.key=26ca9c80a5bfd430f608d77897c3c845
aviationstack.api.url=https://api.aviationstack.com/v1
```

### 4. Build and Run

#### Using NetBeans:
1. Open the project in NetBeans
2. Clean and Build the project
3. Run the project

#### Using Command Line:
```bash
# Build the project
ant build

# Run the application
ant run
```

## 👤 Default Login Credentials

- **Admin User**: 
  - Username: `admin`
  - Password: `admin123`
  - Role: `ADMIN`

- **Staff Users**:
  - Username: `staff1` or `staff2`
  - Password: `staff123`
  - Role: `STAFF`

## 🏗 Project Structure

```
AeroDesk-Pro/
├── src/
│   └── aerodesk/
│       ├── Main.java                 # Application entry point
│       ├── ui/                       # User interface components
│       │   ├── LoginFrame.java
│       │   ├── MainMenuFrame.java
│       │   ├── AviationStackFrame.java  # NEW: Aviation Stack UI
│       │   └── ... (other UI frames)
│       ├── model/                    # Data models
│       │   ├── Flight.java
│       │   ├── Booking.java
│       │   └── ... (other models)
│       ├── dao/                      # Data Access Objects
│       │   ├── FlightDAO.java
│       │   ├── BookingDAO.java
│       │   └── ... (other DAOs)
│       ├── service/                  # Business logic services
│       │   ├── FlightService.java
│       │   ├── BaggageService.java
│       │   ├── AviationStackService.java      # NEW: Aviation Stack service
│       │   └── FlightDataIntegrationService.java  # NEW: Data sync service
│       ├── util/                     # Utility classes
│       │   ├── ConfigManager.java
│       │   ├── FileLogger.java
│       │   ├── DatabaseConnection.java
│       │   └── ApiIntegrator.java    # NEW: API integration
│       └── exception/                # Custom exceptions
│           ├── DatabaseException.java
│           └── GateConflictException.java
├── db/
│   └── schema.sql                    # Database schema
├── lib/                              # External dependencies
├── config.properties                 # Application configuration
├── build.xml                         # Ant build script
├── AVIATION_STACK_INTEGRATION_GUIDE.md  # NEW: Integration guide
└── README.md                         # This file
```

## 🛫 Aviation Stack Integration

### Features Available
- **Flight Tracking**: Enter any flight number to get real-time information
- **Airport Information**: Get detailed airport data including contact info and statistics
- **Airline Information**: Look up airline details and fleet information
- **Route Search**: Find flights between any two airports
- **Live Tracking**: Monitor flights with live position data
- **Airport Statistics**: View comprehensive airport statistics and flight counts

### How to Use
1. Login to the application
2. Click "Aviation Stack API" button in the main menu
3. Use any of the 6 available features:
   - **Track Flight**: Enter flight number (e.g., AA101)
   - **Get Airport Info**: Enter airport code (e.g., JFK)
   - **Airport Stats**: View comprehensive airport statistics
   - **Airline Info**: Look up airline information
   - **Search Route**: Find flights between airports
   - **Live Tracking**: Monitor live flight positions

### API Integration Details
- **Real-time Data**: Live flight tracking with position, altitude, speed
- **Error Handling**: Graceful fallback to mock data when API is unavailable
- **Background Sync**: Automatic data synchronization with local database
- **Comprehensive Logging**: All API interactions are logged for monitoring

## 🔧 Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Implement proper exception handling

### Database Operations
- Use prepared statements to prevent SQL injection
- Implement proper connection pooling
- Handle database exceptions gracefully
- Use transactions for complex operations

### UI Design
- Follow FlatLaf design principles
- Implement responsive layouts
- Use SwingWorker for background operations
- Provide user feedback for all operations

### Multithreading
- Use SwingWorker for UI updates
- Implement proper synchronization
- Handle thread interruption gracefully
- Avoid blocking the EDT (Event Dispatch Thread)

### API Integration
- Implement proper error handling with fallbacks
- Use background threads for API calls
- Cache responses when appropriate
- Log all API interactions for monitoring

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL server is running
   - Check database credentials in `config.properties`
   - Ensure database `aerodesk_pro` exists

2. **Missing Dependencies**
   - Verify all JAR files are in the `lib/` directory
   - Check classpath configuration in `project.properties`

3. **UI Not Displaying**
   - Ensure FlatLaf JAR is properly included
   - Check for any console error messages

4. **Build Errors**
   - Clean and rebuild the project
   - Verify Java version compatibility
   - Check for missing import statements

5. **Aviation Stack API Issues**
   - Check internet connection
   - Verify API key in `config.properties`
   - Check application logs for API errors
   - System will fallback to mock data if API is unavailable

## 📝 Logging

The application logs all activities to `aerodesk.log` in the project root directory. Log levels include:
- **INFO**: General application events
- **WARNING**: Potential issues
- **ERROR**: Error conditions
- **DEBUG**: Detailed debugging information

### API Logging
- All Aviation Stack API calls are logged
- Response times and success/failure rates are tracked
- Error details are logged for troubleshooting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add appropriate tests
5. Submit a pull request

## 📄 License

This project is developed for educational purposes as part of a hackathon project.

## 🎯 Project Status

### ✅ Completed Features
- [x] **Authentication System** - Login with role-based access
- [x] **Database Integration** - MySQL with JDBC connectivity
- [x] **Flight Scheduling Module** - Complete CRUD operations
- [x] **Passenger Check-In Module** - Booking management and check-in
- [x] **Baggage Handling Module** - Real-time baggage tracking
- [x] **Gate Management Module** - Gate assignment and conflict detection
- [x] **Flight Status Dashboard** - Real-time status with weather
- [x] **Reporting System** - Comprehensive reports and analytics
- [x] **Aviation Stack API Integration** - Real-time flight data and tracking
- [x] **System Logging** - Comprehensive logging and monitoring

### 🚧 In Progress
- [ ] **Advanced Analytics** - Machine learning integration
- [ ] **Mobile Companion App** - Cross-platform mobile application
- [ ] **Performance Optimizations** - Caching and connection pooling

### 🔮 Future Enhancements
- [ ] **Real-time Map Integration** - Visual flight tracking
- [ ] **Flight Alert System** - Automated notifications
- [ ] **Advanced Weather Integration** - Detailed weather impact analysis
- [ ] **Third-party Integrations** - Additional aviation APIs

---

**Developed with ❤️ for the AeroDesk Pro Hackathon**

**Aviation Stack Integration Status: ✅ FULLY FUNCTIONAL** 🛫✈️ 