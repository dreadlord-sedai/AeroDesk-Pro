-- AeroDesk Pro Database Setup Script
-- Run this script as MySQL root user to set up the database

-- Create the database
CREATE DATABASE IF NOT EXISTS aerodesk_pro;
USE aerodesk_pro;

-- Create a dedicated user for the application (more secure than using root)
CREATE USER IF NOT EXISTS 'aerodesk_user'@'localhost' IDENTIFIED BY 'aerodesk_password';

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON aerodesk_pro.* TO 'aerodesk_user'@'localhost';
FLUSH PRIVILEGES;

-- Create tables (if they don't exist)
CREATE TABLE IF NOT EXISTS flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(10) NOT NULL UNIQUE,
    origin VARCHAR(3) NOT NULL,
    destination VARCHAR(3) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    aircraft_type VARCHAR(20),
    status ENUM('SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(10) NOT NULL UNIQUE,
    passenger_name VARCHAR(100) NOT NULL,
    flight_id INT NOT NULL,
    seat_number VARCHAR(5),
    check_in_status ENUM('NOT_CHECKED_IN', 'CHECKED_IN', 'BOARDED') DEFAULT 'NOT_CHECKED_IN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

CREATE TABLE IF NOT EXISTS baggage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    baggage_tag VARCHAR(15) NOT NULL UNIQUE,
    booking_id INT NOT NULL,
    weight DECIMAL(5,2),
    status ENUM('CHECKED_IN', 'LOADED', 'DELIVERED', 'LOST') DEFAULT 'CHECKED_IN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE IF NOT EXISTS gates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    gate_number VARCHAR(5) NOT NULL UNIQUE,
    terminal VARCHAR(2),
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gate_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    gate_id INT NOT NULL,
    flight_id INT NOT NULL,
    assignment_time DATETIME NOT NULL,
    departure_time DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (gate_id) REFERENCES gates(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT IGNORE INTO flights (flight_number, origin, destination, departure_time, arrival_time, aircraft_type) VALUES
('AA101', 'JFK', 'LAX', '2024-01-15 10:00:00', '2024-01-15 13:30:00', 'Boeing 737'),
('AA102', 'LAX', 'JFK', '2024-01-15 14:00:00', '2024-01-15 21:30:00', 'Boeing 737'),
('UA201', 'ORD', 'SFO', '2024-01-15 09:00:00', '2024-01-15 11:30:00', 'Airbus A320'),
('DL301', 'ATL', 'MIA', '2024-01-15 12:00:00', '2024-01-15 14:00:00', 'Boeing 757');

INSERT IGNORE INTO gates (gate_number, terminal, status) VALUES
('A1', 'A', 'AVAILABLE'),
('A2', 'A', 'AVAILABLE'),
('B1', 'B', 'AVAILABLE'),
('B2', 'B', 'AVAILABLE');

-- Insert sample users for authentication
INSERT IGNORE INTO users (username, password_hash, role, full_name) VALUES
('admin', 'admin123', 'ADMIN', 'System Administrator'),
('staff1', 'staff123', 'STAFF', 'John Smith'),
('staff2', 'staff123', 'STAFF', 'Jane Doe');

-- Show setup completion message
SELECT 'AeroDesk Pro database setup completed successfully!' AS status; 