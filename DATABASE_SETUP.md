# AeroDesk Pro Database Setup Guide

## 🚀 Quick Setup for Any Computer

### Prerequisites
- MySQL Server 5.7+ or MySQL 8.0+
- Java 21 or higher
- MySQL Connector/J (included in lib folder)

---

## 📋 Step-by-Step Setup

### 1. Install MySQL Server

**Windows:**
```bash
# Download MySQL Installer from https://dev.mysql.com/downloads/installer/
# Run installer and follow setup wizard
# Remember the root password you set!
```

**macOS:**
```bash
# Using Homebrew
brew install mysql
brew services start mysql

# Or download from https://dev.mysql.com/downloads/mysql/
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. Set Up Database and User

**Option A: Using the Setup Script (Recommended)**
```bash
# Connect to MySQL as root
mysql -u root -p

# Run the setup script
source setup_database.sql;
```

**Option B: Manual Setup**
```sql
-- Connect to MySQL as root and run:
CREATE DATABASE aerodesk_pro;
USE aerodesk_pro;

-- Create application user
CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'aerodesk_password';
GRANT ALL PRIVILEGES ON aerodesk_pro.* TO 'aerodesk_user'@'localhost';
FLUSH PRIVILEGES;

-- Create tables (see setup_database.sql for full schema)
-- ... (run the CREATE TABLE statements from setup_database.sql)
```

### 3. Configure Application

**Copy and modify the configuration:**
```bash
# Copy the template
cp config_template.properties config.properties

# Edit config.properties with your settings
```

**Choose your configuration option:**

**Option 1: Dedicated User (Recommended)**
```properties
db.url=jdbc:mysql://localhost:3306/aerodesk_pro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=aerodesk_user
db.password=aerodesk_password
db.driver=com.mysql.cj.jdbc.Driver
```

**Option 2: Root User (Less Secure)**
```properties
db.url=jdbc:mysql://localhost:3306/aerodesk_pro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_ROOT_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Test Connection

**Run the application:**
```bash
java -cp "lib/*;src" aerodesk.Main
```

---

## 🔧 Troubleshooting Common Issues

### Issue 1: "Access denied for user"
**Solution:**
- Check if user exists: `SELECT User, Host FROM mysql.user;`
- Verify password: `ALTER USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'aerodesk_password';`
- Check permissions: `SHOW GRANTS FOR 'aerodesk_user'@'localhost';`

### Issue 2: "Communications link failure"
**Solutions:**
- Ensure MySQL service is running
- Check if port 3306 is correct
- Verify firewall settings
- Try: `mysql -u root -p -h localhost -P 3306`

### Issue 3: "Unknown database 'aerodesk_pro'"
**Solution:**
- Create database: `CREATE DATABASE aerodesk_pro;`
- Run setup script: `source setup_database.sql;`

### Issue 4: "Public Key Retrieval is not allowed"
**Solution:**
- Add `allowPublicKeyRetrieval=true` to JDBC URL
- Or use: `ALTER USER 'aerodesk_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'aerodesk_password';`

### Issue 5: "The server time zone value is unrecognized"
**Solution:**
- Add `serverTimezone=UTC` to JDBC URL
- Or set MySQL timezone: `SET GLOBAL time_zone = '+00:00';`

---

## 🔒 Security Best Practices

### 1. Use Dedicated User (Not Root)
```sql
-- Create application-specific user
CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON aerodesk_pro.* TO 'aerodesk_user'@'localhost';
```

### 2. Restrict Network Access
```sql
-- Only allow local connections
CREATE USER 'aerodesk_user'@'localhost' IDENTIFIED BY 'password';
-- NOT: CREATE USER 'aerodesk_user'@'%' IDENTIFIED BY 'password';
```

### 3. Use Strong Passwords
- Minimum 8 characters
- Mix of uppercase, lowercase, numbers, symbols
- Avoid common words

### 4. Regular Backups
```bash
# Create backup
mysqldump -u root -p aerodesk_pro > backup_$(date +%Y%m%d).sql

# Restore backup
mysql -u root -p aerodesk_pro < backup_20240115.sql
```

---

## 📊 Verification Commands

**Test database connection:**
```sql
mysql -u aerodesk_user -p aerodesk_pro
```

**Check tables exist:**
```sql
USE aerodesk_pro;
SHOW TABLES;
```

**Verify sample data:**
```sql
SELECT * FROM flights LIMIT 5;
SELECT * FROM gates LIMIT 5;
```

**Test application user permissions:**
```sql
-- Connect as aerodesk_user and try:
SELECT * FROM flights;
INSERT INTO flights (flight_number, origin, destination, departure_time, arrival_time) 
VALUES ('TEST001', 'JFK', 'LAX', '2024-01-16 10:00:00', '2024-01-16 13:30:00');
```

---

## 🆘 Getting Help

If you encounter issues:

1. **Check the log file:** `aerodesk.log`
2. **Verify MySQL status:** `systemctl status mysql` (Linux) or Services app (Windows)
3. **Test MySQL connection:** `mysql -u root -p`
4. **Check application logs:** Look for specific error messages

**Common Error Messages:**
- `Access denied`: Wrong username/password or missing permissions
- `Communications link failure`: MySQL not running or wrong port
- `Unknown database`: Database doesn't exist
- `Table doesn't exist`: Run setup script to create tables 