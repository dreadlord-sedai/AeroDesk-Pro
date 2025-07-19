package aerodesk;

import com.formdev.flatlaf.FlatLightLaf;
import aerodesk.ui.LoginFrame;
import aerodesk.util.ConfigManager;
import aerodesk.util.FileLogger;
import javax.swing.*;

/**
 * Main entry point for AeroDesk Pro Airport Management System
 */
public class Main {
    
    public static void main(String[] args) {
        // Set up FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf theme: " + e.getMessage());
        }
        
        // Initialize configuration
        ConfigManager.getInstance();
        
        // Start logging
        FileLogger.getInstance().logInfo("AeroDesk Pro starting up...");
        
        // Launch login screen
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
                FileLogger.getInstance().logInfo("Login screen displayed successfully");
            } catch (Exception e) {
                FileLogger.getInstance().logError("Failed to display login screen: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
} 