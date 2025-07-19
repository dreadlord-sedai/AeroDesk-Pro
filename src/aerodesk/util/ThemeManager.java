package aerodesk.util;

import javax.swing.*;
import java.awt.*;

/**
 * Theme Manager for AeroDesk Pro
 * Provides modern aviation-themed styling for the application
 */
public class ThemeManager {
    
    // Aviation-themed color palette
    public static final Color PRIMARY_BLUE = new Color(25, 118, 210);      // Deep sky blue
    public static final Color SECONDARY_BLUE = new Color(66, 165, 245);    // Lighter blue
    public static final Color ACCENT_ORANGE = new Color(255, 152, 0);      // Aviation orange
    public static final Color SUCCESS_GREEN = new Color(76, 175, 80);      // Success green
    public static final Color WARNING_AMBER = new Color(255, 193, 7);      // Warning amber
    public static final Color ERROR_RED = new Color(244, 67, 54);          // Error red
    public static final Color DARK_GRAY = new Color(33, 33, 33);           // Dark gray
    public static final Color LIGHT_GRAY = new Color(245, 245, 245);       // Light gray
    public static final Color WHITE = Color.WHITE;
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    
    // Gradients
    public static final Color GRADIENT_START = new Color(25, 118, 210);
    public static final Color GRADIENT_END = new Color(66, 165, 245);
    
    // Typography
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font MONOSPACE_FONT = new Font("Consolas", Font.PLAIN, 12);
    
    // Dimensions
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 35;
    public static final int PADDING = 15;
    public static final int BORDER_RADIUS = 8;
    
    private static ThemeManager instance;
    
    private ThemeManager() {}
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Apply modern styling to a button
     */
    public static void styleButton(JButton button) {
        styleButton(button, PRIMARY_BLUE, WHITE);
    }
    
    /**
     * Apply modern styling to a button with custom colors
     */
    public static void styleButton(JButton button, Color backgroundColor, Color textColor) {
        button.setFont(BUTTON_FONT);
        button.setForeground(textColor);
        button.setBackground(backgroundColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, BUTTON_HEIGHT));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
    
    /**
     * Apply modern styling to a text field
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(BODY_FONT);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, INPUT_HEIGHT));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    /**
     * Apply modern styling to a password field
     */
    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(BODY_FONT);
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, INPUT_HEIGHT));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    /**
     * Apply modern styling to a combo box
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, INPUT_HEIGHT));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    /**
     * Apply modern styling to a table
     */
    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(30);
        table.setGridColor(LIGHT_GRAY);
        table.setSelectionBackground(SECONDARY_BLUE);
        table.setSelectionForeground(WHITE);
        table.getTableHeader().setFont(SUBHEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
    }
    
    /**
     * Apply modern styling to a text area
     */
    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(MONOSPACE_FONT);
        textArea.setBackground(WHITE);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    /**
     * Apply modern styling to a panel
     */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }
    
    /**
     * Apply modern styling to a titled panel
     */
    public static void styleTitledPanel(JPanel panel, String title) {
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
            title,
            0, 0,
            SUBHEADER_FONT,
            PRIMARY_BLUE
        ));
    }
    
    /**
     * Create a modern title label
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_BLUE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Create a modern header label
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(PRIMARY_BLUE);
        return label;
    }
    
    /**
     * Create a modern subheader label
     */
    public static JLabel createSubheaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBHEADER_FONT);
        label.setForeground(DARK_GRAY);
        return label;
    }
    
    /**
     * Create a modern body label
     */
    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(DARK_GRAY);
        return label;
    }
    
    /**
     * Create a status label with color coding
     */
    public static JLabel createStatusLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Create a modern card panel with shadow effect
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(LIGHT_GRAY, 1)
        ));
        return panel;
    }
    
    /**
     * Create a gradient panel
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, GRADIENT_START,
                    getWidth(), getHeight(), GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
    }
    
    /**
     * Apply modern styling to a frame
     */
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(WHITE);
        frame.setIconImage(createAviationIcon());
    }
    
    /**
     * Create a simple aviation-themed icon
     */
    private static java.awt.Image createAviationIcon() {
        // Create a simple plane icon
        java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple plane shape
        g2d.setColor(PRIMARY_BLUE);
        g2d.fillPolygon(
            new int[]{8, 24, 20, 16, 12, 8},
            new int[]{16, 16, 20, 18, 20, 16},
            6
        );
        
        g2d.dispose();
        return icon;
    }
    
    /**
     * Get status color based on status string
     */
    public static Color getStatusColor(String status) {
        if (status == null) return DARK_GRAY;
        
        switch (status.toUpperCase()) {
            case "ON_TIME":
            case "COMPLETED":
            case "DELIVERED":
                return SUCCESS_GREEN;
            case "DELAYED":
            case "IN_TRANSIT":
                return WARNING_AMBER;
            case "CANCELLED":
            case "LOST":
                return ERROR_RED;
            case "BOARDING":
            case "LOADED":
                return SECONDARY_BLUE;
            default:
                return DARK_GRAY;
        }
    }
} 