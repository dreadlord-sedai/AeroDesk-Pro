package aerodesk.dao;

import aerodesk.model.User;
import aerodesk.util.DatabaseConnection;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 * Handles authentication and user management
 */
public class UserDAO {
    
    /**
     * Authenticates a user with username, password, and role
     * @param username The username
     * @param password The password
     * @param role The user role
     * @return User object if authentication successful, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public User authenticateUser(String username, String password, String role) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND role = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    FileLogger.getInstance().logInfo("User authenticated successfully: " + username);
                    return user;
                } else {
                    FileLogger.getInstance().logWarning("Authentication failed for user: " + username);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Authentication error: " + e.getMessage());
            throw new DatabaseException("Authentication failed", e);
        }
    }
    
    /**
     * Retrieves a user by username
     * @param username The username
     * @return User object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public User getUserByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve user by username: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve user", e);
        }
    }
    
    /**
     * Retrieves all active users
     * @return List of all active users
     * @throws DatabaseException if database operation fails
     */
    public List<User> getAllActiveUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY username";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + users.size() + " active users from database");
            return users;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to retrieve users: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Creates a new user
     * @param user The user to create
     * @return The created user with ID
     * @throws DatabaseException if database operation fails
     */
    public User createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, is_active) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());
            stmt.setBoolean(5, user.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    FileLogger.getInstance().logInfo("Created new user: " + user.getUsername());
                    return user;
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained");
                }
            }
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to create user: " + e.getMessage());
            throw new DatabaseException("Failed to create user", e);
        }
    }
    
    /**
     * Updates an existing user
     * @param user The user to update
     * @return true if update successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateUser(User user) throws DatabaseException {
        String sql = "UPDATE users SET password_hash = ?, role = ?, full_name = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getFullName());
            stmt.setBoolean(4, user.isActive());
            stmt.setInt(5, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Updated user: " + user.getUsername());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to update user: " + e.getMessage());
            throw new DatabaseException("Failed to update user", e);
        }
    }
    
    /**
     * Deactivates a user (soft delete)
     * @param userId The user ID to deactivate
     * @return true if deactivation successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deactivateUser(int userId) throws DatabaseException {
        String sql = "UPDATE users SET is_active = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                FileLogger.getInstance().logInfo("Deactivated user with ID: " + userId);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to deactivate user: " + e.getMessage());
            throw new DatabaseException("Failed to deactivate user", e);
        }
    }
    
    /**
     * Maps a ResultSet to a User object
     * @param rs The ResultSet
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
} 