# AeroDesk Pro - Advanced Airport Management System

A comprehensive Java SE application for managing airport operations including flight scheduling, passenger check-in, baggage handling, gate management, real-time flight status monitoring, and **enhanced Aviation Stack API integration** for live flight data with professional reporting and analytics.

## 🚀 Features

### ✅ Core Airport Management
- **Flight Scheduling**: Create, edit, and manage flight schedules with enhanced validation
- **Passenger Check-In**: Streamlined passenger check-in process with boarding pass generation
- **Baggage Handling**: Track baggage from check-in to delivery with real-time simulation
- **Gate Management**: Assign and monitor gate availability with conflict detection
- **Real-Time Flight Status**: Live flight status updates with weather integration
- **Enhanced Reporting & Logging**: Professional reporting system with advanced analytics

### ✅ **Enhanced Aviation Stack API Integration** 🛫
- **Real-time Flight Tracking**: Live position, altitude, speed, and direction data with continuous updates
- **Dynamic Airport Information**: Complete airport details with location-specific data for major airports
- **Comprehensive Airline Information**: Airline profiles with fleet data and company details
- **Advanced Route Search**: Find flights between airports with enhanced filtering
- **Live Flight Monitoring**: Real-time flight status with live tracking capabilities every 10 seconds
- **Enhanced Flight Data**: Aircraft information, gate details, weather data, and passenger counts
- **API Call Tracking**: Real-time API usage monitoring and call counting
- **Professional Data Formatting**: Timestamped results with detailed section formatting
- **Continuous Live Updates**: Background monitoring with automatic data refresh
- **Export & Clear Functionality**: Export API data and clear results for better management

### ✅ **Enhanced Reports & Logs System** 📊
- **Professional UI Design**: Clean, icon-free interface with modern typography
- **Wider Action Buttons**: 150px width buttons for improved usability
- **Advanced Search Functionality**: Multi-type search with date range filtering
- **Real-time Status Panel**: Live status updates, last update time, and record counters
- **Print & Email Integration**: Direct printing and email functionality for reports
- **Auto-refresh System**: Background data updates every 5 minutes
- **Enhanced Table Structure**: Additional columns for comprehensive data display
- **Keyboard Shortcuts**: Enter key for quick search activation
- **Professional Export**: Enhanced CSV export with status feedback
- **Resource Management**: Proper cleanup and memory management

### ✅ Technical Features
- **Multi-threaded Operations**: Background processing for real-time updates
- **Database Integration**: MySQL database with JDBC connectivity
- **Modern UI**: FlatLaf themed Swing interface with professional styling
- **API Integration**: HTTP client for external data with comprehensive error handling
- **Comprehensive Logging**: Detailed system logging with multiple levels
- **Configuration Management**: Flexible configuration system
- **Auto-refresh Schedulers**: Background thread management for live updates
- **Enhanced Error Handling**: Graceful fallbacks and user-friendly error messages

## 🛠 Technology Stack

- **Language**: Java SE 21
- **UI Framework**: Java Swing with FlatLaf theming
- **Database**: MySQL 8.0+
- **Database Driver**: MySQL Connector/J 8.2.0
- **Build System**: NetBeans Ant-based build
- **Multithreading**: Java Concurrency Framework with ScheduledExecutorService
- **File I/O**: Java NIO for logging and reports
- **API Integration**: HTTP client for Aviation Stack API with retry mechanisms
- **External APIs**: Aviation Stack API for real-time flight data
- **Printing**: Java Print API for report generation
- **Background Processing**: ScheduledExecutorService for auto-refresh operations

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
3. Populate the database with sample data:
   ```sql
   mysql -u root -p < db/populate_database.sql
   ```
4. Update database credentials in `config.properties`

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
│       │   ├── AviationStackFrame.java  # Enhanced Aviation Stack UI
│       │   ├── ReportsFrame.java        # Enhanced Reports & Logs UI
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
│       │   ├── AviationStackService.java      # Enhanced Aviation Stack service
│       │   └── FlightDataIntegrationService.java  # Data sync service
│       ├── util/                     # Utility classes
│       │   ├── ConfigManager.java
│       │   ├── FileLogger.java
│       │   ├── DatabaseConnection.java
│       │   └── ApiIntegrator.java    # Enhanced API integration
│       └── exception/                # Custom exceptions
│           ├── DatabaseException.java
│           └── GateConflictException.java
├── db/
│   ├── schema.sql                    # Database schema
│   └── populate_database.sql         # Sample data population
├── lib/                              # External dependencies
├── config.properties                 # Application configuration
├── build.xml                         # Ant build script
├── AVIATION_STACK_INTEGRATION_GUIDE.md  # Integration guide
└── README.md                         # This file
```

## 🛫 Enhanced Aviation Stack Integration

### Features Available
- **Advanced Flight Tracking**: Enter any flight number to get real-time information with live updates
- **Dynamic Airport Information**: Get detailed airport data for JFK, LAX, LHR, CDG, NRT with location-specific details
- **Comprehensive Airline Information**: Look up airline details for AA, DL, UA, BA, AF, LH with fleet information
- **Enhanced Route Search**: Find flights between any two airports with detailed results
- **Live Tracking with Auto-refresh**: Monitor flights with live position data every 10 seconds
- **Airport Statistics**: View comprehensive airport statistics and flight counts
- **API Call Monitoring**: Real-time tracking of API usage and call counts
- **Professional Data Export**: Export API results and clear data for better management

### How to Use
1. Login to the application
2. Click "Aviation Stack API" button in the main menu
3. Use the enhanced tabbed interface with 4 main sections:
   - **Flight Tracking**: Enter flight number (e.g., AA101, DL202, UA303)
   - **Airport Information**: Enter airport code (e.g., JFK, LAX, LHR, CDG, NRT)
   - **Route Search**: Find flights between airports
   - **API Management**: Monitor API usage and manage data

### Enhanced API Integration Details
- **Real-time Data**: Live flight tracking with position, altitude, speed, direction
- **Dynamic Mock Data**: Location-specific fallback data when API is unavailable
- **Continuous Monitoring**: Background updates every 10 seconds for live tracking
- **API Call Tracking**: Real-time display of API usage statistics
- **Professional Formatting**: Timestamped results with clear section separators
- **Enhanced Error Handling**: Graceful fallback to realistic mock data
- **Background Sync**: Automatic data synchronization with local database
- **Comprehensive Logging**: All API interactions are logged for monitoring

## 📊 Enhanced Reports & Logs System

### Professional Features
- **Clean UI Design**: Icon-free interface with modern typography and professional appearance
- **Wider Buttons**: All action buttons are 150px wide for improved usability
- **Advanced Search**: Multi-type search with date range filtering and real-time results
- **Status Monitoring**: Live status updates, last update time, and total record counters
- **Print Integration**: Direct printing functionality with formatted output
- **Email Integration**: Report emailing capability for easy distribution
- **Auto-refresh**: Background data updates every 5 minutes
- **Keyboard Shortcuts**: Enter key for quick search activation
- **Enhanced Export**: Professional CSV export with status feedback
- **Resource Management**: Proper cleanup and memory management

### Search Capabilities
- **Multi-Type Search**: Search across Flights, Bookings, Baggage, Gates, Logs, or All
- **Date Range Filtering**: Today, Last 7 Days, Last 30 Days, This Month, Last Month
- **Real-time Results**: Dedicated Search tab with formatted results
- **Comprehensive Coverage**: Search across all system data and logs

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
- Use wider buttons (150px) for better usability
- Remove icons for professional appearance

### Multithreading
- Use SwingWorker for UI updates
- Implement proper synchronization
- Handle thread interruption gracefully
- Avoid blocking the EDT (Event Dispatch Thread)
- Use ScheduledExecutorService for background operations

### API Integration
- Implement proper error handling with fallbacks
- Use background threads for API calls
- Cache responses when appropriate
- Log all API interactions for monitoring
- Provide realistic mock data when API is unavailable
- Track API usage and provide user feedback

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
   - System will use realistic mock data as fallback

6. **Reports & Logs Issues**
   - Check file permissions for log files
   - Verify database connectivity for reports
   - Ensure proper cleanup of background threads

## 🎯 Recent Enhancements

### Aviation Stack API Improvements
- ✅ **Enhanced UI**: Tabbed interface with 4 main sections
- ✅ **Live Tracking**: Continuous updates every 10 seconds
- ✅ **Dynamic Data**: Location-specific airport and airline information
- ✅ **API Monitoring**: Real-time call counting and usage tracking
- ✅ **Professional Formatting**: Timestamped results with clear sections
- ✅ **Export Functionality**: Export and clear API data
- ✅ **Wider Buttons**: 150px width for better usability
- ✅ **Enhanced Error Handling**: Realistic mock data fallbacks

### Reports & Logs Enhancements
- ✅ **Professional UI**: Clean, icon-free interface
- ✅ **Advanced Search**: Multi-type search with date filtering
- ✅ **Real-time Status**: Live updates and record counters
- ✅ **Print & Email**: Direct printing and email functionality
- ✅ **Auto-refresh**: Background updates every 5 minutes
- ✅ **Enhanced Tables**: Additional columns for comprehensive data
- ✅ **Keyboard Shortcuts**: Enter key for quick search
- ✅ **Resource Management**: Proper cleanup and memory management

### General Improvements
- ✅ **Wider Buttons**: All buttons standardized to 150px width
- ✅ **Icon Removal**: Professional appearance without icons
- ✅ **Enhanced Typography**: Better fonts and spacing
- ✅ **Background Processing**: ScheduledExecutorService for live updates
- ✅ **Error Handling**: Graceful fallbacks and user feedback
- ✅ **Performance**: Optimized data loading and processing 