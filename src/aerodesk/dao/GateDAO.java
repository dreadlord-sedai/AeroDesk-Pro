package aerodesk.dao;

import aerodesk.model.Gate;
import aerodesk.model.GateAssignment;
import aerodesk.util.DatabaseConnection;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;
import aerodesk.exception.GateConflictException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Gate operations
 * Handles all database operations related to gates
 */
public class GateDAO {
    
    /**
     * Retrieves all gates from the database
     * @return List of all gates
     * @throws DatabaseException if database operation fails
     */
    public List<Gate> getAllGates() throws DatabaseException {
        List<Gate> gates = new ArrayList<>();
        String sql = "SELECT * FROM gates ORDER BY gate_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Gate gate = mapResultSetToGate(rs);
                gates.add(gate);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + gates.size() + " gates from database");
            return gates;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve gates: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve gates", e);
        }
    }
    
    /**
     * Retrieves all active gates from the database
     * @return List of active gates
     * @throws DatabaseException if database operation fails
     */
    public List<Gate> getActiveGates() throws DatabaseException {
        List<Gate> gates = new ArrayList<>();
        String sql = "SELECT * FROM gates WHERE is_active = TRUE ORDER BY gate_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Gate gate = mapResultSetToGate(rs);
                gates.add(gate);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + gates.size() + " active gates from database");
            return gates;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve active gates: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve active gates", e);
        }
    }
    
    /**
     * Retrieves a gate by its ID
     * @param gateId The gate ID
     * @return Gate object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Gate getGateById(int gateId) throws DatabaseException {
        String sql = "SELECT * FROM gates WHERE gate_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Gate gate = mapResultSetToGate(rs);
                    FileLogger.getInstance().logInfo("Retrieved gate: " + gate.getGateName());
                    return gate;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve gate by ID: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve gate by ID", e);
        }
    }
    
    /**
     * Retrieves a gate by its name
     * @param gateName The gate name
     * @return Gate object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Gate getGateByName(String gateName) throws DatabaseException {
        String sql = "SELECT * FROM gates WHERE gate_name = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gateName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Gate gate = mapResultSetToGate(rs);
                    FileLogger.getInstance().logInfo("Retrieved gate by name: " + gateName);
                    return gate;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve gate by name: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve gate by name", e);
        }
    }
    
    /**
     * Creates a new gate in the database
     * @param gate The gate to create
     * @return The created gate with generated ID
     * @throws DatabaseException if database operation fails
     */
    public Gate createGate(Gate gate) throws DatabaseException {
        String sql = "INSERT INTO gates (gate_name, is_active) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, gate.getGateName());
            stmt.setBoolean(2, gate.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating gate failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    gate.setGateId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new gate: " + gate.getGateName());
                    return gate;
                } else {
                    throw new DatabaseException("Creating gate failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create gate: " + e.getMessage());
            throw new DatabaseException("Failed to create gate", e);
        }
    }
    
    /**
     * Updates an existing gate in the database
     * @param gate The gate to update
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateGate(Gate gate) throws DatabaseException {
        String sql = "UPDATE gates SET gate_name = ?, is_active = ? WHERE gate_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gate.getGateName());
            stmt.setBoolean(2, gate.isActive());
            stmt.setInt(3, gate.getGateId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated gate: " + gate.getGateName());
                return true;
            } else {
                FileLogger.getInstance().logWarning("No gate found to update with ID: " + gate.getGateId());
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update gate: " + e.getMessage());
            throw new DatabaseException("Failed to update gate", e);
        }
    }
    
    /**
     * Deletes a gate from the database
     * @param gateId The ID of the gate to delete
     * @return true if deletion was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deleteGate(int gateId) throws DatabaseException {
        String sql = "DELETE FROM gates WHERE gate_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gateId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deleted gate with ID: " + gateId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No gate found to delete with ID: " + gateId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to delete gate: " + e.getMessage());
            throw new DatabaseException("Failed to delete gate", e);
        }
    }
    
    /**
     * Activates or deactivates a gate
     * @param gateId The gate ID
     * @param active The active status
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean setGateActive(int gateId, boolean active) throws DatabaseException {
        String sql = "UPDATE gates SET is_active = ? WHERE gate_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, active);
            stmt.setInt(2, gateId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Set gate " + gateId + " active status to: " + active);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No gate found to update active status with ID: " + gateId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update gate active status: " + e.getMessage());
            throw new DatabaseException("Failed to update gate active status", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Gate object
     * @param rs The ResultSet containing gate data
     * @return Gate object
     * @throws SQLException if mapping fails
     */
    private Gate mapResultSetToGate(ResultSet rs) throws SQLException {
        Gate gate = new Gate();
        gate.setGateId(rs.getInt("gate_id"));
        gate.setGateName(rs.getString("gate_name"));
        gate.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            gate.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return gate;
    }
    
    // Gate Assignment methods
    public List<GateAssignment> getAllAssignments() throws DatabaseException {
        List<GateAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM gate_assignments ORDER BY assignment_id";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                GateAssignment assignment = mapResultSetToAssignment(rs);
                assignments.add(assignment);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + assignments.size() + " gate assignments from database");
            return assignments;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve gate assignments: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve gate assignments", e);
        }
    }
    
    public GateAssignment createAssignment(GateAssignment assignment) throws DatabaseException, GateConflictException {
        // Check for conflicts first
        if (hasConflict(assignment)) {
            throw new GateConflictException("Gate conflict detected for the specified time period");
        }
        
        String sql = "INSERT INTO gate_assignments (gate_id, flight_id, assigned_from, assigned_to) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, assignment.getGateId());
            stmt.setInt(2, assignment.getFlightId());
            stmt.setTimestamp(3, assignment.getAssignedFrom() != null ? Timestamp.valueOf(assignment.getAssignedFrom()) : null);
            stmt.setTimestamp(4, assignment.getAssignedTo() != null ? Timestamp.valueOf(assignment.getAssignedTo()) : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating gate assignment failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    assignment.setAssignmentId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new gate assignment: " + assignment.getAssignmentId());
                    return assignment;
                } else {
                    throw new DatabaseException("Creating gate assignment failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create gate assignment: " + e.getMessage());
            throw new DatabaseException("Failed to create gate assignment", e);
        }
    }
    
    public boolean removeAssignment(int assignmentId) throws DatabaseException {
        String sql = "DELETE FROM gate_assignments WHERE assignment_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignmentId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deleted gate assignment with ID: " + assignmentId);
                return true;
            } else {
                FileLogger.getInstance().logWarning("No gate assignment found to delete with ID: " + assignmentId);
                return false;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to delete gate assignment: " + e.getMessage());
            throw new DatabaseException("Failed to delete gate assignment", e);
        }
    }
    
    private boolean hasConflict(GateAssignment newAssignment) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM gate_assignments WHERE gate_id = ? AND " +
                    "((assigned_from BETWEEN ? AND ?) OR (assigned_to BETWEEN ? AND ?) OR " +
                    "(? BETWEEN assigned_from AND assigned_to) OR (? BETWEEN assigned_from AND assigned_to))";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newAssignment.getGateId());
            stmt.setTimestamp(2, newAssignment.getAssignedFrom() != null ? Timestamp.valueOf(newAssignment.getAssignedFrom()) : null);
            stmt.setTimestamp(3, newAssignment.getAssignedTo() != null ? Timestamp.valueOf(newAssignment.getAssignedTo()) : null);
            stmt.setTimestamp(4, newAssignment.getAssignedFrom() != null ? Timestamp.valueOf(newAssignment.getAssignedFrom()) : null);
            stmt.setTimestamp(5, newAssignment.getAssignedTo() != null ? Timestamp.valueOf(newAssignment.getAssignedTo()) : null);
            stmt.setTimestamp(6, newAssignment.getAssignedFrom() != null ? Timestamp.valueOf(newAssignment.getAssignedFrom()) : null);
            stmt.setTimestamp(7, newAssignment.getAssignedTo() != null ? Timestamp.valueOf(newAssignment.getAssignedTo()) : null);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to check for gate conflicts: " + e.getMessage());
            throw new DatabaseException("Failed to check for gate conflicts", e);
        }
    }
    
    private GateAssignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        GateAssignment assignment = new GateAssignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setGateId(rs.getInt("gate_id"));
        assignment.setFlightId(rs.getInt("flight_id"));
        
        Timestamp assignedFrom = rs.getTimestamp("assigned_from");
        if (assignedFrom != null) {
            assignment.setAssignedFrom(assignedFrom.toLocalDateTime());
        }
        
        Timestamp assignedTo = rs.getTimestamp("assigned_to");
        if (assignedTo != null) {
            assignment.setAssignedTo(assignedTo.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            assignment.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return assignment;
    }
} 