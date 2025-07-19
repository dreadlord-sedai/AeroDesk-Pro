package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.dao.UserDAO;
import aerodesk.model.User;
import aerodesk.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Login screen for AeroDesk Pro
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton exitButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[]{"STAFF", "ADMIN"});
        
        // Create buttons without icons
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        
        // Apply modern styling
        ThemeManager.styleTextField(usernameField);
        ThemeManager.stylePasswordField(passwordField);
        ThemeManager.styleComboBox(roleComboBox);
        ThemeManager.styleButton(loginButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(exitButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create gradient header panel
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        
        // Title with aviation icon
        JLabel titleLabel = ThemeManager.createTitleLabel("AeroDesk Pro");
        titleLabel.setForeground(ThemeManager.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Subtitle
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Advanced Airport Management System");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ThemeManager.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(ThemeManager.createBodyLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        contentPanel.add(ThemeManager.createBodyLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(passwordField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        contentPanel.add(ThemeManager.createBodyLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(roleComboBox, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Enter key in password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Simple authentication (in real app, this would check database)
        if (authenticateUser(username, password, role)) {
            FileLogger.getInstance().logInfo("User " + username + " logged in successfully as " + role);
            openMainMenu(username, role);
        } else {
            FileLogger.getInstance().logWarning("Failed login attempt for user: " + username);
            JOptionPane.showMessageDialog(this, 
                "Invalid credentials. Please try again.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    private boolean authenticateUser(String username, String password, String role) {
        try {
            User user = userDAO.authenticateUser(username, password, role);
            return user != null;
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Database authentication error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Database connection error. Please check your configuration.", 
                "Authentication Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void openMainMenu(String username, String role) {
        this.dispose(); // Close login window
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame(username, role).setVisible(true);
        });
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        ThemeManager.styleFrame(this);
    }
} 