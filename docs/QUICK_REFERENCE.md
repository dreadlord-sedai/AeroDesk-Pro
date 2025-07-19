# AeroDesk Pro - Quick Reference Card

## 🚀 Quick Commands

### Compile and Run
```bash
# Compile
javac -cp "lib/*;src" src/aerodesk/**/*.java

# Run
java -cp "lib/*;src" aerodesk.Main
```

### Database Setup
```sql
CREATE DATABASE aerodesk;
CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON aerodesk.* TO 'aerodesk_user'@'localhost';
```

## 🔑 Default Login
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: Admin (Full access)

## 📁 Key Files

### Configuration
- `config.properties` - Main configuration file
- `logs/aerodesk.log` - Application logs

### Documentation
- `docs/README.md` - Documentation index
- `docs/SETUP_GUIDE.md` - Quick setup guide
- `docs/USER_GUIDE.md` - Complete user guide
- `docs/DOCUMENTATION.md` - Technical documentation
- `docs/TECHNICAL_ARCHITECTURE.md` - Architecture details

## 🎯 Main Features

### Core Modules
1. **Flight Scheduling** - Manage flight schedules
2. **Passenger Check-In** - Process check-ins
3. **Baggage Handling** - Track baggage
4. **Gate Management** - Assign gates
5. **Flight Status** - Real-time tracking
6. **Aviation Stack API** - External flight data
7. **Reports & Logs** - Generate reports
8. **Live Dashboard** - Real-time KPIs

### User Roles
- **Admin**: Full system access
- **Operator**: Operational functions
- **Viewer**: Read-only access

## 🔧 Configuration

### Database Settings
```properties
db.url=jdbc:mysql://localhost:3306/aerodesk
db.username=aerodesk_user
db.password=your_password
```

### API Keys (Optional)
```properties
aviationstack.api.key=your_key
openweathermap.api.key=your_key
```

## 📊 Database Tables

### Core Tables
- `users` - User accounts
- `flights` - Flight information
- `bookings` - Passenger bookings
- `baggage` - Baggage tracking
- `gate_assignments` - Gate assignments

## 🐛 Common Issues

### Database Connection
- Verify MySQL is running
- Check credentials in config.properties
- Ensure database exists

### API Issues
- Check internet connection
- Verify API keys are valid
- Review API rate limits

### Application Issues
- Check Java version (21+)
- Verify classpath includes lib/* and src
- Review logs/aerodesk.log

## 📞 Support

### Log Files
- **Location**: `logs/aerodesk.log`
- **Debug Mode**: Set `app.log.level=DEBUG`

### Documentation
- **User Guide**: `docs/USER_GUIDE.md`
- **Technical Docs**: `docs/DOCUMENTATION.md`
- **Architecture**: `docs/TECHNICAL_ARCHITECTURE.md`

## 🔄 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    AeroDesk Pro System                      │
├─────────────────────────────────────────────────────────────┤
│  Presentation Layer (UI)                                    │
│  ├── LoginFrame, MainMenuFrame, DashboardFrame             │
│  ├── FlightSchedulingFrame, CheckInFrame, BaggageFrame     │
│  └── GateManagementFrame, AviationStackFrame, ReportsFrame │
├─────────────────────────────────────────────────────────────┤
│  Business Logic Layer (Service)                            │
│  ├── AuthenticationService, FlightService, BookingService  │
│  ├── BaggageService, GateService, AviationStackService     │
│  └── WeatherService, DashboardMetrics                      │
├─────────────────────────────────────────────────────────────┤
│  Data Access Layer (DAO)                                   │
│  ├── UserDAO, FlightDAO, BookingDAO                        │
│  └── BaggageDAO, GateDAO                                   │
├─────────────────────────────────────────────────────────────┤
│  Data Layer (Database)                                     │
│  ├── MySQL Database                                        │
│  └── Configuration Files                                   │
└─────────────────────────────────────────────────────────────┘
```

## 🎨 UI Features

### Main Menu
- Professional dashboard design
- Quick stats panel
- 3x3 menu grid
- Real-time status display

### Live Dashboard
- Real-time KPIs
- Interactive flight map
- Weather overlays
- Auto-refresh every 30 seconds

## 🔐 Security

### Authentication
- Password hashing
- Session management
- Role-based access
- Input validation

### Data Protection
- SQL injection prevention
- Prepared statements
- Secure configuration
- Audit logging

## 📈 Performance

### Caching
- Weather data cache (10 minutes)
- Flight data cache
- Session cache
- Configuration cache

### Optimization
- Connection pooling
- Background tasks
- Async processing
- Database indexing

## 🚀 Deployment

### Requirements
- Java SE 21+
- MySQL 8.0+
- 2GB RAM minimum
- 500MB disk space

### Production
- Use connection pooling
- Configure proper logging
- Set up monitoring
- Regular backups

---

**For complete information, see the full documentation in the docs folder.** 