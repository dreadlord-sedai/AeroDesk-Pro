package aerodesk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility for AeroDesk Pro
 * Handles MySQL database connections with proper exception handling
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;
    private final String driver;
    
    private DatabaseConnection() {
        ConfigManager config = ConfigManager.getInstance();
        this.url = config.getProperty("db.url");
        this.username = config.getProperty("db.username");
        this.password = config.getProperty("db.password");
        this.driver = config.getProperty("db.driver");
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            FileLogger.getInstance().logError("Database driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            FileLogger.getInstance().logDebug("Database connection established successfully");
            return connection;
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Failed to establish database connection: " + e.getMessage());
            throw e;
        }
    }
    
    public void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                FileLogger.getInstance().logInfo("Database connection test successful");
            }
        } catch (SQLException e) {
            FileLogger.getInstance().logError("Database connection test failed: " + e.getMessage());
            throw new RuntimeException("Database connection test failed", e);
        }
    }
} 