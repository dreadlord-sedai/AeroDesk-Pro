package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Main Menu Frame with professional dashboard-like design
 * Provides modern, clean interface for AeroDesk Pro operations
 */
public class MainMenuFrame extends JFrame {
    
    private String currentUser;
    private String userRole;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    
    public MainMenuFrame(String username, String role) {
        this.currentUser = username;
        this.userRole = role;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        
        FileLogger.getInstance().logInfo("Enhanced main menu initialized for user: " + username);
    }
    
    private void initializeComponents() {
        // Welcome label with enhanced styling
        welcomeLabel = new JLabel("Welcome, " + currentUser);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(ThemeManager.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Time and status labels
        timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(ThemeManager.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        statusLabel = new JLabel("System Status: Online | Role: " + userRole);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeManager.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Header panel with gradient background
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content area
        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("AeroDesk Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ThemeManager.WHITE);
        
        JLabel subtitleLabel = new JLabel("Airport Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeManager.WHITE);
        
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Right side - User info and time
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setOpaque(false);
        
        JPanel userInfoPanel = new JPanel(new BorderLayout(5, 2));
        userInfoPanel.setOpaque(false);
        userInfoPanel.add(welcomeLabel, BorderLayout.NORTH);
        userInfoPanel.add(timeLabel, BorderLayout.CENTER);
        userInfoPanel.add(statusLabel, BorderLayout.SOUTH);
        
        rightPanel.add(userInfoPanel, BorderLayout.EAST);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(ThemeManager.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Quick stats panel
        JPanel statsPanel = createQuickStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Menu grid panel
        JPanel menuPanel = createMenuGridPanel();
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createQuickStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(ThemeManager.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Quick stat cards
        statsPanel.add(createStatCard("Active Flights", "24", "🛫", ThemeManager.PRIMARY_BLUE));
        statsPanel.add(createStatCard("Check-ins Today", "156", "👥", ThemeManager.SUCCESS_GREEN));
        statsPanel.add(createStatCard("Gates Occupied", "8", "🚪", ThemeManager.WARNING_AMBER));
        statsPanel.add(createStatCard("System Status", "Online", "✅", ThemeManager.SUCCESS_GREEN));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(ThemeManager.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Icon and title
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(ThemeManager.DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setOpaque(false);
        topPanel.add(iconLabel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMenuGridPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        menuPanel.setBackground(ThemeManager.LIGHT_GRAY);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create enhanced menu buttons
        menuPanel.add(createEnhancedMenuButton("Flight Scheduling", "Schedule and manage flight operations", "🛫", ThemeManager.PRIMARY_BLUE, "flight_scheduling"));
        menuPanel.add(createEnhancedMenuButton("Passenger Check-In", "Process passenger check-ins", "👥", ThemeManager.SUCCESS_GREEN, "check_in"));
        menuPanel.add(createEnhancedMenuButton("Baggage Handling", "Manage baggage operations", "🧳", ThemeManager.WARNING_AMBER, "baggage"));
        menuPanel.add(createEnhancedMenuButton("Gate Management", "Monitor and assign gates", "🚪", ThemeManager.SECONDARY_BLUE, "gate_management"));
        menuPanel.add(createEnhancedMenuButton("Flight Status", "Track flight status and updates", "📊", ThemeManager.ACCENT_ORANGE, "flight_status"));
        menuPanel.add(createEnhancedMenuButton("Aviation Stack API", "Real-time flight data integration", "🌐", ThemeManager.PRIMARY_BLUE, "aviation_stack"));
        menuPanel.add(createEnhancedMenuButton("Reports & Logs", "Generate reports and view logs", "📋", ThemeManager.DARK_GRAY, "reports"));
        menuPanel.add(createEnhancedMenuButton("Live Dashboard", "Real-time operations monitoring", "📈", ThemeManager.SUCCESS_GREEN, "dashboard"));
        
        // Empty panel for the last slot
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        menuPanel.add(emptyPanel);
        
        return menuPanel;
    }
    
    private JButton createEnhancedMenuButton(String title, String description, String icon, Color color, String actionCommand) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 10));
        button.setPreferredSize(new Dimension(200, 120));
        button.setBackground(color);
        button.setForeground(ThemeManager.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setActionCommand(actionCommand);
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(ThemeManager.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(ThemeManager.WHITE);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descLabel.setForeground(ThemeManager.WHITE);
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(descLabel, BorderLayout.SOUTH);
        
        button.add(contentPanel);
        
        // Enhanced hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 2, 2),
                    BorderFactory.createLineBorder(color.darker(), 2)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createEmptyBorder());
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(ThemeManager.DARK_GRAY);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left side - Copyright
        JLabel copyrightLabel = new JLabel("© 2024 AeroDesk Pro - Airport Management System");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(ThemeManager.WHITE);
        
        // Right side - Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(ThemeManager.ERROR_RED);
        logoutBtn.setForeground(ThemeManager.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setActionCommand("logout");
        
        // Logout button hover effect
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(ThemeManager.ERROR_RED.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(ThemeManager.ERROR_RED);
            }
        });
        
        footerPanel.add(copyrightLabel, BorderLayout.WEST);
        footerPanel.add(logoutBtn, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private void setupEventHandlers() {
        // Add action listeners to all buttons
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                addActionListenersToPanel((JPanel) comp);
            }
        }
    }
    
    private void addActionListenersToPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleButtonClick(e.getActionCommand());
                    }
                });
            } else if (comp instanceof JPanel) {
                addActionListenersToPanel((JPanel) comp);
            }
        }
    }
    
    private void handleButtonClick(String actionCommand) {
        switch (actionCommand) {
            case "flight_scheduling":
                openFlightScheduling();
                break;
            case "check_in":
                openCheckIn();
                break;
            case "baggage":
                openBaggageHandling();
                break;
            case "gate_management":
                openGateManagement();
                break;
            case "flight_status":
                openFlightStatus();
                break;
            case "aviation_stack":
                openAviationStack();
                break;
            case "reports":
                openReports();
                break;
            case "dashboard":
                openDashboard();
                break;
            case "logout":
                handleLogout();
                break;
        }
    }
    
    private void openFlightScheduling() {
        SwingUtilities.invokeLater(() -> {
            new FlightSchedulingFrame().setVisible(true);
        });
    }
    
    private void openCheckIn() {
        SwingUtilities.invokeLater(() -> {
            new CheckInFrame().setVisible(true);
        });
    }
    
    private void openBaggageHandling() {
        SwingUtilities.invokeLater(() -> {
            new BaggageFrame().setVisible(true);
        });
    }
    
    private void openGateManagement() {
        SwingUtilities.invokeLater(() -> {
            new GateManagementFrame().setVisible(true);
        });
    }
    
    private void openFlightStatus() {
        SwingUtilities.invokeLater(() -> {
            new FlightStatusFrame().setVisible(true);
        });
    }
    
    private void openReports() {
        SwingUtilities.invokeLater(() -> {
            new ReportsFrame().setVisible(true);
        });
    }
    
    private void openAviationStack() {
        SwingUtilities.invokeLater(() -> {
            new AviationStackFrame().setVisible(true);
        });
    }
    
    private void openDashboard() {
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame().setVisible(true);
        });
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            FileLogger.getInstance().logInfo("User " + currentUser + " logged out");
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
} 