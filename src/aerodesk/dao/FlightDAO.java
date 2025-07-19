package aerodesk.dao;

import aerodesk.model.Flight;
import aerodesk.util.DatabaseConnection;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Flight operations
 * Handles all database operations related to flights
 */
public class FlightDAO {
    
    /**
     * Retrieves all flights from the database
     * @return List of all flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getAllFlights() throws DatabaseException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY depart_time";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Flight flight = mapResultSetToFlight(rs);
                flights.add(flight);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + flights.size() + " flights from database");
            return flights;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve flights: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve flights", e);
        }
    }
    
    /**
     * Retrieves a flight by its ID
     * @param flightId The flight ID
     * @return Flight object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Flight getFlightById(int flightId) throws DatabaseException {
        String sql = "SELECT * FROM flights WHERE flight_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Flight flight = mapResultSetToFlight(rs);
                    FileLogger.getInstance().logInfo("Retrieved flight: " + flight.getFlightNo());
                    return flight;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve flight by ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve flight by ID", e);
        }
    }
    
    /**
     * Creates a new flight in the database
     * @param flight The flight to create
     * @return The created flight with generated ID
     * @throws DatabaseException if database operation fails
     */
    public Flight createFlight(Flight flight) throws DatabaseException {
        String sql = "INSERT INTO flights (flight_no, origin, destination, depart_time, arrive_time, aircraft_type, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, flight.getFlightNo());
            stmt.setString(2, flight.getOrigin());
            stmt.setString(3, flight.getDestination());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArriveTime()));
            stmt.setString(6, flight.getAircraftType());
            stmt.setString(7, flight.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating flight failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    flight.setFlightId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new flight: " + flight.getFlightNo());
                    return flight;
                } else {
                    throw new DatabaseException("Creating flight failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create flight: " + e.getMessage());
            throw new DatabaseException("Failed to create flight", e);
        }
    }
    
    /**
     * Updates an existing flight in the database
     * @param flight The flight to update
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateFlight(Flight flight) throws DatabaseException {
        String sql = "UPDATE flights SET flight_no = ?, origin = ?, destination = ?, " +
                    "depart_time = ?, arrive_time = ?, aircraft_type = ?, status = ? " +
                    "WHERE flight_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, flight.getFlightNo());
            stmt.setString(2, flight.getOrigin());
            stmt.setString(3, flight.getDestination());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArriveTime()));
            stmt.setString(6, flight.getAircraftType());
            stmt.setString(7, flight.getStatus().name());
            stmt.setInt(8, flight.getFlightId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated flight: " + flight.getFlightNo());
                return true;
            } else {
                FileLogger.getInstance().logWarning("No flight found to update with ID: " + flight.getFlightId());
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update flight: " + e.getMessage());
            throw new DatabaseException("Failed to update flight", e);
        }
    }
    
    /**
     * Deletes a flight from the database
     * @param flightId The ID of the flight to delete
     * @return true if deletion was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deleteFlight(int flightId) throws DatabaseException {
        String sql = "DELETE FROM flights WHERE flight_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deleted flight with ID: " + flightId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No flight found to delete with ID: " + flightId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to delete flight: " + e.getMessage());
            throw new DatabaseException("Failed to delete flight", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Flight object
     * @param rs The ResultSet containing flight data
     * @return Flight object
     * @throws SQLException if mapping fails
     */
    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNo(rs.getString("flight_no"));
        flight.setOrigin(rs.getString("origin"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartTime(rs.getTimestamp("depart_time").toLocalDateTime());
        flight.setArriveTime(rs.getTimestamp("arrive_time").toLocalDateTime());
        flight.setAircraftType(rs.getString("aircraft_type"));
        flight.setStatus(Flight.FlightStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            flight.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            flight.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return flight;
    }
} 