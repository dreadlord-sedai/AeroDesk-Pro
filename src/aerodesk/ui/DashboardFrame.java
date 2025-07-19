package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main Dashboard Frame integrating live KPI metrics and flight status map
 * Provides comprehensive airport operations monitoring interface
 */
public class DashboardFrame extends JFrame {
    
    private final DashboardPanel dashboardPanel;
    private final MapPanel mapPanel;
    private final JTabbedPane tabbedPane;
    
    public DashboardFrame() {
        this.dashboardPanel = new DashboardPanel();
        this.mapPanel = new MapPanel();
        this.tabbedPane = new JTabbedPane();
        
        initializeFrame();
        setupLayout();
        setupEventHandlers();
        
        FileLogger.getInstance().logInfo("Dashboard frame initialized");
    }
    
    /**
     * Initialize frame properties
     */
    private void initializeFrame() {
        setTitle("AeroDesk Pro - Live Operations Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        ThemeManager.styleFrame(this);
    }
    
    /**
     * Setup frame layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content with tabs
        tabbedPane.setFont(ThemeManager.SUBHEADER_FONT);
        tabbedPane.setBackground(ThemeManager.WHITE);
        
        // Add tabs
        tabbedPane.addTab("Live KPIs", null, dashboardPanel, "Real-time Key Performance Indicators");
        tabbedPane.addTab("Flight Map", null, mapPanel, "Interactive Flight Status & Weather Map");
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Create header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("AeroDesk Pro - Live Operations Dashboard");
        titleLabel.setFont(ThemeManager.TITLE_FONT);
        titleLabel.setForeground(ThemeManager.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton refreshAllBtn = new JButton("Refresh All");
        refreshAllBtn.setPreferredSize(new Dimension(120, 35));
        ThemeManager.styleButton(refreshAllBtn, ThemeManager.WHITE, ThemeManager.PRIMARY_BLUE);
        refreshAllBtn.addActionListener(e -> refreshAllData());
        
        JButton exportBtn = new JButton("Export Report");
        exportBtn.setPreferredSize(new Dimension(120, 35));
        ThemeManager.styleButton(exportBtn, ThemeManager.WHITE, ThemeManager.SUCCESS_GREEN);
        exportBtn.addActionListener(e -> exportComprehensiveReport());
        
        buttonPanel.add(refreshAllBtn);
        buttonPanel.add(exportBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create status bar
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ThemeManager.LIGHT_GRAY);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel statusLabel = new JLabel("Dashboard ready - Monitoring airport operations");
        statusLabel.setFont(ThemeManager.BODY_FONT);
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        
        JLabel timeLabel = new JLabel("Started: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeLabel.setFont(ThemeManager.BODY_FONT);
        timeLabel.setForeground(ThemeManager.DARK_GRAY);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Create tab icon
     */
    private JLabel createTabIcon(String icon) {
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        return iconLabel;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }
    
    /**
     * Refresh all dashboard data
     */
    private void refreshAllData() {
        try {
            dashboardPanel.refreshData();
            mapPanel.refreshData();
            
            FileLogger.getInstance().logInfo("All dashboard data refreshed");
            
            JOptionPane.showMessageDialog(this,
                "Dashboard data refreshed successfully!",
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error refreshing dashboard data: " + e.getMessage());
            
            JOptionPane.showMessageDialog(this,
                "Error refreshing data: " + e.getMessage(),
                "Refresh Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Export comprehensive dashboard report
     */
    private void exportComprehensiveReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Comprehensive Dashboard Report");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new java.io.File("comprehensive_dashboard_report_" + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                
                writer.println("=== AeroDesk Pro Comprehensive Dashboard Report ===");
                writer.println("Generated: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("=".repeat(60));
                writer.println();
                
                // KPI Summary
                writer.println("=== KEY PERFORMANCE INDICATORS ===");
                writer.println("Dashboard metrics and operational statistics");
                writer.println();
                
                // Flight Status Summary
                writer.println("=== FLIGHT STATUS SUMMARY ===");
                writer.println("Current flight operations and status overview");
                writer.println();
                
                // Weather Summary
                writer.println("=== WEATHER CONDITIONS ===");
                writer.println("Current weather conditions at major airports");
                writer.println();
                
                // System Status
                writer.println("=== SYSTEM STATUS ===");
                writer.println("Dashboard system status and last update times");
                writer.println();
                
                writer.close();
                
                JOptionPane.showMessageDialog(this,
                    "Comprehensive dashboard report exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                FileLogger.getInstance().logInfo("Comprehensive dashboard report exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error exporting comprehensive report: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error exporting report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        try {
            if (dashboardPanel != null) {
                dashboardPanel.cleanup();
            }
            if (mapPanel != null) {
                mapPanel.cleanup();
            }
            
            FileLogger.getInstance().logInfo("Dashboard frame cleanup completed");
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error during dashboard cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create and display dashboard
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            });
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error starting dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 