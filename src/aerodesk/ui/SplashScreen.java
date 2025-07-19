package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.ui.MainMenuFrame;
import aerodesk.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Professional Splash Screen for AeroDesk Pro
 * Displays during application startup with loading animation
 */
public class SplashScreen extends JWindow {
    
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel versionLabel;
    private JLabel loadingLabel;
    private Timer progressTimer;
    private Timer loadingTimer;
    private int progressValue = 0;
    private int loadingDots = 0;
    
    private final String[] loadingMessages = {
        "Initializing AeroDesk Pro...",
        "Loading configuration...",
        "Connecting to database...",
        "Initializing flight data...",
        "Setting up weather services...",
        "Preparing user interface...",
        "Starting aviation services...",
        "Loading API integrations...",
        "Finalizing startup...",
        "Ready to launch!"
    };
    
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public SplashScreen() {
        initializeComponents();
        setupLayout();
        setupTimers();
        configureWindow();
    }
    
    private void initializeComponents() {
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setForeground(ThemeManager.SUCCESS_GREEN);
        progressBar.setBackground(ThemeManager.LIGHT_GRAY);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(400, 8));
        
        // Status label
        statusLabel = new JLabel(loadingMessages[0]);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(ThemeManager.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Loading animation label
        loadingLabel = new JLabel("Loading");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setForeground(ThemeManager.WHITE);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Version label
        versionLabel = new JLabel("AeroDesk Pro v2.0 | " + LocalDateTime.now().format(timeFormatter));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(ThemeManager.WHITE);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create gradient background panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient from dark blue to lighter blue
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 50, 100),
                    0, getHeight(), new Color(45, 85, 150)
                );
                
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < getWidth(); i += 20) {
                    for (int j = 0; j < getHeight(); j += 20) {
                        g2d.drawLine(i, j, i + 1, j + 1);
                    }
                }
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Top section with logo and title
        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 20, 40));
        
        // Logo removed for cleaner design
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout(10, 5));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("AeroDesk Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Advanced Airport Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Use only title (removed logo)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(titlePanel);
        
        topPanel.add(headerPanel, BorderLayout.CENTER);
        
        // Center section with status
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        centerPanel.add(statusLabel, BorderLayout.CENTER);
        centerPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        // Bottom section with progress and version
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(versionLabel, BorderLayout.SOUTH);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupTimers() {
        // Progress timer - updates progress bar (reduced from 100ms to 50ms for faster loading)
        progressTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue += 2; // Increased increment from 1 to 2 for faster progress
                progressBar.setValue(progressValue);
                progressBar.setString(progressValue + "%");
                
                // Update status message based on progress
                int messageIndex = (progressValue * loadingMessages.length) / 100;
                if (messageIndex < loadingMessages.length) {
                    statusLabel.setText(loadingMessages[messageIndex]);
                }
                
                // Stop when complete
                if (progressValue >= 100) {
                    progressTimer.stop();
                    loadingTimer.stop();
                    dispose();
                    launchLoginScreen();
                }
            }
        });
        
        // Loading animation timer - updates dots (reduced from 500ms to 300ms)
        loadingTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingDots = (loadingDots + 1) % 4;
                StringBuilder dots = new StringBuilder("Loading");
                for (int i = 0; i < loadingDots; i++) {
                    dots.append(".");
                }
                loadingLabel.setText(dots.toString());
            }
        });
    }
    
    private void configureWindow() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        // Remove window decorations for a clean look
        setBackground(new Color(0, 0, 0, 0));
        
        // Start timers
        progressTimer.start();
        loadingTimer.start();
        
        // Log startup
        FileLogger.getInstance().logInfo("Splash screen displayed - Starting AeroDesk Pro");
    }
    
    private void launchLoginScreen() {
        // Launch the login screen after splash screen completes
        SwingUtilities.invokeLater(() -> {
            try {
                FileLogger.getInstance().logInfo("Launching login screen after splash screen");
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                FileLogger.getInstance().logError("Failed to launch login screen: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    public void showSplash() {
        setVisible(true);
    }
} 