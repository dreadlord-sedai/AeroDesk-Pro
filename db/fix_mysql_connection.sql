-- Fix MySQL 8.0+ Authentication Issues
-- Run this as MySQL root user if you get "Public Key Retrieval is not allowed" error

USE mysql;

-- Option 1: Change authentication method for existing users
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'mysql2006';
ALTER USER 'aerodesk_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'aerodesk_password';

-- Option 2: If you want to use the new authentication method, update config.properties instead:
-- db.url=jdbc:mysql://localhost:3306/aerodesk_pro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Test the connection
SELECT 'MySQL authentication method updated successfully!' as status; 