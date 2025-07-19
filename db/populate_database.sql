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
INSERT INTO gates (gate_number, terminal, status) VALUES
('A1', 'A', 'AVAILABLE'),
('A2', 'A', 'AVAILABLE'),
('A3', 'A', 'MAINTENANCE'),
('A4', 'A', 'AVAILABLE'),
('B1', 'B', 'AVAILABLE'),
('B2', 'B', 'AVAILABLE'),
('B3', 'B', 'AVAILABLE'),
('B4', 'B', 'MAINTENANCE'),
('C1', 'C', 'AVAILABLE'),
('C2', 'C', 'AVAILABLE'),
('C3', 'C', 'AVAILABLE'),
('C4', 'C', 'AVAILABLE');

-- =====================================================
-- 2. POPULATE FLIGHTS TABLE
-- =====================================================
INSERT INTO flights (flight_number, origin, destination, departure_time, arrival_time, aircraft_type, status) VALUES
-- Domestic Flights
('DL101', 'JFK', 'LAX', '2024-01-20 08:00:00', '2024-01-20 11:30:00', 'Boeing 737-800', 'SCHEDULED'),
('AA202', 'JFK', 'ORD', '2024-01-20 09:15:00', '2024-01-20 11:45:00', 'Airbus A320', 'SCHEDULED'),
('UA303', 'JFK', 'SFO', '2024-01-20 10:30:00', '2024-01-20 14:15:00', 'Boeing 757-200', 'SCHEDULED'),
('SW404', 'JFK', 'DEN', '2024-01-20 11:45:00', '2024-01-20 14:30:00', 'Boeing 737-700', 'SCHEDULED'),
('DL505', 'JFK', 'ATL', '2024-01-20 12:00:00', '2024-01-20 14:45:00', 'Airbus A321', 'SCHEDULED'),

-- International Flights
('BA601', 'JFK', 'LHR', '2024-01-20 13:30:00', '2024-01-21 02:15:00', 'Boeing 777-300ER', 'SCHEDULED'),
('AF702', 'JFK', 'CDG', '2024-01-20 14:45:00', '2024-01-21 04:30:00', 'Airbus A350-900', 'SCHEDULED'),
('LH803', 'JFK', 'FRA', '2024-01-20 16:00:00', '2024-01-21 06:45:00', 'Boeing 747-8', 'SCHEDULED'),
('JL904', 'JFK', 'NRT', '2024-01-20 17:15:00', '2024-01-21 20:30:00', 'Boeing 787-9', 'SCHEDULED'),
('EK1005', 'JFK', 'DXB', '2024-01-20 18:30:00', '2024-01-21 16:45:00', 'Airbus A380-800', 'SCHEDULED'),

-- Evening Flights
('DL1106', 'JFK', 'MIA', '2024-01-20 19:45:00', '2024-01-20 23:15:00', 'Boeing 737-900', 'SCHEDULED'),
('AA1207', 'JFK', 'DFW', '2024-01-20 20:00:00', '2024-01-20 23:30:00', 'Airbus A319', 'SCHEDULED'),
('UA1308', 'JFK', 'IAH', '2024-01-20 21:15:00', '2024-01-21 01:45:00', 'Boeing 737-800', 'SCHEDULED'),
('SW1409', 'JFK', 'LAS', '2024-01-20 22:30:00', '2024-01-21 02:15:00', 'Boeing 737-800', 'SCHEDULED'),
('DL1510', 'JFK', 'SEA', '2024-01-20 23:45:00', '2024-01-21 03:30:00', 'Airbus A320', 'SCHEDULED');

-- =====================================================
-- 3. POPULATE BOOKINGS TABLE
-- =====================================================
INSERT INTO bookings (booking_reference, passenger_name, flight_id, seat_number, check_in_status) VALUES
-- DL101 Bookings (flight_id = 1)
('BK001', 'John Smith', 1, '12A', 'CHECKED_IN'),
('BK002', 'Sarah Johnson', 1, '12B', 'CHECKED_IN'),
('BK003', 'Michael Brown', 1, '15C', 'CHECKED_IN'),
('BK004', 'Emily Davis', 1, '15D', 'CHECKED_IN'),
('BK005', 'David Wilson', 1, '18F', 'NOT_CHECKED_IN'),

-- AA202 Bookings (flight_id = 2)
('BK006', 'Lisa Anderson', 2, '8A', 'CHECKED_IN'),
('BK007', 'Robert Taylor', 2, '8B', 'CHECKED_IN'),
('BK008', 'Jennifer Martinez', 2, '12C', 'CHECKED_IN'),
('BK009', 'Christopher Lee', 2, '12D', 'CHECKED_IN'),
('BK010', 'Amanda Garcia', 2, '16F', 'NOT_CHECKED_IN'),

-- UA303 Bookings (flight_id = 3)
('BK011', 'Daniel Rodriguez', 3, '5A', 'CHECKED_IN'),
('BK012', 'Jessica Lopez', 3, '5B', 'CHECKED_IN'),
('BK013', 'Matthew White', 3, '10C', 'CHECKED_IN'),
('BK014', 'Nicole Clark', 3, '10D', 'CHECKED_IN'),
('BK015', 'Kevin Hall', 3, '14F', 'NOT_CHECKED_IN'),

-- BA601 International Bookings (flight_id = 6)
('BK016', 'Sophie Thompson', 6, '2A', 'CHECKED_IN'),
('BK017', 'James Moore', 6, '2B', 'CHECKED_IN'),
('BK018', 'Emma Jackson', 6, '8C', 'CHECKED_IN'),
('BK019', 'William Martin', 6, '8D', 'CHECKED_IN'),
('BK020', 'Olivia Lee', 6, '15F', 'NOT_CHECKED_IN'),

-- AF702 International Bookings (flight_id = 7)
('BK021', 'Lucas Garcia', 7, '3A', 'CHECKED_IN'),
('BK022', 'Isabella Rodriguez', 7, '3B', 'CHECKED_IN'),
('BK023', 'Ethan Martinez', 7, '10C', 'CHECKED_IN'),
('BK024', 'Mia Johnson', 7, '10D', 'CHECKED_IN'),
('BK025', 'Noah Davis', 7, '18F', 'NOT_CHECKED_IN');

-- =====================================================
-- 4. POPULATE BAGGAGE TABLE
-- =====================================================
INSERT INTO baggage (baggage_tag, booking_id, weight, status) VALUES
-- DL101 Baggage
('BG001', 1, 22.5, 'LOADED'),
('BG002', 2, 18.7, 'LOADED'),
('BG003', 3, 25.3, 'LOADED'),
('BG004', 4, 15.2, 'LOADED'),
('BG005', 5, 20.1, 'CHECKED_IN'),

-- AA202 Baggage
('BG006', 6, 28.1, 'LOADED'),
('BG007', 7, 20.8, 'LOADED'),
('BG008', 8, 16.5, 'LOADED'),
('BG009', 9, 19.3, 'LOADED'),
('BG010', 10, 17.8, 'CHECKED_IN'),

-- UA303 Baggage
('BG011', 11, 24.7, 'LOADED'),
('BG012', 12, 17.8, 'LOADED'),
('BG013', 13, 21.4, 'LOADED'),
('BG014', 14, 18.9, 'LOADED'),
('BG015', 15, 16.2, 'CHECKED_IN'),

-- BA601 International Baggage
('BG016', 16, 30.2, 'LOADED'),
('BG017', 17, 26.8, 'LOADED'),
('BG018', 18, 23.5, 'LOADED'),
('BG019', 19, 27.1, 'LOADED'),
('BG020', 20, 19.6, 'CHECKED_IN'),

-- AF702 International Baggage
('BG021', 21, 29.1, 'LOADED'),
('BG022', 22, 25.6, 'LOADED'),
('BG023', 23, 22.9, 'LOADED'),
('BG024', 24, 24.3, 'LOADED'),
('BG025', 25, 18.7, 'CHECKED_IN');

-- =====================================================
-- 5. POPULATE GATE ASSIGNMENTS TABLE
-- =====================================================
INSERT INTO gate_assignments (gate_id, flight_id, assignment_time, departure_time) VALUES
-- Morning Flights
(1, 1, '2024-01-20 06:00:00', '2024-01-20 08:00:00'),  -- Gate A1, Flight DL101
(2, 2, '2024-01-20 06:15:00', '2024-01-20 09:15:00'),  -- Gate A2, Flight AA202
(5, 3, '2024-01-20 06:30:00', '2024-01-20 10:30:00'),  -- Gate B1, Flight UA303
(6, 4, '2024-01-20 06:45:00', '2024-01-20 11:45:00'),  -- Gate B2, Flight SW404
(4, 5, '2024-01-20 07:00:00', '2024-01-20 12:00:00'),  -- Gate A4, Flight DL505

-- International Flights
(9, 6, '2024-01-20 11:00:00', '2024-01-20 13:30:00'),  -- Gate C1, Flight BA601
(10, 7, '2024-01-20 12:00:00', '2024-01-20 14:45:00'), -- Gate C2, Flight AF702
(11, 8, '2024-01-20 13:00:00', '2024-01-20 16:00:00'), -- Gate C3, Flight LH803
(12, 9, '2024-01-20 14:00:00', '2024-01-20 17:15:00'), -- Gate C4, Flight JL904
(9, 10, '2024-01-20 15:00:00', '2024-01-20 18:30:00'), -- Gate C1, Flight EK1005

-- Evening Flights
(7, 11, '2024-01-20 17:00:00', '2024-01-20 19:45:00'), -- Gate B3, Flight DL1106
(8, 12, '2024-01-20 17:15:00', '2024-01-20 20:00:00'), -- Gate B4, Flight AA1207
(1, 13, '2024-01-20 18:00:00', '2024-01-20 21:15:00'), -- Gate A1, Flight UA1308
(2, 14, '2024-01-20 18:15:00', '2024-01-20 22:30:00'), -- Gate A2, Flight SW1409
(3, 15, '2024-01-20 18:30:00', '2024-01-20 23:45:00'); -- Gate A3, Flight DL1510

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
SELECT 'GATE_ASSIGNMENTS', COUNT(*) FROM gate_assignments;

-- Display flight summary
SELECT 
    flight_number,
    origin,
    destination,
    departure_time,
    status
FROM flights
ORDER BY departure_time;

-- Display gate assignments with flight info
SELECT 
    ga.id as assignment_id,
    f.flight_number,
    g.gate_number,
    ga.assignment_time,
    ga.departure_time
FROM gate_assignments ga
JOIN flights f ON ga.flight_id = f.id
JOIN gates g ON ga.gate_id = g.id
ORDER BY ga.assignment_time;

-- Display booking summary by flight
SELECT 
    f.flight_number,
    COUNT(b.id) as total_bookings,
    COUNT(CASE WHEN b.check_in_status = 'CHECKED_IN' THEN 1 END) as checked_in,
    COUNT(CASE WHEN b.check_in_status = 'NOT_CHECKED_IN' THEN 1 END) as pending
FROM flights f
LEFT JOIN bookings b ON f.id = b.flight_id
GROUP BY f.id, f.flight_number
ORDER BY f.departure_time;

-- Display baggage summary
SELECT 
    f.flight_number,
    COUNT(bg.id) as total_bags,
    COUNT(CASE WHEN bg.status = 'LOADED' THEN 1 END) as loaded_bags,
    COUNT(CASE WHEN bg.status = 'CHECKED_IN' THEN 1 END) as checked_in_bags,
    ROUND(AVG(bg.weight), 1) as avg_weight_kg
FROM baggage bg
JOIN bookings b ON bg.booking_id = b.id
JOIN flights f ON b.flight_id = f.id
GROUP BY f.id, f.flight_number
ORDER BY f.flight_number;

-- =====================================================
-- SCRIPT COMPLETION MESSAGE
-- =====================================================
SELECT 'Database population completed successfully!' as status_message; 