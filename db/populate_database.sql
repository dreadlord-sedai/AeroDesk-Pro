-- =====================================================
-- AeroDesk Pro - Database Population Script
-- Populates all tables with realistic sample data
-- =====================================================

USE aerodesk_pro;

-- Clear existing data (except users)
DELETE FROM gate_assignments;
DELETE FROM baggage;
DELETE FROM bookings;
DELETE FROM flights;
DELETE FROM gates;

-- =====================================================
-- 1. POPULATE GATES TABLE
-- =====================================================
INSERT INTO gates (gate_number, terminal, status, capacity) VALUES
('A1', 'Terminal A', 'AVAILABLE', 150),
('A2', 'Terminal A', 'AVAILABLE', 120),
('A3', 'Terminal A', 'MAINTENANCE', 100),
('A4', 'Terminal A', 'AVAILABLE', 200),
('B1', 'Terminal B', 'AVAILABLE', 180),
('B2', 'Terminal B', 'AVAILABLE', 160),
('B3', 'Terminal B', 'AVAILABLE', 140),
('B4', 'Terminal B', 'MAINTENANCE', 120),
('C1', 'Terminal C', 'AVAILABLE', 220),
('C2', 'Terminal C', 'AVAILABLE', 190),
('C3', 'Terminal C', 'AVAILABLE', 170),
('C4', 'Terminal C', 'AVAILABLE', 150);

-- =====================================================
-- 2. POPULATE FLIGHTS TABLE
-- =====================================================
INSERT INTO flights (flight_number, airline, origin, destination, departure_time, arrival_time, aircraft_type, status, capacity, booked_seats) VALUES
-- Domestic Flights
('DL101', 'Delta Airlines', 'JFK', 'LAX', '2024-01-20 08:00:00', '2024-01-20 11:30:00', 'Boeing 737-800', 'SCHEDULED', 180, 145),
('AA202', 'American Airlines', 'JFK', 'ORD', '2024-01-20 09:15:00', '2024-01-20 11:45:00', 'Airbus A320', 'SCHEDULED', 150, 120),
('UA303', 'United Airlines', 'JFK', 'SFO', '2024-01-20 10:30:00', '2024-01-20 14:15:00', 'Boeing 757-200', 'SCHEDULED', 200, 178),
('SW404', 'Southwest Airlines', 'JFK', 'DEN', '2024-01-20 11:45:00', '2024-01-20 14:30:00', 'Boeing 737-700', 'SCHEDULED', 140, 95),
('DL505', 'Delta Airlines', 'JFK', 'ATL', '2024-01-20 12:00:00', '2024-01-20 14:45:00', 'Airbus A321', 'SCHEDULED', 190, 167),

-- International Flights
('BA601', 'British Airways', 'JFK', 'LHR', '2024-01-20 13:30:00', '2024-01-21 02:15:00', 'Boeing 777-300ER', 'SCHEDULED', 350, 298),
('AF702', 'Air France', 'JFK', 'CDG', '2024-01-20 14:45:00', '2024-01-21 04:30:00', 'Airbus A350-900', 'SCHEDULED', 300, 245),
('LH803', 'Lufthansa', 'JFK', 'FRA', '2024-01-20 16:00:00', '2024-01-21 06:45:00', 'Boeing 747-8', 'SCHEDULED', 400, 356),
('JL904', 'Japan Airlines', 'JFK', 'NRT', '2024-01-20 17:15:00', '2024-01-21 20:30:00', 'Boeing 787-9', 'SCHEDULED', 280, 234),
('EK1005', 'Emirates', 'JFK', 'DXB', '2024-01-20 18:30:00', '2024-01-21 16:45:00', 'Airbus A380-800', 'SCHEDULED', 500, 423),

-- Evening Flights
('DL1106', 'Delta Airlines', 'JFK', 'MIA', '2024-01-20 19:45:00', '2024-01-20 23:15:00', 'Boeing 737-900', 'SCHEDULED', 160, 128),
('AA1207', 'American Airlines', 'JFK', 'DFW', '2024-01-20 20:00:00', '2024-01-20 23:30:00', 'Airbus A319', 'SCHEDULED', 130, 98),
('UA1308', 'United Airlines', 'JFK', 'IAH', '2024-01-20 21:15:00', '2024-01-21 01:45:00', 'Boeing 737-800', 'SCHEDULED', 170, 145),
('SW1409', 'Southwest Airlines', 'JFK', 'LAS', '2024-01-20 22:30:00', '2024-01-21 02:15:00', 'Boeing 737-800', 'SCHEDULED', 175, 156),
('DL1510', 'Delta Airlines', 'JFK', 'SEA', '2024-01-20 23:45:00', '2024-01-21 03:30:00', 'Airbus A320', 'SCHEDULED', 180, 134);

-- =====================================================
-- 3. POPULATE BOOKINGS TABLE
-- =====================================================
INSERT INTO bookings (booking_id, flight_number, passenger_name, passenger_id, seat_number, booking_date, check_in_status, ticket_class, special_requests) VALUES
-- DL101 Bookings
('BK001', 'DL101', 'John Smith', 'P123456789', '12A', '2024-01-15 10:30:00', 'CHECKED_IN', 'ECONOMY', 'Window seat preferred'),
('BK002', 'DL101', 'Sarah Johnson', 'P987654321', '12B', '2024-01-15 11:15:00', 'CHECKED_IN', 'ECONOMY', 'Aisle seat preferred'),
('BK003', 'DL101', 'Michael Brown', 'P456789123', '15C', '2024-01-15 14:20:00', 'CHECKED_IN', 'BUSINESS', 'Vegetarian meal'),
('BK004', 'DL101', 'Emily Davis', 'P789123456', '15D', '2024-01-15 16:45:00', 'CHECKED_IN', 'BUSINESS', 'Wheelchair assistance'),
('BK005', 'DL101', 'David Wilson', 'P321654987', '18F', '2024-01-15 18:30:00', 'PENDING', 'ECONOMY', NULL),

-- AA202 Bookings
('BK006', 'AA202', 'Lisa Anderson', 'P147258369', '8A', '2024-01-16 09:00:00', 'CHECKED_IN', 'FIRST', 'Extra legroom'),
('BK007', 'AA202', 'Robert Taylor', 'P963852741', '8B', '2024-01-16 10:30:00', 'CHECKED_IN', 'FIRST', 'Vegetarian meal'),
('BK008', 'AA202', 'Jennifer Martinez', 'P852963741', '12C', '2024-01-16 12:15:00', 'CHECKED_IN', 'ECONOMY', 'Window seat'),
('BK009', 'AA202', 'Christopher Lee', 'P741852963', '12D', '2024-01-16 13:45:00', 'CHECKED_IN', 'ECONOMY', 'Aisle seat'),
('BK010', 'AA202', 'Amanda Garcia', 'P369258147', '16F', '2024-01-16 15:20:00', 'PENDING', 'ECONOMY', NULL),

-- UA303 Bookings
('BK011', 'UA303', 'Daniel Rodriguez', 'P258147369', '5A', '2024-01-16 08:30:00', 'CHECKED_IN', 'BUSINESS', 'Kosher meal'),
('BK012', 'UA303', 'Jessica Lopez', 'P147369258', '5B', '2024-01-16 09:45:00', 'CHECKED_IN', 'BUSINESS', 'Vegetarian meal'),
('BK013', 'UA303', 'Matthew White', 'P963147258', '10C', '2024-01-16 11:20:00', 'CHECKED_IN', 'ECONOMY', 'Window seat'),
('BK014', 'UA303', 'Nicole Clark', 'P852147963', '10D', '2024-01-16 12:40:00', 'CHECKED_IN', 'ECONOMY', 'Aisle seat'),
('BK015', 'UA303', 'Kevin Hall', 'P741963852', '14F', '2024-01-16 14:15:00', 'PENDING', 'ECONOMY', NULL),

-- BA601 International Bookings
('BK016', 'BA601', 'Sophie Thompson', 'P369741852', '2A', '2024-01-17 10:00:00', 'CHECKED_IN', 'FIRST', 'Champagne service'),
('BK017', 'BA601', 'James Moore', 'P258963741', '2B', '2024-01-17 11:30:00', 'CHECKED_IN', 'FIRST', 'Vegetarian meal'),
('BK018', 'BA601', 'Emma Jackson', 'P147852963', '8C', '2024-01-17 13:00:00', 'CHECKED_IN', 'BUSINESS', 'Window seat'),
('BK019', 'BA601', 'William Martin', 'P963741852', '8D', '2024-01-17 14:30:00', 'CHECKED_IN', 'BUSINESS', 'Aisle seat'),
('BK020', 'BA601', 'Olivia Lee', 'P852369741', '15F', '2024-01-17 16:00:00', 'PENDING', 'ECONOMY', NULL),

-- AF702 International Bookings
('BK021', 'AF702', 'Lucas Garcia', 'P741258963', '3A', '2024-01-17 09:15:00', 'CHECKED_IN', 'FIRST', 'French wine selection'),
('BK022', 'AF702', 'Isabella Rodriguez', 'P369852147', '3B', '2024-01-17 10:45:00', 'CHECKED_IN', 'FIRST', 'Vegetarian meal'),
('BK023', 'AF702', 'Ethan Martinez', 'P258741963', '10C', '2024-01-17 12:15:00', 'CHECKED_IN', 'BUSINESS', 'Window seat'),
('BK024', 'AF702', 'Mia Johnson', 'P147963852', '10D', '2024-01-17 13:45:00', 'CHECKED_IN', 'BUSINESS', 'Aisle seat'),
('BK025', 'AF702', 'Noah Davis', 'P963258741', '18F', '2024-01-17 15:15:00', 'PENDING', 'ECONOMY', NULL);

-- =====================================================
-- 4. POPULATE BAGGAGE TABLE
-- =====================================================
INSERT INTO baggage (baggage_id, booking_id, passenger_name, flight_number, baggage_type, weight, status, check_in_time, location) VALUES
-- DL101 Baggage
('BG001', 'BK001', 'John Smith', 'DL101', 'CHECKED', 22.5, 'LOADED', '2024-01-20 06:30:00', 'CARGO_HOLD'),
('BG002', 'BK001', 'John Smith', 'DL101', 'CARRY_ON', 8.0, 'SECURED', '2024-01-20 06:30:00', 'CABIN'),
('BG003', 'BK002', 'Sarah Johnson', 'DL101', 'CHECKED', 18.7, 'LOADED', '2024-01-20 06:45:00', 'CARGO_HOLD'),
('BG004', 'BK002', 'Sarah Johnson', 'DL101', 'CARRY_ON', 6.5, 'SECURED', '2024-01-20 06:45:00', 'CABIN'),
('BG005', 'BK003', 'Michael Brown', 'DL101', 'CHECKED', 25.3, 'LOADED', '2024-01-20 07:00:00', 'CARGO_HOLD'),
('BG006', 'BK003', 'Michael Brown', 'DL101', 'CARRY_ON', 7.8, 'SECURED', '2024-01-20 07:00:00', 'CABIN'),
('BG007', 'BK004', 'Emily Davis', 'DL101', 'CHECKED', 15.2, 'LOADED', '2024-01-20 07:15:00', 'CARGO_HOLD'),
('BG008', 'BK004', 'Emily Davis', 'DL101', 'CARRY_ON', 5.5, 'SECURED', '2024-01-20 07:15:00', 'CABIN'),

-- AA202 Baggage
('BG009', 'BK006', 'Lisa Anderson', 'AA202', 'CHECKED', 28.1, 'LOADED', '2024-01-20 07:30:00', 'CARGO_HOLD'),
('BG010', 'BK006', 'Lisa Anderson', 'AA202', 'CARRY_ON', 9.2, 'SECURED', '2024-01-20 07:30:00', 'CABIN'),
('BG011', 'BK007', 'Robert Taylor', 'AA202', 'CHECKED', 20.8, 'LOADED', '2024-01-20 07:45:00', 'CARGO_HOLD'),
('BG012', 'BK007', 'Robert Taylor', 'AA202', 'CARRY_ON', 6.8, 'SECURED', '2024-01-20 07:45:00', 'CABIN'),
('BG013', 'BK008', 'Jennifer Martinez', 'AA202', 'CHECKED', 16.5, 'LOADED', '2024-01-20 08:00:00', 'CARGO_HOLD'),
('BG014', 'BK008', 'Jennifer Martinez', 'AA202', 'CARRY_ON', 7.1, 'SECURED', '2024-01-20 08:00:00', 'CABIN'),
('BG015', 'BK009', 'Christopher Lee', 'AA202', 'CHECKED', 19.3, 'LOADED', '2024-01-20 08:15:00', 'CARGO_HOLD'),
('BG016', 'BK009', 'Christopher Lee', 'AA202', 'CARRY_ON', 5.9, 'SECURED', '2024-01-20 08:15:00', 'CABIN'),

-- UA303 Baggage
('BG017', 'BK011', 'Daniel Rodriguez', 'UA303', 'CHECKED', 24.7, 'LOADED', '2024-01-20 08:30:00', 'CARGO_HOLD'),
('BG018', 'BK011', 'Daniel Rodriguez', 'UA303', 'CARRY_ON', 8.5, 'SECURED', '2024-01-20 08:30:00', 'CABIN'),
('BG019', 'BK012', 'Jessica Lopez', 'UA303', 'CHECKED', 17.8, 'LOADED', '2024-01-20 08:45:00', 'CARGO_HOLD'),
('BG020', 'BK012', 'Jessica Lopez', 'UA303', 'CARRY_ON', 6.2, 'SECURED', '2024-01-20 08:45:00', 'CABIN'),
('BG021', 'BK013', 'Matthew White', 'UA303', 'CHECKED', 21.4, 'LOADED', '2024-01-20 09:00:00', 'CARGO_HOLD'),
('BG022', 'BK013', 'Matthew White', 'UA303', 'CARRY_ON', 7.3, 'SECURED', '2024-01-20 09:00:00', 'CABIN'),
('BG023', 'BK014', 'Nicole Clark', 'UA303', 'CHECKED', 18.9, 'LOADED', '2024-01-20 09:15:00', 'CARGO_HOLD'),
('BG024', 'BK014', 'Nicole Clark', 'UA303', 'CARRY_ON', 5.7, 'SECURED', '2024-01-20 09:15:00', 'CABIN'),

-- BA601 International Baggage
('BG025', 'BK016', 'Sophie Thompson', 'BA601', 'CHECKED', 30.2, 'LOADED', '2024-01-20 11:00:00', 'CARGO_HOLD'),
('BG026', 'BK016', 'Sophie Thompson', 'BA601', 'CARRY_ON', 10.1, 'SECURED', '2024-01-20 11:00:00', 'CABIN'),
('BG027', 'BK017', 'James Moore', 'BA601', 'CHECKED', 26.8, 'LOADED', '2024-01-20 11:15:00', 'CARGO_HOLD'),
('BG028', 'BK017', 'James Moore', 'BA601', 'CARRY_ON', 8.9, 'SECURED', '2024-01-20 11:15:00', 'CABIN'),
('BG029', 'BK018', 'Emma Jackson', 'BA601', 'CHECKED', 23.5, 'LOADED', '2024-01-20 11:30:00', 'CARGO_HOLD'),
('BG030', 'BK018', 'Emma Jackson', 'BA601', 'CARRY_ON', 7.6, 'SECURED', '2024-01-20 11:30:00', 'CABIN'),

-- AF702 International Baggage
('BG031', 'BK021', 'Lucas Garcia', 'AF702', 'CHECKED', 29.1, 'LOADED', '2024-01-20 12:00:00', 'CARGO_HOLD'),
('BG032', 'BK021', 'Lucas Garcia', 'AF702', 'CARRY_ON', 9.8, 'SECURED', '2024-01-20 12:00:00', 'CABIN'),
('BG033', 'BK022', 'Isabella Rodriguez', 'AF702', 'CHECKED', 25.6, 'LOADED', '2024-01-20 12:15:00', 'CARGO_HOLD'),
('BG034', 'BK022', 'Isabella Rodriguez', 'AF702', 'CARRY_ON', 8.3, 'SECURED', '2024-01-20 12:15:00', 'CABIN'),
('BG035', 'BK023', 'Ethan Martinez', 'AF702', 'CHECKED', 22.9, 'LOADED', '2024-01-20 12:30:00', 'CARGO_HOLD'),
('BG036', 'BK023', 'Ethan Martinez', 'AF702', 'CARRY_ON', 7.4, 'SECURED', '2024-01-20 12:30:00', 'CABIN');

-- =====================================================
-- 5. POPULATE GATE ASSIGNMENTS TABLE
-- =====================================================
INSERT INTO gate_assignments (assignment_id, flight_number, gate_number, assignment_time, status, notes) VALUES
-- Morning Flights
('GA001', 'DL101', 'A1', '2024-01-20 06:00:00', 'ASSIGNED', 'Domestic flight - Terminal A'),
('GA002', 'AA202', 'A2', '2024-01-20 06:15:00', 'ASSIGNED', 'Domestic flight - Terminal A'),
('GA003', 'UA303', 'B1', '2024-01-20 06:30:00', 'ASSIGNED', 'Domestic flight - Terminal B'),
('GA004', 'SW404', 'B2', '2024-01-20 06:45:00', 'ASSIGNED', 'Domestic flight - Terminal B'),
('GA005', 'DL505', 'A4', '2024-01-20 07:00:00', 'ASSIGNED', 'Domestic flight - Terminal A'),

-- International Flights
('GA006', 'BA601', 'C1', '2024-01-20 11:00:00', 'ASSIGNED', 'International flight - Terminal C'),
('GA007', 'AF702', 'C2', '2024-01-20 12:00:00', 'ASSIGNED', 'International flight - Terminal C'),
('GA008', 'LH803', 'C3', '2024-01-20 13:00:00', 'ASSIGNED', 'International flight - Terminal C'),
('GA009', 'JL904', 'C4', '2024-01-20 14:00:00', 'ASSIGNED', 'International flight - Terminal C'),
('GA010', 'EK1005', 'C1', '2024-01-20 15:00:00', 'ASSIGNED', 'International flight - Terminal C'),

-- Evening Flights
('GA011', 'DL1106', 'B3', '2024-01-20 17:00:00', 'ASSIGNED', 'Domestic flight - Terminal B'),
('GA012', 'AA1207', 'B4', '2024-01-20 17:15:00', 'ASSIGNED', 'Domestic flight - Terminal B'),
('GA013', 'UA1308', 'A1', '2024-01-20 18:00:00', 'ASSIGNED', 'Domestic flight - Terminal A'),
('GA014', 'SW1409', 'A2', '2024-01-20 18:15:00', 'ASSIGNED', 'Domestic flight - Terminal A'),
('GA015', 'DL1510', 'A3', '2024-01-20 18:30:00', 'ASSIGNED', 'Domestic flight - Terminal A');

-- =====================================================
-- 6. POPULATE SYSTEM LOGS TABLE (Optional - for monitoring)
-- =====================================================
INSERT INTO system_logs (log_id, timestamp, level, component, message, user_id) VALUES
('LOG001', '2024-01-20 06:00:00', 'INFO', 'GATE_MANAGEMENT', 'Gate A1 assigned to flight DL101', 'staff1'),
('LOG002', '2024-01-20 06:15:00', 'INFO', 'GATE_MANAGEMENT', 'Gate A2 assigned to flight AA202', 'staff1'),
('LOG003', '2024-01-20 06:30:00', 'INFO', 'GATE_MANAGEMENT', 'Gate B1 assigned to flight UA303', 'staff2'),
('LOG004', '2024-01-20 06:45:00', 'INFO', 'GATE_MANAGEMENT', 'Gate B2 assigned to flight SW404', 'staff2'),
('LOG005', '2024-01-20 07:00:00', 'INFO', 'GATE_MANAGEMENT', 'Gate A4 assigned to flight DL505', 'staff1'),
('LOG006', '2024-01-20 07:30:00', 'INFO', 'CHECK_IN', 'Passenger John Smith checked in for flight DL101', 'staff1'),
('LOG007', '2024-01-20 07:45:00', 'INFO', 'CHECK_IN', 'Passenger Sarah Johnson checked in for flight DL101', 'staff1'),
('LOG008', '2024-01-20 08:00:00', 'INFO', 'CHECK_IN', 'Passenger Michael Brown checked in for flight DL101', 'staff2'),
('LOG009', '2024-01-20 08:15:00', 'INFO', 'CHECK_IN', 'Passenger Emily Davis checked in for flight DL101', 'staff2'),
('LOG010', '2024-01-20 08:30:00', 'INFO', 'CHECK_IN', 'Passenger Lisa Anderson checked in for flight AA202', 'staff1'),
('LOG011', '2024-01-20 08:45:00', 'INFO', 'CHECK_IN', 'Passenger Robert Taylor checked in for flight AA202', 'staff1'),
('LOG012', '2024-01-20 09:00:00', 'INFO', 'CHECK_IN', 'Passenger Jennifer Martinez checked in for flight AA202', 'staff2'),
('LOG013', '2024-01-20 09:15:00', 'INFO', 'CHECK_IN', 'Passenger Christopher Lee checked in for flight AA202', 'staff2'),
('LOG014', '2024-01-20 09:30:00', 'INFO', 'BAGGAGE', 'Baggage BG001 loaded for flight DL101', 'staff1'),
('LOG015', '2024-01-20 09:45:00', 'INFO', 'BAGGAGE', 'Baggage BG002 secured for flight DL101', 'staff1'),
('LOG016', '2024-01-20 10:00:00', 'INFO', 'BAGGAGE', 'Baggage BG003 loaded for flight DL101', 'staff2'),
('LOG017', '2024-01-20 10:15:00', 'INFO', 'BAGGAGE', 'Baggage BG004 secured for flight DL101', 'staff2'),
('LOG018', '2024-01-20 11:00:00', 'INFO', 'GATE_MANAGEMENT', 'Gate C1 assigned to flight BA601', 'staff1'),
('LOG019', '2024-01-20 11:15:00', 'INFO', 'CHECK_IN', 'Passenger Sophie Thompson checked in for flight BA601', 'staff1'),
('LOG020', '2024-01-20 11:30:00', 'INFO', 'CHECK_IN', 'Passenger James Moore checked in for flight BA601', 'staff1');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Display summary of populated data
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

-- Display flight summary
SELECT 
    flight_number,
    airline,
    origin,
    destination,
    departure_time,
    status,
    capacity,
    booked_seats,
    ROUND((booked_seats / capacity) * 100, 1) as occupancy_percentage
FROM flights
ORDER BY departure_time;

-- Display gate assignments
SELECT 
    ga.assignment_id,
    ga.flight_number,
    f.airline,
    ga.gate_number,
    ga.assignment_time,
    ga.status
FROM gate_assignments ga
JOIN flights f ON ga.flight_number = f.flight_number
ORDER BY ga.assignment_time;

-- Display booking summary by flight
SELECT 
    f.flight_number,
    f.airline,
    COUNT(b.booking_id) as total_bookings,
    COUNT(CASE WHEN b.check_in_status = 'CHECKED_IN' THEN 1 END) as checked_in,
    COUNT(CASE WHEN b.check_in_status = 'PENDING' THEN 1 END) as pending
FROM flights f
LEFT JOIN bookings b ON f.flight_number = b.flight_number
GROUP BY f.flight_number, f.airline
ORDER BY f.departure_time;

-- Display baggage summary
SELECT 
    b.flight_number,
    COUNT(bg.baggage_id) as total_bags,
    COUNT(CASE WHEN bg.baggage_type = 'CHECKED' THEN 1 END) as checked_bags,
    COUNT(CASE WHEN bg.baggage_type = 'CARRY_ON' THEN 1 END) as carry_on_bags,
    ROUND(AVG(bg.weight), 1) as avg_weight_kg
FROM baggage bg
JOIN bookings b ON bg.booking_id = b.booking_id
GROUP BY b.flight_number
ORDER BY b.flight_number;

-- =====================================================
-- SCRIPT COMPLETION MESSAGE
-- =====================================================
SELECT 'Database population completed successfully!' as status_message; 