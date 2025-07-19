-- AeroDesk Pro Database Schema
-- Airport Management System

-- Create database
CREATE DATABASE IF NOT EXISTS aerodesk_pro;
USE aerodesk_pro;

-- Flights table
CREATE TABLE flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_no VARCHAR(10) UNIQUE NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    depart_time DATETIME NOT NULL,
    arrive_time DATETIME NOT NULL,
    aircraft_type VARCHAR(20),
    status ENUM('SCHEDULED', 'ON_TIME', 'DELAYED', 'DEPARTED', 'CANCELLED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Gates table
CREATE TABLE gates (
    gate_id INT AUTO_INCREMENT PRIMARY KEY,
    gate_name VARCHAR(5) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gate assignments table
CREATE TABLE gate_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    gate_id INT NOT NULL,
    assigned_from DATETIME NOT NULL,
    assigned_to DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    FOREIGN KEY (gate_id) REFERENCES gates(gate_id) ON DELETE CASCADE,
    UNIQUE KEY unique_gate_time (gate_id, assigned_from, assigned_to)
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    passport_no VARCHAR(20) NOT NULL,
    seat_no VARCHAR(5),
    checked_in BOOLEAN DEFAULT FALSE,
    check_in_time DATETIME,
    booking_reference VARCHAR(10) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE
);

-- Baggage table
CREATE TABLE baggage (
    baggage_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL,
    baggage_type ENUM('CHECKED', 'CARRY_ON') DEFAULT 'CHECKED',
    tag_number VARCHAR(15) UNIQUE NOT NULL,
    status ENUM('LOADED', 'IN_TRANSIT', 'DELIVERED') DEFAULT 'LOADED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- Users table for authentication
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- System logs table
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    log_level ENUM('INFO', 'WARNING', 'ERROR', 'DEBUG') NOT NULL,
    message TEXT NOT NULL,
    source VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO gates (gate_name) VALUES 
('A1'), ('A2'), ('A3'), ('B1'), ('B2'), ('B3'), ('C1'), ('C2');

INSERT INTO users (username, password_hash, role, full_name) VALUES 
('admin', 'admin123', 'ADMIN', 'System Administrator'),
('staff1', 'staff123', 'STAFF', 'John Smith'),
('staff2', 'staff123', 'STAFF', 'Jane Doe');

-- Insert sample flights
INSERT INTO flights (flight_no, origin, destination, depart_time, arrive_time, aircraft_type) VALUES 
('AA101', 'New York', 'Los Angeles', '2024-01-15 08:00:00', '2024-01-15 11:30:00', 'Boeing 737'),
('UA202', 'Chicago', 'Miami', '2024-01-15 09:15:00', '2024-01-15 12:45:00', 'Airbus A320'),
('DL303', 'Atlanta', 'Seattle', '2024-01-15 10:30:00', '2024-01-15 13:15:00', 'Boeing 757'),
('SW404', 'Dallas', 'Denver', '2024-01-15 11:00:00', '2024-01-15 12:30:00', 'Boeing 737');

-- Insert sample bookings
INSERT INTO bookings (flight_id, passenger_name, passport_no, seat_no, booking_reference) VALUES 
(1, 'Alice Johnson', 'US123456789', '12A', 'BK001'),
(1, 'Bob Wilson', 'US987654321', '12B', 'BK002'),
(2, 'Carol Davis', 'US456789123', '15C', 'BK003'),
(3, 'David Brown', 'US789123456', '8D', 'BK004');

-- Insert sample baggage
INSERT INTO baggage (booking_id, weight_kg, baggage_type, tag_number) VALUES 
(1, 23.5, 'CHECKED', 'BG001'),
(2, 18.2, 'CHECKED', 'BG002'),
(3, 15.8, 'CARRY_ON', 'BG003'),
(4, 28.1, 'CHECKED', 'BG004'); 