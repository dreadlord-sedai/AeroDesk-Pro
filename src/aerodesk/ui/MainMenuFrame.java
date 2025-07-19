package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.util.IconManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main menu screen for AeroDesk Pro
 */
public class MainMenuFrame extends JFrame {
    private String currentUser;
    private String userRole;
    private JLabel welcomeLabel;
    
    public MainMenuFrame(String username, String role) {
        this.currentUser = username;
        this.userRole = role;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeComponents() {
        welcomeLabel = ThemeManager.createSubheaderLabel("Welcome, " + currentUser + " (" + userRole + ")");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createTitleLabel(IconManager.getTextIcon("flight") + " AeroDesk Pro");
        titleLabel.setForeground(ThemeManager.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Advanced Airport Management System");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(ThemeManager.WHITE);
        welcomePanel.add(welcomeLabel);
        
        // Main menu panel with modern card-style buttons
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        menuPanel.setBackground(ThemeManager.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create modern menu buttons
        JButton flightSchedulingBtn = createModernMenuButton("Flight Scheduling", "flight", ThemeManager.PRIMARY_BLUE);
        JButton checkInBtn = createModernMenuButton("Passenger Check-In", "user", ThemeManager.SUCCESS_GREEN);
        JButton baggageBtn = createModernMenuButton("Baggage Handling", "baggage", ThemeManager.WARNING_AMBER);
        JButton gateManagementBtn = createModernMenuButton("Gate Management", "gate", ThemeManager.SECONDARY_BLUE);
        JButton flightStatusBtn = createModernMenuButton("Flight Status", "status", ThemeManager.ACCENT_ORANGE);
        JButton aviationStackBtn = createModernMenuButton("Aviation Stack API", "api", ThemeManager.PRIMARY_BLUE);
        JButton reportsBtn = createModernMenuButton("Reports & Logs", "reports", ThemeManager.DARK_GRAY);
        
        menuPanel.add(flightSchedulingBtn);
        menuPanel.add(checkInBtn);
        menuPanel.add(baggageBtn);
        menuPanel.add(gateManagementBtn);
        menuPanel.add(flightStatusBtn);
        menuPanel.add(aviationStackBtn);
        menuPanel.add(reportsBtn);
        
        // Bottom panel with logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(ThemeManager.WHITE);
        JButton logoutBtn = new JButton(IconManager.getTextIcon("logout") + " Logout");
        ThemeManager.styleButton(logoutBtn, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        bottomPanel.add(logoutBtn);
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(welcomePanel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Store buttons for event handling
        flightSchedulingBtn.setActionCommand("flight_scheduling");
        checkInBtn.setActionCommand("check_in");
        baggageBtn.setActionCommand("baggage");
        gateManagementBtn.setActionCommand("gate_management");
        flightStatusBtn.setActionCommand("flight_status");
        aviationStackBtn.setActionCommand("aviation_stack");
        reportsBtn.setActionCommand("reports");
        logoutBtn.setActionCommand("logout");
    }
    
    private JButton createModernMenuButton(String text, String iconType, Color color) {
        // Use IconManager to get proper text-based icons
        String iconText = IconManager.getTextIcon(iconType);
        JButton button = new JButton("<html><center><font size='+2'>" + iconText + "</font><br>" + text + "</center></html>");
        button.setFont(ThemeManager.SUBHEADER_FONT);
        button.setPreferredSize(new Dimension(200, 100));
        button.setBackground(color);
        button.setForeground(ThemeManager.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add modern hover effect with shadow
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
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        ThemeManager.styleFrame(this);
    }
} 