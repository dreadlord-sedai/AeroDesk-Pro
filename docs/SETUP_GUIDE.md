# AeroDesk Pro - Quick Setup Guide

This guide provides a quick overview for setting up and running AeroDesk Pro.

## 🚀 Quick Start

### Prerequisites
- **Java SE 21** or higher
- **MySQL 8.0** or higher
- **Internet connection** (for API features)

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE aerodesk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON aerodesk.* TO 'aerodesk_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Create `config.properties` in the project root:
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/aerodesk?useSSL=false&serverTimezone=UTC
db.username=aerodesk_user
db.password=your_password

# API Configuration (Optional)
aviationstack.api.key=your_aviation_stack_key
openweathermap.api.key=your_weather_api_key

# Application Configuration
app.name=AeroDesk Pro
app.version=2.0.0
app.log.level=INFO
```

### 3. Compile and Run
```bash
# Compile the application
javac -cp "lib/*;src" src/aerodesk/**/*.java

# Run the application
java -cp "lib/*;src" aerodesk.Main
```

### 4. First Login
- **Username**: `admin`
- **Password**: `admin123`
- **Change password** on first login

## 📁 Project Structure
```
AeroDesk/
├── src/
│   └── aerodesk/
│       ├── Main.java
│       ├── model/
│       ├── service/
│       ├── dao/
│       ├── ui/
│       └── util/
├── lib/                    # External libraries
├── docs/                   # Documentation
├── config.properties       # Configuration file
└── logs/                   # Log files
```

## 🔧 Key Features Setup

### Database Tables
The application will automatically create required tables on first run:
- `users` - User accounts and authentication
- `flights` - Flight information and schedules
- `bookings` - Passenger bookings and check-ins
- `baggage` - Baggage tracking and management
- `gate_assignments` - Gate assignments and scheduling

### API Integration (Optional)
1. **AviationStack API**: Get real-time flight data
   - Sign up at [aviationstack.com](https://aviationstack.com)
   - Add API key to config.properties

2. **OpenWeatherMap API**: Get weather data
   - Sign up at [openweathermap.org](https://openweathermap.org)
   - Add API key to config.properties

### Multithreading Configuration
The application uses extensive multithreading for API operations:

#### Thread Pool Settings
```properties
# Thread Configuration (in config.properties)
aviationstack.threads=3
flight.sync.threads=2
dashboard.threads=1
map.update.threads=1
baggage.threads=1
```

#### Background Task Intervals
- **Live Flight Tracking**: 10 seconds
- **Flight Data Sync**: 5 minutes
- **Flight Status Updates**: 2 minutes
- **Dashboard Metrics**: 30 seconds
- **Map Updates**: 60 seconds
- **Baggage Status**: 10 seconds

#### Performance Considerations
- **Memory Usage**: Each thread pool consumes memory
- **CPU Usage**: Background tasks use CPU resources
- **Network**: API calls consume bandwidth
- **Database**: Concurrent database operations

## 🎯 Quick Navigation

### Main Menu Features
1. **Flight Scheduling** - Manage flight schedules
2. **Passenger Check-In** - Process passenger check-ins
3. **Baggage Handling** - Track and manage baggage
4. **Gate Management** - Assign and monitor gates
5. **Flight Status** - Track real-time flight status
6. **Aviation Stack API** - External flight data
7. **Reports & Logs** - Generate reports and view logs
8. **Live Dashboard** - Real-time operational metrics

### User Roles
- **Admin**: Full system access
- **Operator**: Operational functions
- **Viewer**: Read-only access

## 🐛 Troubleshooting

### Common Issues
1. **Database Connection Failed**
   - Verify MySQL is running
   - Check database credentials
   - Ensure database exists

2. **API Data Not Loading**
   - Check internet connection
   - Verify API keys are valid
   - Check API service status

3. **Application Won't Start**
   - Verify Java version (21+)
   - Check classpath includes lib/* and src
   - Review error logs in logs/aerodesk.log

### Log Files
- **Location**: `logs/aerodesk.log`
- **Format**: `[Timestamp] [Level] [Class] - Message`
- **Debug Mode**: Set `app.log.level=DEBUG` in config

## 📚 Next Steps

1. **Read Documentation**: Start with [USER_GUIDE.md](./USER_GUIDE.md)
2. **Explore Features**: Try each menu option
3. **Configure APIs**: Add API keys for full functionality
4. **Customize**: Modify configuration as needed

## 📞 Support

- **Documentation**: Check the docs folder
- **Logs**: Review logs/aerodesk.log for errors
- **Configuration**: Verify config.properties settings

---

**For detailed information, see:**
- [Complete Documentation](./DOCUMENTATION.md)
- [Technical Architecture](./TECHNICAL_ARCHITECTURE.md)
- [User Guide](./USER_GUIDE.md) 