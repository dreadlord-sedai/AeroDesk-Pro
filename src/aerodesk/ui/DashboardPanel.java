package aerodesk.ui;

import aerodesk.model.KPI;
import aerodesk.service.DashboardMetrics;
import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Dashboard Panel for displaying real-time KPI metrics
 * Provides live statistics and visualizations for airport operations
 */
public class DashboardPanel extends JPanel {
    
    private final DashboardMetrics metrics;
    private final Map<String, JLabel> kpiLabels;
    private final Map<String, JLabel> statusLabels;
    private JLabel lastUpdateLabel;
    private JTextArea summaryArea;
    private final ScheduledExecutorService updateScheduler;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public DashboardPanel() {
        this.metrics = DashboardMetrics.getInstance();
        this.kpiLabels = new java.util.HashMap<>();
        this.statusLabels = new java.util.HashMap<>();
        this.updateScheduler = Executors.newScheduledThreadPool(1);
        
        initializeComponents();
        setupLayout();
        startAutoUpdate();
        
        FileLogger.getInstance().logInfo("Dashboard panel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        setBackground(ThemeManager.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create KPI display labels
        String[] kpiKeys = {
            "total_flights", "checkins_hour", "baggage_handled", "gates_occupied",
            "on_time_flights", "delayed_flights", "cancelled_flights", "avg_delay"
        };
        
        for (String key : kpiKeys) {
            kpiLabels.put(key, createKPILabel());
            statusLabels.put(key, createStatusLabel());
        }
        
        // Last update label
        lastUpdateLabel = new JLabel("Last Update: " + LocalDateTime.now().format(timeFormatter));
        lastUpdateLabel.setFont(ThemeManager.SUBHEADER_FONT);
        lastUpdateLabel.setForeground(ThemeManager.DARK_GRAY);
        
        // Summary area
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(ThemeManager.BODY_FONT);
        summaryArea.setBackground(ThemeManager.LIGHT_GRAY);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(ThemeManager.WHITE);
        
        // KPI grid panel
        JPanel kpiGridPanel = createKPIGridPanel();
        contentPanel.add(kpiGridPanel, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Live Operations Dashboard");
        titleLabel.setFont(ThemeManager.TITLE_FONT);
        titleLabel.setForeground(ThemeManager.WHITE);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(lastUpdateLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create KPI grid panel
     */
    private JPanel createKPIGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        gridPanel.setBackground(ThemeManager.WHITE);
        
        // Create KPI cards
        gridPanel.add(createKPICard("Total Flights Today", "total_flights", "flights"));
        gridPanel.add(createKPICard("Check-ins (Last Hour)", "checkins_hour", "passengers"));
        gridPanel.add(createKPICard("Baggage Handled", "baggage_handled", "units"));
        gridPanel.add(createKPICard("Gates Occupied", "gates_occupied", "gates"));
        gridPanel.add(createKPICard("On-Time Flights", "on_time_flights", "%"));
        gridPanel.add(createKPICard("Delayed Flights", "delayed_flights", "flights"));
        gridPanel.add(createKPICard("Cancelled Flights", "cancelled_flights", "flights"));
        gridPanel.add(createKPICard("Average Delay", "avg_delay", "minutes"));
        
        return gridPanel;
    }
    
    /**
     * Create individual KPI card
     */
    private JPanel createKPICard(String title, String kpiKey, String unit) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(ThemeManager.LIGHT_GRAY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.SUBHEADER_FONT);
        titleLabel.setForeground(ThemeManager.DARK_GRAY);
        
        // Value
        JLabel valueLabel = kpiLabels.get(kpiKey);
        valueLabel.setText("0 " + unit);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(ThemeManager.PRIMARY_BLUE);
        
        // Status indicator
        JLabel statusLabel = statusLabels.get(kpiKey);
        statusLabel.setText("⚪");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create summary panel
     */
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(ThemeManager.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_GRAY),
            "Operations Summary",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            ThemeManager.SUBHEADER_FONT,
            ThemeManager.DARK_GRAY
        ));
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = new JButton("Refresh Now");
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        ThemeManager.styleButton(refreshBtn, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        refreshBtn.addActionListener(e -> refreshData());
        
        JButton exportBtn = new JButton("Export Report");
        exportBtn.setPreferredSize(new Dimension(120, 35));
        ThemeManager.styleButton(exportBtn, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        exportBtn.addActionListener(e -> exportReport());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);
        
        // Summary area with scroll
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        summaryPanel.add(buttonPanel, BorderLayout.NORTH);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        
        return summaryPanel;
    }
    
    /**
     * Create KPI label
     */
    private JLabel createKPILabel() {
        JLabel label = new JLabel("0");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Create status label
     */
    private JLabel createStatusLabel() {
        JLabel label = new JLabel("⚪");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Start automatic updates
     */
    private void startAutoUpdate() {
        updateScheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::refreshData);
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    /**
     * Refresh dashboard data
     */
    public void refreshData() {
        try {
            Map<String, KPI> kpis = metrics.getAllKPIs();
            
            for (Map.Entry<String, KPI> entry : kpis.entrySet()) {
                String key = entry.getKey();
                KPI kpi = entry.getValue();
                
                JLabel valueLabel = kpiLabels.get(key);
                JLabel statusLabel = statusLabels.get(key);
                
                if (valueLabel != null && statusLabel != null) {
                    valueLabel.setText(kpi.getFormattedValue());
                    statusLabel.setText(kpi.getStatusIcon());
                    
                    // Update colors based on status
                    switch (kpi.getStatus()) {
                        case EXCELLENT:
                            valueLabel.setForeground(ThemeManager.SUCCESS_GREEN);
                            break;
                        case GOOD:
                            valueLabel.setForeground(ThemeManager.PRIMARY_BLUE);
                            break;
                        case WARNING:
                            valueLabel.setForeground(ThemeManager.WARNING_AMBER);
                            break;
                        case CRITICAL:
                            valueLabel.setForeground(ThemeManager.ERROR_RED);
                            break;
                        default:
                            valueLabel.setForeground(ThemeManager.DARK_GRAY);
                    }
                }
            }
            
            // Update summary
            summaryArea.setText(metrics.getKPISummary());
            
            // Update timestamp
            lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(timeFormatter));
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error refreshing dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Export dashboard report
     */
    private void exportReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Dashboard Report");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new java.io.File("dashboard_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                
                writer.println("=== AeroDesk Dashboard Report ===");
                writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println();
                writer.println(metrics.getKPISummary());
                
                writer.close();
                
                JOptionPane.showMessageDialog(this,
                    "Dashboard report exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                FileLogger.getInstance().logInfo("Dashboard report exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error exporting dashboard report: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error exporting report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (updateScheduler != null && !updateScheduler.isShutdown()) {
            updateScheduler.shutdown();
            try {
                if (!updateScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    updateScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                updateScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        FileLogger.getInstance().logInfo("Dashboard panel cleanup completed");
    }
} 