package aerodesk.dao;

import aerodesk.model.Booking;
import aerodesk.util.DatabaseConnection;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking operations
 * Handles all database operations related to bookings
 */
public class BookingDAO {
    
    /**
     * Retrieves all bookings from the database
     * @return List of all bookings
     * @throws DatabaseException if database operation fails
     */
    public List<Booking> getAllBookings() throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookings.add(booking);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + bookings.size() + " bookings from database");
            return bookings;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve bookings: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve bookings", e);
        }
    }
    
    /**
     * Retrieves a booking by its ID
     * @param bookingId The booking ID
     * @return Booking object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Booking getBookingById(int bookingId) throws DatabaseException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    FileLogger.getInstance().logInfo("Retrieved booking: " + booking.getBookingReference());
                    return booking;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve booking by ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve booking by ID", e);
        }
    }
    
    /**
     * Retrieves a booking by its reference number
     * @param bookingReference The booking reference
     * @return Booking object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Booking getBookingByReference(String bookingReference) throws DatabaseException {
        String sql = "SELECT * FROM bookings WHERE booking_reference = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookingReference);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    FileLogger.getInstance().logInfo("Retrieved booking by reference: " + bookingReference);
                    return booking;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve booking by reference: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve booking by reference", e);
        }
    }
    
    /**
     * Retrieves all bookings for a specific flight
     * @param flightId The flight ID
     * @return List of bookings for the flight
     * @throws DatabaseException if database operation fails
     */
    public List<Booking> getBookingsByFlightId(int flightId) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE flight_id = ? ORDER BY passenger_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    bookings.add(booking);
                }
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + bookings.size() + " bookings for flight ID: " + flightId);
            return bookings;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve bookings by flight ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve bookings by flight ID", e);
        }
    }
    
    /**
     * Creates a new booking in the database
     * @param booking The booking to create
     * @return The created booking with generated ID
     * @throws DatabaseException if database operation fails
     */
    public Booking createBooking(Booking booking) throws DatabaseException {
        String sql = "INSERT INTO bookings (flight_id, passenger_name, seat_number, booking_reference) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getFlightId());
            stmt.setString(2, booking.getPassengerName());
            stmt.setString(3, booking.getSeatNo());
            stmt.setString(4, booking.getBookingReference());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating booking failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new booking: " + booking.getBookingReference());
                    return booking;
                } else {
                    throw new DatabaseException("Creating booking failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create booking: " + e.getMessage());
            throw new DatabaseException("Failed to create booking", e);
        }
    }
    
    /**
     * Updates an existing booking in the database
     * @param booking The booking to update
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateBooking(Booking booking) throws DatabaseException {
        String sql = "UPDATE bookings SET flight_id = ?, passenger_name = ?, seat_number = ?, " +
                    "check_in_status = ?, created_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getFlightId());
            stmt.setString(2, booking.getPassengerName());
            stmt.setString(3, booking.getSeatNo());
            stmt.setString(4, booking.isCheckedIn() ? "CHECKED_IN" : "NOT_CHECKED_IN");
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, booking.getBookingId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated booking: " + booking.getBookingReference());
                return true;
            } else {
                FileLogger.getInstance().logWarning("No booking found to update with ID: " + booking.getBookingId());
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update booking: " + e.getMessage());
            throw new DatabaseException("Failed to update booking", e);
        }
    }
    
    /**
     * Deletes a booking from the database
     * @param bookingId The ID of the booking to delete
     * @return true if deletion was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deleteBooking(int bookingId) throws DatabaseException {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deleted booking with ID: " + bookingId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No booking found to delete with ID: " + bookingId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to delete booking: " + e.getMessage());
            throw new DatabaseException("Failed to delete booking", e);
        }
    }
    
    /**
     * Checks in a passenger
     * @param bookingId The booking ID
     * @param seatNo The assigned seat number
     * @return true if check-in was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean checkInPassenger(int bookingId, String seatNo) throws DatabaseException {
        String sql = "UPDATE bookings SET check_in_status = 'CHECKED_IN', created_at = ?, seat_number = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, seatNo);
            stmt.setInt(3, bookingId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Checked in passenger for booking ID: " + bookingId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No booking found to check in with ID: " + bookingId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to check in passenger: " + e.getMessage());
            throw new DatabaseException("Failed to check in passenger", e);
        }
    }
    
    /**
     * Gets all checked-in passengers for a flight
     * @param flightId The flight ID
     * @return List of checked-in bookings
     * @throws DatabaseException if database operation fails
     */
    public List<Booking> getCheckedInPassengers(int flightId) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE flight_id = ? AND check_in_status = 'CHECKED_IN' ORDER BY created_at";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    bookings.add(booking);
                }
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + bookings.size() + " checked-in passengers for flight ID: " + flightId);
            return bookings;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve checked-in passengers: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve checked-in passengers", e);
        }
    }
    
    /**
     * Checks if a seat is available for a flight
     * @param flightId The flight ID
     * @param seatNo The seat number to check
     * @return true if seat is available
     * @throws DatabaseException if database operation fails
     */
    public boolean isSeatAvailable(int flightId, String seatNo) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE flight_id = ? AND seat_number = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.setString(2, seatNo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to check seat availability: " + e.getMessage());
            throw new DatabaseException("Failed to check seat availability", e);
        }
    }
    
    /**
     * Simple search by any field
     * @param searchTerm The search term
     * @return List of matching bookings
     * @throws DatabaseException if database operation fails
     */
    public List<Booking> searchBookings(String searchTerm) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE passenger_name LIKE ? OR booking_reference LIKE ? ORDER BY passenger_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    bookings.add(booking);
                }
            }
            
            FileLogger.getInstance().logInfo("Found " + bookings.size() + " bookings for search term: " + searchTerm);
            return bookings;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to search bookings: " + e.getMessage());
            throw new DatabaseException("Failed to search bookings", e);
        }
    }
    
    /**
     * Simple filter by check-in status
     * @param checkedIn true for checked-in passengers, false for not checked-in
     * @return List of filtered bookings
     * @throws DatabaseException if database operation fails
     */
    public List<Booking> getBookingsByStatus(boolean checkedIn) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE check_in_status = ? ORDER BY passenger_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String status = checkedIn ? "CHECKED_IN" : "NOT_CHECKED_IN";
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    bookings.add(booking);
                }
            }
            
            FileLogger.getInstance().logInfo("Found " + bookings.size() + " " + (checkedIn ? "checked-in" : "not checked-in") + " bookings");
            return bookings;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to filter bookings by status: " + e.getMessage());
            throw new DatabaseException("Failed to filter bookings by status", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Booking object
     * @param rs The ResultSet containing booking data
     * @return Booking object
     * @throws SQLException if mapping fails
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        
        // Handle different possible column names for ID
        try {
            booking.setBookingId(rs.getInt("id"));
        } catch (SQLException e) {
            try {
                booking.setBookingId(rs.getInt("booking_id"));
            } catch (SQLException e2) {
                booking.setBookingId(0);
            }
        }
        
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setPassengerName(rs.getString("passenger_name"));
        
        // Handle different possible column names for seat
        try {
            booking.setSeatNo(rs.getString("seat_number"));
        } catch (SQLException e) {
            try {
                booking.setSeatNo(rs.getString("seat_no"));
            } catch (SQLException e2) {
                booking.setSeatNo("N/A");
            }
        }
        
        // Handle different possible column names for check-in status
        try {
            booking.setCheckedIn("CHECKED_IN".equals(rs.getString("check_in_status")));
        } catch (SQLException e) {
            try {
                booking.setCheckedIn(rs.getBoolean("checked_in"));
            } catch (SQLException e2) {
                booking.setCheckedIn(false);
            }
        }
        
        // Handle different possible column names for booking reference
        try {
            booking.setBookingReference(rs.getString("booking_reference"));
        } catch (SQLException e) {
            try {
                booking.setBookingReference(rs.getString("booking_ref"));
            } catch (SQLException e2) {
                booking.setBookingReference("N/A");
            }
        }
        
        // Handle passport number
        try {
            booking.setPassportNo(rs.getString("passport_no"));
        } catch (SQLException e) {
            booking.setPassportNo("N/A");
        }
        
        // Handle check-in time
        try {
            Timestamp checkInTime = rs.getTimestamp("check_in_time");
            if (checkInTime != null) {
                booking.setCheckInTime(checkInTime.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Check-in time not available
        }
        
        // Handle created at
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                booking.setCreatedAt(createdAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Created at not available
        }
        
        return booking;
    }
} 