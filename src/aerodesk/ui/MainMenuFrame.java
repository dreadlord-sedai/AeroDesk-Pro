package aerodesk.ui;

import aerodesk.util.FileLogger;
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
        welcomeLabel = new JLabel("Welcome, " + currentUser + " (" + userRole + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(41, 128, 185));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("AeroDesk Pro - Airport Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel);
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(welcomeLabel);
        
        // Main menu panel with buttons
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create menu buttons
        JButton flightSchedulingBtn = createMenuButton("Flight Scheduling", "✈️");
        JButton checkInBtn = createMenuButton("Passenger Check-In", "👤");
        JButton baggageBtn = createMenuButton("Baggage Handling", "👜");
        JButton gateManagementBtn = createMenuButton("Gate Management", "🚪");
        JButton flightStatusBtn = createMenuButton("Flight Status", "📊");
        JButton reportsBtn = createMenuButton("Reports & Logs", "📋");
        
        menuPanel.add(flightSchedulingBtn);
        menuPanel.add(checkInBtn);
        menuPanel.add(baggageBtn);
        menuPanel.add(gateManagementBtn);
        menuPanel.add(flightStatusBtn);
        menuPanel.add(reportsBtn);
        
        // Bottom panel with logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 35));
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
        reportsBtn.setActionCommand("reports");
        logoutBtn.setActionCommand("logout");
    }
    
    private JButton createMenuButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(200, 80));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
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
                        handleMenuAction(e.getActionCommand());
                    }
                });
            } else if (comp instanceof JPanel) {
                addActionListenersToPanel((JPanel) comp);
            }
        }
    }
    
    private void handleMenuAction(String action) {
        FileLogger.getInstance().logInfo("User " + currentUser + " accessed: " + action);
        
        switch (action) {
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
} 