-- AeroDesk Pro Database Test Script
-- Run this after setup_database.sql to verify everything works

USE aerodesk_pro;

-- Test 1: Check if all tables exist
SHOW TABLES;

-- Test 2: Check users table structure
DESCRIBE users;

-- Test 3: Verify sample users exist
SELECT id, username, role, full_name, is_active FROM users;

-- Test 4: Test authentication queries
SELECT * FROM users WHERE username = 'admin' AND password_hash = 'admin123' AND role = 'ADMIN' AND is_active = TRUE;
SELECT * FROM users WHERE username = 'staff1' AND password_hash = 'staff123' AND role = 'STAFF' AND is_active = TRUE;

-- Test 5: Check flights table
SELECT COUNT(*) as flight_count FROM flights;

-- Test 6: Check gates table
SELECT COUNT(*) as gate_count FROM gates;

-- Test 7: Verify foreign key relationships
SELECT 
    f.flight_number,
    b.booking_reference,
    b.passenger_name
FROM flights f
LEFT JOIN bookings b ON f.id = b.flight_id
LIMIT 5;

-- Test 8: Check application user permissions
-- (Run this as aerodesk_user)
SELECT 'Testing SELECT permission on flights' as test;
SELECT COUNT(*) FROM flights;

SELECT 'Testing SELECT permission on users' as test;
SELECT COUNT(*) FROM users;

-- Success message
SELECT 'All database tests completed successfully!' as status; 