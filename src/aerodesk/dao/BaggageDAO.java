package aerodesk.dao;

import aerodesk.model.Baggage;
import aerodesk.util.DatabaseConnection;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Baggage operations
 * Handles all database operations related to baggage
 */
public class BaggageDAO {
    
    /**
     * Retrieves all baggage from the database
     * @return List of all baggage
     * @throws DatabaseException if database operation fails
     */
    public List<Baggage> getAllBaggage() throws DatabaseException {
        List<Baggage> baggageList = new ArrayList<>();
        String sql = "SELECT * FROM baggage ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Baggage baggage = mapResultSetToBaggage(rs);
                baggageList.add(baggage);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + baggageList.size() + " baggage items from database");
            return baggageList;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage", e);
        }
    }
    
    /**
     * Retrieves baggage by its ID
     * @param baggageId The baggage ID
     * @return Baggage object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Baggage getBaggageById(int baggageId) throws DatabaseException {
        String sql = "SELECT * FROM baggage WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, baggageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Baggage baggage = mapResultSetToBaggage(rs);
                    FileLogger.getInstance().logInfo("Retrieved baggage: " + baggage.getTagNumber());
                    return baggage;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage by ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage by ID", e);
        }
    }
    
    /**
     * Retrieves baggage by tag number
     * @param tagNumber The tag number
     * @return Baggage object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Baggage getBaggageByTagNumber(String tagNumber) throws DatabaseException {
        String sql = "SELECT * FROM baggage WHERE baggage_tag = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tagNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Baggage baggage = mapResultSetToBaggage(rs);
                    FileLogger.getInstance().logInfo("Retrieved baggage by tag: " + tagNumber);
                    return baggage;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage by tag: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage by tag", e);
        }
    }
    
    /**
     * Retrieves all baggage for a specific booking
     * @param bookingId The booking ID
     * @return List of baggage for the booking
     * @throws DatabaseException if database operation fails
     */
    public List<Baggage> getBaggageByBookingId(int bookingId) throws DatabaseException {
        List<Baggage> baggageList = new ArrayList<>();
        String sql = "SELECT * FROM baggage WHERE booking_id = ? ORDER BY created_at";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Baggage baggage = mapResultSetToBaggage(rs);
                    baggageList.add(baggage);
                }
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + baggageList.size() + " baggage items for booking ID: " + bookingId);
            return baggageList;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage by booking ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage by booking ID", e);
        }
    }
    
    /**
     * Creates a new baggage item in the database
     * @param baggage The baggage to create
     * @return The created baggage with generated ID
     * @throws DatabaseException if database operation fails
     */
    public Baggage createBaggage(Baggage baggage) throws DatabaseException {
        String sql = "INSERT INTO baggage (booking_id, weight, status) " +
                    "VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, baggage.getBookingId());
            stmt.setDouble(2, baggage.getWeightKg());
            stmt.setString(3, baggage.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating baggage failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    baggage.setBaggageId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new baggage: " + baggage.getTagNumber());
                    return baggage;
                } else {
                    throw new DatabaseException("Creating baggage failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create baggage: " + e.getMessage());
            throw new DatabaseException("Failed to create baggage", e);
        }
    }
    
    /**
     * Updates an existing baggage item in the database
     * @param baggage The baggage to update
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateBaggage(Baggage baggage) throws DatabaseException {
        String sql = "UPDATE baggage SET booking_id = ?, weight = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, baggage.getBookingId());
            stmt.setDouble(2, baggage.getWeightKg());
            stmt.setString(3, baggage.getStatus().name());
            stmt.setInt(4, baggage.getBaggageId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated baggage: " + baggage.getTagNumber());
                return true;
            } else {
                FileLogger.getInstance().logWarning("No baggage found to update with ID: " + baggage.getBaggageId());
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update baggage: " + e.getMessage());
            throw new DatabaseException("Failed to update baggage", e);
        }
    }
    
    /**
     * Deletes a baggage item from the database
     * @param baggageId The ID of the baggage to delete
     * @return true if deletion was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deleteBaggage(int baggageId) throws DatabaseException {
        String sql = "DELETE FROM baggage WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, baggageId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deleted baggage with ID: " + baggageId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No baggage found to delete with ID: " + baggageId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to delete baggage: " + e.getMessage());
            throw new DatabaseException("Failed to delete baggage", e);
        }
    }
    
    /**
     * Updates baggage status
     * @param baggageId The baggage ID
     * @param newStatus The new status
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateBaggageStatus(int baggageId, Baggage.BaggageStatus newStatus) throws DatabaseException {
        String sql = "UPDATE baggage SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, baggageId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated baggage status to: " + newStatus);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No baggage found to update status with ID: " + baggageId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update baggage status: " + e.getMessage());
            throw new DatabaseException("Failed to update baggage status", e);
        }
    }
    
    /**
     * Gets baggage by status
     * @param status The status to filter by
     * @return List of baggage with the specified status
     * @throws DatabaseException if database operation fails
     */
    public List<Baggage> getBaggageByStatus(Baggage.BaggageStatus status) throws DatabaseException {
        List<Baggage> baggageList = new ArrayList<>();
        String sql = "SELECT * FROM baggage WHERE status = ? ORDER BY created_at";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Baggage baggage = mapResultSetToBaggage(rs);
                    baggageList.add(baggage);
                }
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + baggageList.size() + " baggage items with status: " + status);
            return baggageList;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage by status: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage by status", e);
        }
    }
    
    /**
     * Gets all baggage that needs status updates (not delivered)
     * @return List of baggage that can be updated
     * @throws DatabaseException if database operation fails
     */
    public List<Baggage> getBaggageForStatusUpdate() throws DatabaseException {
        List<Baggage> baggageList = new ArrayList<>();
        String sql = "SELECT * FROM baggage WHERE status != 'DELIVERED' ORDER BY created_at";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Baggage baggage = mapResultSetToBaggage(rs);
                baggageList.add(baggage);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + baggageList.size() + " baggage items for status update");
            return baggageList;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve baggage for status update: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve baggage for status update", e);
        }
    }
    
    /**
     * Generates a unique tag number
     * @return A unique tag number
     * @throws DatabaseException if database operation fails
     */
    public String generateTagNumber() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM baggage";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("BG%06d", count + 1);
            }
            
            return "BG000001";
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to generate tag number: " + e.getMessage());
            throw new DatabaseException("Failed to generate tag number", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Baggage object
     * @param rs The ResultSet containing baggage data
     * @return Baggage object
     * @throws SQLException if mapping fails
     */
    private Baggage mapResultSetToBaggage(ResultSet rs) throws SQLException {
        Baggage baggage = new Baggage();
        baggage.setBaggageId(rs.getInt("id"));
        baggage.setBookingId(rs.getInt("booking_id"));
        baggage.setWeightKg(rs.getDouble("weight"));
        baggage.setTagNumber(rs.getString("baggage_tag"));
        baggage.setBaggageType(Baggage.BaggageType.valueOf(rs.getString("baggage_type")));
        baggage.setStatus(Baggage.BaggageStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            baggage.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return baggage;
    }
} 