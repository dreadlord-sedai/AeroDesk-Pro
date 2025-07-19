package aerodesk.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for AeroDesk Pro
 * Handles loading and accessing application configuration
 */
public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
            // Set default values
            setDefaultProperties();
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/aerodesk_pro");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("app.title", "AeroDesk Pro");
        properties.setProperty("log.file", "aerodesk.log");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
} 