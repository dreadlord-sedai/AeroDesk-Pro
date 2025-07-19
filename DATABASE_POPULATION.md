# Database Population Guide

## Overview
This guide explains how to populate the AeroDesk Pro database with realistic sample data for testing and demonstration purposes.

## Files Included

### 1. `populate_database.sql`
The main SQL script containing all sample data:
- **12 Gates** across 3 terminals (A, B, C)
- **15 Flights** (domestic and international)
- **25 Bookings** with various ticket classes
- **36 Baggage items** (checked and carry-on)
- **15 Gate assignments**
- **20 System log entries**

### 2. `populate_database.bat`
Windows batch script for easy execution.

### 3. `populate_database.ps1`
PowerShell script with better error handling and cross-platform support.

## Prerequisites

1. **MySQL Server** installed and running
2. **Database created** using `setup_database.sql`
3. **Configuration** in `config.properties` file
4. **MySQL command line client** in PATH

## Quick Start

### Option 1: Using PowerShell (Recommended)
```powershell
.\populate_database.ps1
```

### Option 2: Using Batch Script
```cmd
populate_database.bat
```

### Option 3: Manual Execution
```bash
mysql -u your_username -p your_database < populate_database.sql
```

## Sample Data Details

### Gates (12 records)
- **Terminal A**: Gates A1-A4 (150-200 capacity each)
- **Terminal B**: Gates B1-B4 (120-180 capacity each)  
- **Terminal C**: Gates C1-C4 (150-220 capacity each)
- **Status**: Most available, some under maintenance

### Flights (15 records)
- **Domestic**: Delta, American, United, Southwest (5 flights)
- **International**: British Airways, Air France, Lufthansa, Japan Airlines, Emirates (5 flights)
- **Evening**: Additional domestic flights (5 flights)
- **Aircraft**: Various Boeing and Airbus models
- **Occupancy**: 60-85% capacity

### Bookings (25 records)
- **Ticket Classes**: Economy, Business, First
- **Check-in Status**: Mix of checked-in and pending
- **Special Requests**: Dietary, seating preferences, assistance
- **Passengers**: Diverse names and requirements

### Baggage (36 records)
- **Types**: Checked luggage and carry-on bags
- **Weights**: Realistic weight ranges (5-30 kg)
- **Status**: Loaded in cargo hold or secured in cabin
- **Tracking**: Full traceability from check-in

### Gate Assignments (15 records)
- **Strategic Assignment**: Domestic flights to Terminals A/B, International to Terminal C
- **Timing**: Coordinated with flight schedules
- **Notes**: Detailed assignment information

### System Logs (20 records)
- **Activities**: Gate assignments, check-ins, baggage handling
- **Users**: staff1 and staff2
- **Timestamps**: Realistic operational timeline

## Data Relationships

### Flight Operations
```
Flight DL101 (JFK → LAX)
├── Gate Assignment: A1
├── Bookings: 5 passengers
│   ├── John Smith (Economy, Checked-in)
│   ├── Sarah Johnson (Economy, Checked-in)
│   ├── Michael Brown (Business, Checked-in)
│   ├── Emily Davis (Business, Checked-in)
│   └── David Wilson (Economy, Pending)
└── Baggage: 8 items
    ├── 4 checked bags (cargo hold)
    └── 4 carry-on bags (cabin)
```

### Terminal Distribution
- **Terminal A**: Domestic flights (Delta, American)
- **Terminal B**: Domestic flights (United, Southwest)
- **Terminal C**: International flights (all carriers)

## Verification Queries

After running the population script, you can verify the data with these queries:

### Check Record Counts
```sql
SELECT 'GATES' as table_name, COUNT(*) as record_count FROM gates
UNION ALL
SELECT 'FLIGHTS', COUNT(*) FROM flights
UNION ALL
SELECT 'BOOKINGS', COUNT(*) FROM bookings
UNION ALL
SELECT 'BAGGAGE', COUNT(*) FROM baggage
UNION ALL
SELECT 'GATE_ASSIGNMENTS', COUNT(*) FROM gate_assignments
UNION ALL
SELECT 'SYSTEM_LOGS', COUNT(*) FROM system_logs;
```

### Flight Occupancy
```sql
SELECT 
    flight_number,
    airline,
    ROUND((booked_seats / capacity) * 100, 1) as occupancy_percentage
FROM flights
ORDER BY departure_time;
```

### Gate Utilization
```sql
SELECT 
    g.gate_number,
    g.terminal,
    COUNT(ga.flight_number) as assigned_flights
FROM gates g
LEFT JOIN gate_assignments ga ON g.gate_number = ga.gate_number
GROUP BY g.gate_number, g.terminal
ORDER BY g.terminal, g.gate_number;
```

## Troubleshooting

### Common Issues

1. **MySQL not found**
   - Ensure MySQL is installed and in PATH
   - Try using full path to mysql.exe

2. **Connection refused**
   - Check MySQL service is running
   - Verify credentials in config.properties

3. **Database not found**
   - Run `setup_database.sql` first
   - Check database name in config.properties

4. **Permission denied**
   - Ensure user has INSERT privileges
   - Check MySQL user permissions

### Error Messages

- **"Access denied"**: Wrong username/password
- **"Unknown database"**: Database doesn't exist
- **"Table doesn't exist"**: Schema not created
- **"Duplicate entry"**: Data already exists

## Customization

### Adding More Data
To add additional sample data:

1. **Edit the SQL file** to add more INSERT statements
2. **Maintain referential integrity** between tables
3. **Use realistic data** for testing scenarios
4. **Update verification queries** if needed

### Modifying Existing Data
- **Flights**: Change times, routes, aircraft
- **Bookings**: Add more passengers, change classes
- **Gates**: Modify capacities, terminals
- **Baggage**: Adjust weights, types

## Best Practices

1. **Backup first**: Always backup existing data
2. **Test environment**: Use separate test database
3. **Consistent data**: Maintain realistic relationships
4. **Documentation**: Update this guide for changes
5. **Version control**: Track data changes

## Next Steps

After populating the database:

1. **Run the application**: `java -cp "lib/*;src" aerodesk.Main`
2. **Test all modules**: Verify data appears correctly
3. **Create test scenarios**: Use the sample data for testing
4. **Generate reports**: Test reporting functionality
5. **Simulate operations**: Test real-world scenarios

## Support

For issues with database population:
1. Check the troubleshooting section above
2. Verify MySQL installation and configuration
3. Review error messages in detail
4. Ensure all prerequisites are met
5. Test with a simple query first

---

**Note**: This sample data is for testing and demonstration purposes only. In production, use real data and ensure proper data security measures. 