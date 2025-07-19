# AeroDesk Pro - Advanced Airport Management System

A comprehensive Java SE application for managing airport operations including flight scheduling, passenger check-in, baggage handling, gate management, and real-time flight status monitoring.

## 🚀 Features

- **Flight Scheduling**: Create, edit, and manage flight schedules
- **Passenger Check-In**: Streamlined passenger check-in process with boarding pass generation
- **Baggage Handling**: Track baggage from check-in to delivery with real-time simulation
- **Gate Management**: Assign and monitor gate availability with conflict detection
- **Real-Time Flight Status**: Live flight status updates with weather integration
- **Reporting & Logging**: Comprehensive reporting and system logging
- **Multi-threaded Operations**: Background processing for real-time updates
- **Database Integration**: MySQL database with JDBC connectivity
- **Modern UI**: FlatLaf themed Swing interface

## 🛠 Technology Stack

- **Language**: Java SE 21
- **UI Framework**: Java Swing with FlatLaf theming
- **Database**: MySQL 8.0+
- **Database Driver**: MySQL Connector/J 8.2.0
- **Build System**: NetBeans Ant-based build
- **Multithreading**: Java Concurrency Framework
- **File I/O**: Java NIO for logging and reports
- **API Integration**: HTTP client for external data

## 📋 Prerequisites

- Java Development Kit (JDK) 21 or higher
- MySQL Server 8.0 or higher
- NetBeans IDE (recommended) or any Java IDE
- Internet connection for API features

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
db.url=jdbc:mysql://localhost:3306/aerodesk_pro
db.username=your_username
db.password=your_password
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
│       │   └── ... (other services)
│       ├── util/                     # Utility classes
│       │   ├── ConfigManager.java
│       │   ├── FileLogger.java
│       │   └── DatabaseConnection.java
│       └── exception/                # Custom exceptions
│           ├── DatabaseException.java
│           └── GateConflictException.java
├── db/
│   └── schema.sql                    # Database schema
├── lib/                              # External dependencies
├── config.properties                 # Application configuration
├── build.xml                         # Ant build script
└── README.md                         # This file
```

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

## 📝 Logging

The application logs all activities to `aerodesk.log` in the project root directory. Log levels include:
- **INFO**: General application events
- **WARNING**: Potential issues
- **ERROR**: Error conditions
- **DEBUG**: Detailed debugging information

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add appropriate tests
5. Submit a pull request

## 📄 License

This project is developed for educational purposes as part of a hackathon project.

## 🎯 Roadmap

- [ ] Flight Scheduling Module
- [ ] Passenger Check-In Module
- [ ] Baggage Handling Module
- [ ] Gate Management Module
- [ ] Flight Status Dashboard
- [ ] Reporting System
- [ ] API Integration
- [ ] Advanced Analytics
- [ ] Mobile Companion App

---

**Developed with ❤️ for the AeroDesk Pro Hackathon** 