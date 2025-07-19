package aerodesk.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File logging utility for AeroDesk Pro
 * Handles writing log messages to file
 */
public class FileLogger {
    private static FileLogger instance;
    private PrintWriter writer;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private FileLogger() {
        initializeLogger();
    }
    
    public static FileLogger getInstance() {
        if (instance == null) {
            instance = new FileLogger();
        }
        return instance;
    }
    
    private void initializeLogger() {
        try {
            String logFile = ConfigManager.getInstance().getProperty("log.file", "aerodesk.log");
            writer = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }
    
    public void logInfo(String message) {
        log("INFO", message);
    }
    
    public void logWarning(String message) {
        log("WARNING", message);
    }
    
    public void logError(String message) {
        log("ERROR", message);
    }
    
    public void logDebug(String message) {
        log("DEBUG", message);
    }
    
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);
        
        if (writer != null) {
            writer.println(logEntry);
        }
        
        // Also print to console for development
        System.out.println(logEntry);
    }
    
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
} 