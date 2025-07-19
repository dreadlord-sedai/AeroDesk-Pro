# AeroDesk Pro - User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [Login and Authentication](#login-and-authentication)
3. [Main Menu Navigation](#main-menu-navigation)
4. [Flight Management](#flight-management)
5. [Passenger Check-In](#passenger-check-in)
6. [Baggage Handling](#baggage-handling)
7. [Gate Management](#gate-management)
8. [Flight Status Tracking](#flight-status-tracking)
9. [Aviation Stack API](#aviation-stack-api)
10. [Reports and Logs](#reports-and-logs)
11. [Live Dashboard](#live-dashboard)
12. [Troubleshooting](#troubleshooting)

---

## Getting Started

### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Linux
- **Java Runtime**: Java SE 21 or higher
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: 500MB free disk space
- **Network**: Internet connection for API features

### Installation
1. **Download** the AeroDesk Pro application
2. **Extract** the files to your desired location
3. **Configure** the database connection (see Configuration section)
4. **Run** the application using the provided script or command

### First Launch
1. **Start** the application
2. **Login** with default credentials:
   - Username: `admin`
   - Password: `admin123`
3. **Change** the default password on first login
4. **Configure** API keys for full functionality

---

## Login and Authentication

### Login Screen
The login screen provides secure access to AeroDesk Pro with the following features:

- **Username Field**: Enter your username
- **Password Field**: Enter your password (hidden)
- **Login Button**: Authenticate and access the system
- **Remember Me**: Optional checkbox for session persistence

### User Roles
AeroDesk Pro supports three user roles with different access levels:

#### Admin Role
- **Full System Access**: All features and functions
- **User Management**: Create, edit, and delete users
- **System Configuration**: Modify system settings
- **Database Management**: Access to all data

#### Operator Role
- **Operational Functions**: Flight, passenger, and baggage management
- **Limited Configuration**: Basic system settings
- **Reporting Access**: Generate and view reports
- **No User Management**: Cannot modify user accounts

#### Viewer Role
- **Read-Only Access**: View data but cannot modify
- **Report Viewing**: Access to reports and logs
- **Dashboard Access**: View live dashboard
- **No Operational Functions**: Cannot perform operations

### Password Security
- **Minimum Length**: 8 characters
- **Complexity**: Must include letters and numbers
- **Expiration**: Passwords expire every 90 days
- **History**: Cannot reuse last 3 passwords

---

## Main Menu Navigation

### Main Menu Interface
The enhanced main menu provides a professional dashboard-like experience with:

#### Header Panel
- **Application Title**: "AeroDesk Pro - Airport Management System"
- **User Information**: Current user and role
- **System Status**: Online/offline status
- **Current Date**: Real-time date display

#### Quick Stats Panel
Live metrics displayed in card format:
- **Active Flights**: Number of currently active flights
- **Check-ins Today**: Total passenger check-ins for the day
- **Gates Occupied**: Number of currently occupied gates
- **System Status**: Overall system health status

#### Menu Grid
3x3 grid layout with enhanced menu buttons:
1. **Flight Scheduling** - Schedule and manage flights
2. **Passenger Check-In** - Process passenger check-ins
3. **Baggage Handling** - Manage baggage operations
4. **Gate Management** - Monitor and assign gates
5. **Flight Status** - Track flight status and updates
6. **Aviation Stack API** - Real-time flight data integration
7. **Reports & Logs** - Generate reports and view logs
8. **Live Dashboard** - Real-time operations monitoring
9. **Empty Slot** - Reserved for future features

#### Footer Panel
- **Copyright Information**: Application copyright notice
- **Logout Button**: Secure logout functionality

### Navigation Tips
- **Hover Effects**: Buttons highlight when you hover over them
- **Descriptions**: Each button shows a description of its function
- **Keyboard Shortcuts**: Use Tab key to navigate between elements
- **Responsive Design**: Interface adapts to different screen sizes

---

## Flight Management

### Flight Scheduling Interface
Access flight management through the "Flight Scheduling" button on the main menu.

#### Adding New Flights
1. **Click** "Add New Flight" button
2. **Enter** flight details:
   - Flight Number (e.g., AA123)
   - Origin Airport (e.g., JFK)
   - Destination Airport (e.g., LAX)
   - Departure Date and Time
   - Arrival Date and Time
   - Aircraft Type
3. **Click** "Save Flight" to create the flight

#### Flight Status Management
- **Scheduled**: Flight is planned but not yet active
- **On Time**: Flight is operating on schedule
- **Delayed**: Flight is behind schedule
- **Cancelled**: Flight has been cancelled
- **Departed**: Flight has left the gate

#### Flight Search and Filter
- **Search by Flight Number**: Enter specific flight number
- **Filter by Date**: Select date range for flights
- **Filter by Status**: Show flights by current status
- **Filter by Route**: Search flights by origin/destination

#### Flight Operations
- **Edit Flight**: Modify flight details
- **Update Status**: Change flight status
- **Delete Flight**: Remove flight from system
- **View Details**: See complete flight information

### Flight Data Validation
The system validates all flight data:
- **Flight Numbers**: Must follow airline format (e.g., AA123)
- **Airport Codes**: Must be valid 3-letter IATA codes
- **Times**: Arrival must be after departure
- **Dates**: Cannot schedule flights in the past

---

## Passenger Check-In

### Check-In Interface
Access passenger check-in through the "Passenger Check-In" button.

#### Processing Check-Ins
1. **Search** for passenger by name or booking reference
2. **Select** the passenger from search results
3. **Verify** passenger details and flight information
4. **Assign** seat number (if not already assigned)
5. **Process** check-in and print boarding pass

#### Check-In Status
- **Confirmed**: Booking confirmed but not checked in
- **Checked In**: Passenger has completed check-in
- **Boarded**: Passenger has boarded the aircraft
- **No Show**: Passenger did not check in

#### Seat Assignment
- **Automatic Assignment**: System can auto-assign seats
- **Manual Assignment**: Staff can manually assign seats
- **Seat Preferences**: Consider passenger preferences
- **Seat Conflicts**: System prevents double-booking

#### Boarding Pass Generation
- **Automatic Generation**: Created upon successful check-in
- **Print Options**: Print physical or digital boarding pass
- **QR Code**: Includes QR code for easy scanning
- **Flight Information**: Complete flight and passenger details

### Check-In Workflow
```
1. Passenger arrives at check-in counter
2. Staff searches for passenger booking
3. System displays passenger and flight details
4. Staff verifies passenger identity
5. System assigns seat (if needed)
6. Staff processes check-in
7. System generates boarding pass
8. Passenger receives boarding pass
```

---

## Baggage Handling

### Baggage Management Interface
Access baggage handling through the "Baggage Handling" button.

#### Baggage Check-In
1. **Scan** or enter baggage tag number
2. **Enter** passenger booking reference
3. **Weigh** baggage and enter weight
4. **Select** baggage type (checked, carry-on, special)
5. **Process** baggage check-in

#### Baggage Types
- **Checked Baggage**: Stored in aircraft hold
- **Carry-on Baggage**: Passenger keeps with them
- **Special Baggage**: Oversized, fragile, or special items
- **Excess Baggage**: Over weight or size limits

#### Baggage Tracking
- **Real-time Status**: Track baggage location
- **Status Updates**: Automatic status updates
- **Delivery Confirmation**: Confirm baggage delivery
- **Lost Baggage**: Report and track lost items

#### Baggage Status
- **Checked In**: Baggage received and processed
- **In Transit**: Baggage being transported
- **Loaded**: Baggage loaded onto aircraft
- **Delivered**: Baggage delivered to passenger
- **Lost**: Baggage reported as missing

### Baggage Workflow
```
1. Passenger checks in baggage
2. System generates baggage tag
3. Baggage weighed and categorized
4. Baggage transported to aircraft
5. Baggage loaded onto aircraft
6. Baggage unloaded at destination
7. Baggage delivered to passenger
```

---

## Gate Management

### Gate Assignment Interface
Access gate management through the "Gate Management" button.

#### Gate Assignment
1. **Select** flight from active flights list
2. **Choose** available gate from gate list
3. **Set** departure time
4. **Assign** gate to flight
5. **Confirm** assignment

#### Gate Status
- **Available**: Gate is free for assignment
- **Assigned**: Gate assigned to a flight
- **Occupied**: Flight is at the gate
- **Maintenance**: Gate under maintenance
- **Closed**: Gate temporarily closed

#### Gate Operations
- **Assign Gate**: Assign gate to flight
- **Change Gate**: Reassign flight to different gate
- **Release Gate**: Free gate after flight departure
- **Gate Schedule**: View gate usage schedule

#### Gate Monitoring
- **Real-time Status**: Live gate status updates
- **Flight Information**: Current flight at each gate
- **Schedule View**: Upcoming gate assignments
- **Conflict Detection**: Automatic conflict detection

### Gate Assignment Rules
- **One Flight Per Gate**: Only one flight can use a gate at a time
- **Time Separation**: Minimum time between flights at same gate
- **Aircraft Compatibility**: Gate must accommodate aircraft type
- **Priority System**: Emergency flights get priority

---

## Flight Status Tracking

### Flight Status Interface
Access flight status through the "Flight Status" button.

#### Real-time Status
- **Live Updates**: Real-time flight status updates
- **Status History**: Complete status change history
- **Delay Information**: Detailed delay reasons and times
- **ETA Updates**: Updated arrival time estimates

#### Status Categories
- **On Time**: Flight operating on schedule
- **Delayed**: Flight behind schedule
- **Cancelled**: Flight cancelled
- **Diverted**: Flight diverted to different airport
- **Landed**: Flight has landed

#### Flight Information Display
- **Flight Details**: Complete flight information
- **Passenger Count**: Number of passengers on board
- **Baggage Status**: Baggage loading status
- **Gate Information**: Current gate assignment

#### Status Updates
- **Automatic Updates**: System updates from external APIs
- **Manual Updates**: Staff can manually update status
- **Notification System**: Automatic notifications for status changes
- **Audit Trail**: Complete record of all status changes

---

## Aviation Stack API

### API Integration Interface
Access Aviation Stack features through the "Aviation Stack API" button.

#### Flight Tracking Tab
- **Live Flight Tracking**: Real-time flight position and status
- **Flight Search**: Search for specific flights
- **Flight Information**: Detailed flight data from API
- **Status Updates**: Live status updates from AviationStack

#### Airport Information Tab
- **Airport Details**: Complete airport information
- **Runway Information**: Runway details and status
- **Terminal Information**: Terminal facilities and services
- **Weather Information**: Current weather at airport

#### Route Search Tab
- **Route Planning**: Search flights between airports
- **Schedule Information**: Flight schedules for routes
- **Alternative Routes**: Find alternative flight options
- **Price Information**: Fare information (if available)

#### API Management Tab
- **API Status**: Check API connectivity and status
- **Usage Statistics**: Monitor API usage and limits
- **Configuration**: Manage API settings and keys
- **Error Logs**: View API error logs and troubleshooting

### API Features
- **Real-time Data**: Live flight and airport data
- **Global Coverage**: Worldwide flight information
- **Multiple Airlines**: Data from hundreds of airlines
- **Historical Data**: Access to historical flight data

---

## Reports and Logs

### Reports Interface
Access reports through the "Reports & Logs" button.

#### Report Types
- **Flight Reports**: Flight schedules, status, and performance
- **Passenger Reports**: Check-in statistics and passenger data
- **Baggage Reports**: Baggage handling statistics
- **Gate Reports**: Gate utilization and efficiency
- **System Reports**: System performance and usage

#### Report Generation
1. **Select** report type from dropdown
2. **Choose** date range for report
3. **Select** additional filters (if applicable)
4. **Click** "Generate Report"
5. **View** report in table format
6. **Export** report to PDF or Excel

#### Advanced Search
- **Multi-criteria Search**: Search across multiple fields
- **Date Range Filter**: Filter by specific date ranges
- **Status Filter**: Filter by various status types
- **Export Options**: Export search results

#### Log Management
- **System Logs**: Application and system logs
- **User Activity Logs**: User action logs
- **Error Logs**: Error and exception logs
- **API Logs**: External API interaction logs

### Report Features
- **Real-time Data**: Reports use current data
- **Export Formats**: PDF, Excel, CSV export options
- **Scheduled Reports**: Automatically generate reports
- **Email Delivery**: Send reports via email

---

## Live Dashboard

### Dashboard Interface
Access the live dashboard through the "Live Dashboard" button.

#### Live KPIs Tab
Real-time Key Performance Indicators displayed in card format:

**Flight Metrics**
- **Total Flights Today**: Number of flights scheduled for today
- **On-Time Flights**: Percentage of flights on time
- **Delayed Flights**: Number of delayed flights
- **Cancelled Flights**: Number of cancelled flights

**Operational Metrics**
- **Check-ins (Last Hour)**: Passengers checked in during last hour
- **Baggage Handled**: Baggage units processed in last hour
- **Gates Occupied**: Currently occupied gates
- **Average Delay**: Average delay time for delayed flights

**Status Indicators**
- 🟢 **Excellent**: Performance above 90%
- 🟡 **Good**: Performance 80-90%
- 🟠 **Warning**: Performance 70-80%
- 🔴 **Critical**: Performance below 70%

#### Flight Map Tab
Interactive map showing:
- **Airport Locations**: Major airports with coordinates
- **Flight Routes**: Visual representation of flight paths
- **Weather Overlay**: Current weather conditions at airports
- **Status Colors**: Color-coded flight status
  - 🟢 Green: On time
  - 🟡 Yellow: Delayed
  - 🔴 Red: Cancelled

#### Interactive Features
- **Click on Flights**: View detailed flight information
- **Click on Airports**: View airport and weather information
- **Legend**: Color-coded status indicators
- **Auto-refresh**: Map updates every 60 seconds

#### Dashboard Controls
- **Refresh Button**: Manually refresh dashboard data
- **Export Report**: Export comprehensive dashboard report
- **Auto-refresh**: Automatic updates every 30 seconds
- **Status Bar**: Shows last update time and system status

### Dashboard Data Sources
- **Database**: Internal flight and operational data
- **AviationStack API**: Real-time flight information
- **OpenWeatherMap API**: Current weather data
- **System Metrics**: Internal performance metrics

---

## Troubleshooting

### Common Issues and Solutions

#### Login Problems
**Issue**: Cannot log in with correct credentials
**Solutions**:
1. Check username and password spelling
2. Ensure Caps Lock is off
3. Verify database connection
4. Contact administrator if account is locked

#### Database Connection Issues
**Issue**: "Database connection failed" error
**Solutions**:
1. Verify database server is running
2. Check database credentials in configuration
3. Ensure network connectivity
4. Restart the application

#### API Integration Issues
**Issue**: No data displayed in Aviation Stack or Weather sections
**Solutions**:
1. Check internet connection
2. Verify API keys are valid
3. Check API service status
4. Review API rate limits

#### Performance Issues
**Issue**: Application runs slowly or freezes
**Solutions**:
1. Close other applications to free memory
2. Restart the application
3. Check system resources
4. Update Java runtime if needed

#### UI Display Issues
**Issue**: Interface elements not displaying correctly
**Solutions**:
1. Check screen resolution settings
2. Update graphics drivers
3. Restart the application
4. Verify Java version compatibility

### Getting Help

#### Log Files
- **Location**: `logs/aerodesk.log`
- **Content**: Detailed error and activity logs
- **Format**: Timestamp, Level, Class, Message

#### Support Information
- **Documentation**: Refer to this user guide
- **Configuration**: Check `config.properties` file
- **System Requirements**: Verify system meets requirements
- **Contact**: Reach out to system administrator

#### Debug Mode
Enable debug logging by setting in configuration:
```properties
app.log.level=DEBUG
```

### Best Practices

#### Security
- **Regular Password Changes**: Change password every 90 days
- **Secure Logout**: Always log out when finished
- **Account Protection**: Don't share login credentials
- **Session Management**: Close application when not in use

#### Data Management
- **Regular Backups**: Ensure data is regularly backed up
- **Data Validation**: Verify data accuracy before saving
- **Audit Trail**: Review logs for data changes
- **Error Reporting**: Report any data inconsistencies

#### Performance
- **Regular Updates**: Keep application updated
- **Resource Management**: Monitor system resources
- **Cache Management**: Clear cache if needed
- **Network Optimization**: Ensure stable internet connection

---

## Conclusion

AeroDesk Pro provides a comprehensive airport management solution with intuitive interfaces and powerful features. This user guide covers all major functions and should help you effectively use the system for airport operations.

For additional support or feature requests, please contact your system administrator or refer to the technical documentation. 