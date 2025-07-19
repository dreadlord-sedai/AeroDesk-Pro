package aerodesk.ui;

import aerodesk.util.ConfigManager;
import aerodesk.util.FileLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login screen for AeroDesk Pro
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton exitButton;
    
    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[]{"STAFF", "ADMIN"});
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        
        // Set preferred sizes
        usernameField.setPreferredSize(new Dimension(200, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        loginButton.setPreferredSize(new Dimension(100, 35));
        exitButton.setPreferredSize(new Dimension(100, 35));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("AeroDesk Pro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
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
        // Simple hardcoded authentication for demo
        if ("admin".equals(username) && "admin123".equals(password) && "ADMIN".equals(role)) {
            return true;
        }
        if (("staff1".equals(username) || "staff2".equals(username)) && 
            "staff123".equals(password) && "STAFF".equals(role)) {
            return true;
        }
        return false;
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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
} 