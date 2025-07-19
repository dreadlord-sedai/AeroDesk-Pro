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
-- 3. POPULATE BOOKINGS TABLE (Using subqueries to get correct flight IDs)
-- =====================================================
INSERT INTO bookings (booking_reference, passenger_name, flight_id, seat_number, check_in_status) VALUES
-- DL101 Bookings
('BK001', 'John Smith', (SELECT id FROM flights WHERE flight_number = 'DL101'), '12A', 'CHECKED_IN'),
('BK002', 'Sarah Johnson', (SELECT id FROM flights WHERE flight_number = 'DL101'), '12B', 'CHECKED_IN'),
('BK003', 'Michael Brown', (SELECT id FROM flights WHERE flight_number = 'DL101'), '15C', 'CHECKED_IN'),
('BK004', 'Emily Davis', (SELECT id FROM flights WHERE flight_number = 'DL101'), '15D', 'CHECKED_IN'),
('BK005', 'David Wilson', (SELECT id FROM flights WHERE flight_number = 'DL101'), '18F', 'NOT_CHECKED_IN'),

-- AA202 Bookings
('BK006', 'Lisa Anderson', (SELECT id FROM flights WHERE flight_number = 'AA202'), '8A', 'CHECKED_IN'),
('BK007', 'Robert Taylor', (SELECT id FROM flights WHERE flight_number = 'AA202'), '8B', 'CHECKED_IN'),
('BK008', 'Jennifer Martinez', (SELECT id FROM flights WHERE flight_number = 'AA202'), '12C', 'CHECKED_IN'),
('BK009', 'Christopher Lee', (SELECT id FROM flights WHERE flight_number = 'AA202'), '12D', 'CHECKED_IN'),
('BK010', 'Amanda Garcia', (SELECT id FROM flights WHERE flight_number = 'AA202'), '16F', 'NOT_CHECKED_IN'),

-- UA303 Bookings
('BK011', 'Daniel Rodriguez', (SELECT id FROM flights WHERE flight_number = 'UA303'), '5A', 'CHECKED_IN'),
('BK012', 'Jessica Lopez', (SELECT id FROM flights WHERE flight_number = 'UA303'), '5B', 'CHECKED_IN'),
('BK013', 'Matthew White', (SELECT id FROM flights WHERE flight_number = 'UA303'), '10C', 'CHECKED_IN'),
('BK014', 'Nicole Clark', (SELECT id FROM flights WHERE flight_number = 'UA303'), '10D', 'CHECKED_IN'),
('BK015', 'Kevin Hall', (SELECT id FROM flights WHERE flight_number = 'UA303'), '14F', 'NOT_CHECKED_IN'),

-- BA601 International Bookings
('BK016', 'Sophie Thompson', (SELECT id FROM flights WHERE flight_number = 'BA601'), '2A', 'CHECKED_IN'),
('BK017', 'James Moore', (SELECT id FROM flights WHERE flight_number = 'BA601'), '2B', 'CHECKED_IN'),
('BK018', 'Emma Jackson', (SELECT id FROM flights WHERE flight_number = 'BA601'), '8C', 'CHECKED_IN'),
('BK019', 'William Martin', (SELECT id FROM flights WHERE flight_number = 'BA601'), '8D', 'CHECKED_IN'),
('BK020', 'Olivia Lee', (SELECT id FROM flights WHERE flight_number = 'BA601'), '15F', 'NOT_CHECKED_IN'),

-- AF702 International Bookings
('BK021', 'Lucas Garcia', (SELECT id FROM flights WHERE flight_number = 'AF702'), '3A', 'CHECKED_IN'),
('BK022', 'Isabella Rodriguez', (SELECT id FROM flights WHERE flight_number = 'AF702'), '3B', 'CHECKED_IN'),
('BK023', 'Ethan Martinez', (SELECT id FROM flights WHERE flight_number = 'AF702'), '10C', 'CHECKED_IN'),
('BK024', 'Mia Johnson', (SELECT id FROM flights WHERE flight_number = 'AF702'), '10D', 'CHECKED_IN'),
('BK025', 'Noah Davis', (SELECT id FROM flights WHERE flight_number = 'AF702'), '18F', 'NOT_CHECKED_IN');

-- =====================================================
-- 4. POPULATE BAGGAGE TABLE (Using subqueries to get correct booking IDs)
-- =====================================================
INSERT INTO baggage (baggage_tag, booking_id, weight, status) VALUES
-- DL101 Baggage
('BG001', (SELECT id FROM bookings WHERE booking_reference = 'BK001'), 22.5, 'LOADED'),
('BG002', (SELECT id FROM bookings WHERE booking_reference = 'BK002'), 18.7, 'LOADED'),
('BG003', (SELECT id FROM bookings WHERE booking_reference = 'BK003'), 25.3, 'LOADED'),
('BG004', (SELECT id FROM bookings WHERE booking_reference = 'BK004'), 15.2, 'LOADED'),
('BG005', (SELECT id FROM bookings WHERE booking_reference = 'BK005'), 20.1, 'CHECKED_IN'),

-- AA202 Baggage
('BG006', (SELECT id FROM bookings WHERE booking_reference = 'BK006'), 28.1, 'LOADED'),
('BG007', (SELECT id FROM bookings WHERE booking_reference = 'BK007'), 20.8, 'LOADED'),
('BG008', (SELECT id FROM bookings WHERE booking_reference = 'BK008'), 16.5, 'LOADED'),
('BG009', (SELECT id FROM bookings WHERE booking_reference = 'BK009'), 19.3, 'LOADED'),
('BG010', (SELECT id FROM bookings WHERE booking_reference = 'BK010'), 17.8, 'CHECKED_IN'),

-- UA303 Baggage
('BG011', (SELECT id FROM bookings WHERE booking_reference = 'BK011'), 24.7, 'LOADED'),
('BG012', (SELECT id FROM bookings WHERE booking_reference = 'BK012'), 17.8, 'LOADED'),
('BG013', (SELECT id FROM bookings WHERE booking_reference = 'BK013'), 21.4, 'LOADED'),
('BG014', (SELECT id FROM bookings WHERE booking_reference = 'BK014'), 18.9, 'LOADED'),
('BG015', (SELECT id FROM bookings WHERE booking_reference = 'BK015'), 16.2, 'CHECKED_IN'),

-- BA601 International Baggage
('BG016', (SELECT id FROM bookings WHERE booking_reference = 'BK016'), 30.2, 'LOADED'),
('BG017', (SELECT id FROM bookings WHERE booking_reference = 'BK017'), 26.8, 'LOADED'),
('BG018', (SELECT id FROM bookings WHERE booking_reference = 'BK018'), 23.5, 'LOADED'),
('BG019', (SELECT id FROM bookings WHERE booking_reference = 'BK019'), 27.1, 'LOADED'),
('BG020', (SELECT id FROM bookings WHERE booking_reference = 'BK020'), 19.6, 'CHECKED_IN'),

-- AF702 International Baggage
('BG021', (SELECT id FROM bookings WHERE booking_reference = 'BK021'), 29.1, 'LOADED'),
('BG022', (SELECT id FROM bookings WHERE booking_reference = 'BK022'), 25.6, 'LOADED'),
('BG023', (SELECT id FROM bookings WHERE booking_reference = 'BK023'), 22.9, 'LOADED'),
('BG024', (SELECT id FROM bookings WHERE booking_reference = 'BK024'), 24.3, 'LOADED'),
('BG025', (SELECT id FROM bookings WHERE booking_reference = 'BK025'), 18.7, 'CHECKED_IN');

-- =====================================================
-- 5. POPULATE GATE ASSIGNMENTS TABLE (Using subqueries to get correct IDs)
-- =====================================================
INSERT INTO gate_assignments (gate_id, flight_id, assignment_time, departure_time) VALUES
-- Morning Flights
((SELECT id FROM gates WHERE gate_number = 'A1'), (SELECT id FROM flights WHERE flight_number = 'DL101'), '2024-01-20 06:00:00', '2024-01-20 08:00:00'),
((SELECT id FROM gates WHERE gate_number = 'A2'), (SELECT id FROM flights WHERE flight_number = 'AA202'), '2024-01-20 06:15:00', '2024-01-20 09:15:00'),
((SELECT id FROM gates WHERE gate_number = 'B1'), (SELECT id FROM flights WHERE flight_number = 'UA303'), '2024-01-20 06:30:00', '2024-01-20 10:30:00'),
((SELECT id FROM gates WHERE gate_number = 'B2'), (SELECT id FROM flights WHERE flight_number = 'SW404'), '2024-01-20 06:45:00', '2024-01-20 11:45:00'),
((SELECT id FROM gates WHERE gate_number = 'A4'), (SELECT id FROM flights WHERE flight_number = 'DL505'), '2024-01-20 07:00:00', '2024-01-20 12:00:00'),

-- International Flights
((SELECT id FROM gates WHERE gate_number = 'C1'), (SELECT id FROM flights WHERE flight_number = 'BA601'), '2024-01-20 11:00:00', '2024-01-20 13:30:00'),
((SELECT id FROM gates WHERE gate_number = 'C2'), (SELECT id FROM flights WHERE flight_number = 'AF702'), '2024-01-20 12:00:00', '2024-01-20 14:45:00'),
((SELECT id FROM gates WHERE gate_number = 'C3'), (SELECT id FROM flights WHERE flight_number = 'LH803'), '2024-01-20 13:00:00', '2024-01-20 16:00:00'),
((SELECT id FROM gates WHERE gate_number = 'C4'), (SELECT id FROM flights WHERE flight_number = 'JL904'), '2024-01-20 14:00:00', '2024-01-20 17:15:00'),
((SELECT id FROM gates WHERE gate_number = 'C1'), (SELECT id FROM flights WHERE flight_number = 'EK1005'), '2024-01-20 15:00:00', '2024-01-20 18:30:00'),

-- Evening Flights
((SELECT id FROM gates WHERE gate_number = 'B3'), (SELECT id FROM flights WHERE flight_number = 'DL1106'), '2024-01-20 17:00:00', '2024-01-20 19:45:00'),
((SELECT id FROM gates WHERE gate_number = 'B4'), (SELECT id FROM flights WHERE flight_number = 'AA1207'), '2024-01-20 17:15:00', '2024-01-20 20:00:00'),
((SELECT id FROM gates WHERE gate_number = 'A1'), (SELECT id FROM flights WHERE flight_number = 'UA1308'), '2024-01-20 18:00:00', '2024-01-20 21:15:00'),
((SELECT id FROM gates WHERE gate_number = 'A2'), (SELECT id FROM flights WHERE flight_number = 'SW1409'), '2024-01-20 18:15:00', '2024-01-20 22:30:00'),
((SELECT id FROM gates WHERE gate_number = 'A3'), (SELECT id FROM flights WHERE flight_number = 'DL1510'), '2024-01-20 18:30:00', '2024-01-20 23:45:00');

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