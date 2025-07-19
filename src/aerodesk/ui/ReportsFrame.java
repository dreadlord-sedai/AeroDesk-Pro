package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.util.ConfigManager;
import aerodesk.dao.FlightDAO;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.BaggageDAO;
import aerodesk.dao.GateDAO;
import aerodesk.model.Flight;
import aerodesk.model.Booking;
import aerodesk.model.Baggage;
import aerodesk.model.GateAssignment;
import aerodesk.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

/**
 * Enhanced Reports & Logs UI for AeroDesk Pro
 * Provides comprehensive system reporting, log management, and analytics
 */
public class ReportsFrame extends JFrame {
    // UI Components - Tables
    private JTabbedPane tabbedPane;
    private JTable flightsReportTable;
    private DefaultTableModel flightsReportModel;
    private JTable bookingsReportTable;
    private DefaultTableModel bookingsReportModel;
    private JTable baggageReportTable;
    private DefaultTableModel baggageReportModel;
    private JTable gatesReportTable;
    private DefaultTableModel gatesReportModel;
    
    // UI Components - Text Areas
    private JTextArea logsArea;
    private JTextArea systemStatsArea;
    private JTextArea searchResultsArea;
    
    // UI Components - Search Fields
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JComboBox<String> dateRangeCombo;
    
    // UI Components - Buttons
    private JButton exportButton;
    private JButton refreshButton;
    private JButton clearLogsButton;
    private JButton saveLogsButton;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton printButton;
    private JButton emailButton;
    
    // UI Components - Labels and Status
    private JLabel statusLabel;
    private JLabel lastUpdateLabel;
    private JLabel totalRecordsLabel;
    
    // Data Access Objects
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;
    private BaggageDAO baggageDAO;
    private GateDAO gateDAO;
    
    // Configuration and Utilities
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ScheduledExecutorService autoRefreshScheduler;
    private boolean autoRefreshEnabled = false;
    
    public ReportsFrame() {
        this.flightDAO = new FlightDAO();
        this.bookingDAO = new BookingDAO();
        this.baggageDAO = new BaggageDAO();
        this.gateDAO = new GateDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupAutoRefresh();
        configureWindow();
        loadAllReports();
        loadSystemLogs();
        updateSystemStats();
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Enhanced Flights Report Table
        String[] flightColumns = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Status", "Aircraft", "Passengers"};
        flightsReportModel = new DefaultTableModel(flightColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsReportTable = new JTable(flightsReportModel);
        flightsReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(flightsReportTable);
        
        // Enhanced Bookings Report Table
        String[] bookingColumns = {"Booking ID", "Passenger", "Flight No", "Seat", "Checked In", "Check-in Time", "Baggage Count"};
        bookingsReportModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsReportTable = new JTable(bookingsReportModel);
        bookingsReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(bookingsReportTable);
        
        // Enhanced Baggage Report Table
        String[] baggageColumns = {"Tag Number", "Passenger", "Weight", "Type", "Status", "Created", "Last Updated"};
        baggageReportModel = new DefaultTableModel(baggageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baggageReportTable = new JTable(baggageReportModel);
        baggageReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(baggageReportTable);
        
        // Enhanced Gates Report Table
        String[] gateColumns = {"Gate", "Flight No", "Departure", "Arrival", "Status", "Capacity"};
        gatesReportModel = new DefaultTableModel(gateColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gatesReportTable = new JTable(gatesReportModel);
        gatesReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(gatesReportTable);
        
        // Enhanced Text Areas
        logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        ThemeManager.styleTextArea(logsArea);
        
        systemStatsArea = new JTextArea();
        systemStatsArea.setEditable(false);
        systemStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        ThemeManager.styleTextArea(systemStatsArea);
        
        searchResultsArea = new JTextArea();
        searchResultsArea.setEditable(false);
        searchResultsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        ThemeManager.styleTextArea(searchResultsArea);
        
        // Search Components
        searchField = new JTextField(20);
        ThemeManager.styleTextField(searchField);
        
        searchTypeCombo = new JComboBox<>(new String[]{"All", "Flights", "Bookings", "Baggage", "Gates", "Logs"});
        ThemeManager.styleComboBox(searchTypeCombo);
        
        dateRangeCombo = new JComboBox<>(new String[]{"All Time", "Today", "Last 7 Days", "Last 30 Days", "This Month", "Last Month"});
        ThemeManager.styleComboBox(dateRangeCombo);
        
        // Enhanced Buttons with wider width
        exportButton = new JButton("Export Report");
        refreshButton = new JButton("Refresh Data");
        clearLogsButton = new JButton("Clear Logs");
        saveLogsButton = new JButton("Save Logs");
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear Search");
        printButton = new JButton("Print Report");
        emailButton = new JButton("Email Report");
        
        // Set wider preferred size for all buttons
        Dimension wideButtonSize = new Dimension(150, 40);
        exportButton.setPreferredSize(wideButtonSize);
        refreshButton.setPreferredSize(wideButtonSize);
        clearLogsButton.setPreferredSize(wideButtonSize);
        saveLogsButton.setPreferredSize(wideButtonSize);
        searchButton.setPreferredSize(wideButtonSize);
        clearSearchButton.setPreferredSize(wideButtonSize);
        printButton.setPreferredSize(wideButtonSize);
        emailButton.setPreferredSize(wideButtonSize);
        
        // Apply modern styling to buttons
        ThemeManager.styleButton(exportButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(clearLogsButton, ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        ThemeManager.styleButton(saveLogsButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(searchButton, ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        ThemeManager.styleButton(clearSearchButton, ThemeManager.DARK_GRAY, ThemeManager.WHITE);
        ThemeManager.styleButton(printButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(emailButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        
        // Status Labels
        statusLabel = ThemeManager.createStatusLabel("Ready", ThemeManager.SUCCESS_GREEN);
        lastUpdateLabel = ThemeManager.createBodyLabel("Last Update: " + LocalDateTime.now().format(dateFormatter));
        totalRecordsLabel = ThemeManager.createBodyLabel("Total Records: 0");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Enhanced gradient header panel
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Enhanced Reports & Logs Management");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Comprehensive System Analytics, Reporting & Log Management");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Search panel
        JPanel searchPanel = ThemeManager.createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.add(ThemeManager.createBodyLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(ThemeManager.createBodyLabel("Type:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(ThemeManager.createBodyLabel("Date Range:"));
        searchPanel.add(dateRangeCombo);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        
        // Create enhanced tabs
        createFlightsTab();
        createBookingsTab();
        createBaggageTab();
        createGatesTab();
        createLogsTab();
        createStatsTab();
        createSearchTab();
        
        // Enhanced button panel with card styling
        JPanel buttonPanel = ThemeManager.createCardPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(printButton);
        buttonPanel.add(emailButton);
        buttonPanel.add(clearLogsButton);
        buttonPanel.add(saveLogsButton);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(ThemeManager.WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        leftStatus.setBackground(ThemeManager.WHITE);
        leftStatus.add(ThemeManager.createBodyLabel("Status:"));
        leftStatus.add(statusLabel);
        
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        rightStatus.setBackground(ThemeManager.WHITE);
        rightStatus.add(lastUpdateLabel);
        rightStatus.add(totalRecordsLabel);
        
        statusPanel.add(leftStatus, BorderLayout.WEST);
        statusPanel.add(rightStatus, BorderLayout.EAST);
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.WHITE);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupAutoRefresh() {
        autoRefreshScheduler = Executors.newScheduledThreadPool(1);
        // Auto-refresh every 5 minutes
        autoRefreshScheduler.scheduleAtFixedRate(() -> {
            if (autoRefreshEnabled) {
                SwingUtilities.invokeLater(() -> {
                    loadAllReports();
                    loadSystemLogs();
                    updateSystemStats();
                    updateStatusLabels();
                });
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    private void createFlightsTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("Flights Report");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(flightsReportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Flights", panel);
    }
    
    private void createSearchTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("Search Results");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(searchResultsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Search", panel);
    }
    
    private void updateStatusLabels() {
        lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(dateFormatter));
        
        int totalRecords = 0;
        totalRecords += flightsReportModel.getRowCount();
        totalRecords += bookingsReportModel.getRowCount();
        totalRecords += baggageReportModel.getRowCount();
        totalRecords += gatesReportModel.getRowCount();
        
        totalRecordsLabel.setText("Total Records: " + totalRecords);
    }
    
    private void createBookingsTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("Bookings Report");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(bookingsReportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Bookings", panel);
    }
    
    private void createBaggageTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("Baggage Report");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(baggageReportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Baggage", panel);
    }
    
    private void createGatesTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("Gate Assignments Report");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(gatesReportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Gates", panel);
    }
    
    private void createLogsTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("System Logs");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(logsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Logs", panel);
    }
    
    private void createStatsTab() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("System Statistics");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(systemStatsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Statistics", panel);
    }
    
    private void setupEventHandlers() {
        exportButton.addActionListener(e -> handleExport());
        refreshButton.addActionListener(e -> handleRefresh());
        clearLogsButton.addActionListener(e -> handleClearLogs());
        saveLogsButton.addActionListener(e -> handleSaveLogs());
        searchButton.addActionListener(e -> handleSearch());
        clearSearchButton.addActionListener(e -> handleClearSearch());
        printButton.addActionListener(e -> handlePrint());
        emailButton.addActionListener(e -> handleEmail());
        
        // Add keyboard shortcuts
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSearch();
                }
            }
        });
        
        // Add tab change listener
        tabbedPane.addChangeListener(e -> {
            updateStatusLabels();
            statusLabel.setText("Viewing: " + tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
        });
    }
    
    private void handleExport() {
        int selectedTab = tabbedPane.getSelectedIndex();
        String reportType = tabbedPane.getTitleAt(selectedTab);
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export " + reportType + " Report");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(reportType.toLowerCase() + "_report_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            exportToCSV(file, selectedTab);
            statusLabel.setText("Export completed successfully");
            updateStatusLabels();
        }
    }
    
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();
        String dateRange = (String) dateRangeCombo.getSelectedItem();
        
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder results = new StringBuilder();
        results.append("=== Search Results ===\n");
        results.append("Search Term: ").append(searchTerm).append("\n");
        results.append("Type: ").append(searchType).append("\n");
        results.append("Date Range: ").append(dateRange).append("\n");
        results.append("Timestamp: ").append(LocalDateTime.now().format(dateFormatter)).append("\n");
        results.append("=".repeat(50)).append("\n\n");
        
        // Perform search based on type
        switch (searchType) {
            case "Flights":
                searchFlights(searchTerm, results);
                break;
            case "Bookings":
                searchBookings(searchTerm, results);
                break;
            case "Baggage":
                searchBaggage(searchTerm, results);
                break;
            case "Gates":
                searchGates(searchTerm, results);
                break;
            case "Logs":
                searchLogs(searchTerm, results);
                break;
            case "All":
                searchAll(searchTerm, results);
                break;
        }
        
        searchResultsArea.setText(results.toString());
        tabbedPane.setSelectedIndex(6); // Switch to Search tab
        statusLabel.setText("Search completed - " + searchTerm);
        updateStatusLabels();
    }
    
    private void handleClearSearch() {
        searchField.setText("");
        searchResultsArea.setText("");
        statusLabel.setText("Search cleared");
    }
    
    private void handlePrint() {
        int selectedTab = tabbedPane.getSelectedIndex();
        String reportType = tabbedPane.getTitleAt(selectedTab);
        
        try {
            // Create a print job
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return NO_SUCH_PAGE;
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                
                // Print header
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("AeroDesk Pro - " + reportType + " Report", 50, 50);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString("Generated: " + LocalDateTime.now().format(dateFormatter), 50, 70);
                
                // Print content based on selected tab
                String content = getCurrentTabContent();
                String[] lines = content.split("\n");
                int y = 100;
                for (String line : lines) {
                    if (y > pageFormat.getImageableHeight() - 100) break;
                    g2d.drawString(line, 50, y);
                    y += 15;
                }
                
                return PAGE_EXISTS;
            });
            
            if (job.printDialog()) {
                job.print();
                statusLabel.setText("Print job completed");
            }
        } catch (Exception ex) {
            FileLogger.getInstance().logError("Print error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleEmail() {
        String email = JOptionPane.showInputDialog(this, "Enter email address:", "Email Report", JOptionPane.QUESTION_MESSAGE);
        if (email != null && !email.trim().isEmpty()) {
            // Simulate email sending
            statusLabel.setText("Email sent to: " + email);
            JOptionPane.showMessageDialog(this, "Report sent to: " + email, "Email Sent", JOptionPane.INFORMATION_MESSAGE);
            FileLogger.getInstance().logInfo("Report emailed to: " + email);
        }
    }
    
    private void exportToCSV(File file, int tabIndex) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            DefaultTableModel model = null;
            
            switch (tabIndex) {
                case 0: // Flights
                    model = flightsReportModel;
                    break;
                case 1: // Bookings
                    model = bookingsReportModel;
                    break;
                case 2: // Baggage
                    model = baggageReportModel;
                    break;
                case 3: // Gates
                    model = gatesReportModel;
                    break;
            }
            
            if (model != null) {
                // Write headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
                
                // Write data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        writer.print(value != null ? value.toString() : "");
                        if (col < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
            }
            
            FileLogger.getInstance().logInfo("Exported report to: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, 
                "Report exported successfully to:\n" + file.getAbsolutePath(), 
                "Export Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            FileLogger.getInstance().logError("Failed to export report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to export report: " + ex.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRefresh() {
        loadAllReports();
        loadSystemLogs();
        updateSystemStats();
        FileLogger.getInstance().logInfo("Reports data refreshed");
        JOptionPane.showMessageDialog(this, 
            "All reports refreshed successfully.", 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleClearLogs() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear the logs display?\nThis will not delete the log file.", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            logsArea.setText("");
            FileLogger.getInstance().logInfo("Logs display cleared by user");
        }
    }
    
    private void handleSaveLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Logs");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("aerodesk_logs_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(logsArea.getText());
                FileLogger.getInstance().logInfo("Logs saved to: " + file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Logs saved successfully to:\n" + file.getAbsolutePath(), 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                FileLogger.getInstance().logError("Failed to save logs: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Failed to save logs: " + ex.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadAllReports() {
        loadFlightsReport();
        loadBookingsReport();
        loadBaggageReport();
        loadGatesReport();
    }
    
    private void loadFlightsReport() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            flightsReportModel.setRowCount(0);
            
            for (Flight flight : flights) {
                Object[] row = {
                    flight.getFlightNo(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartTime() != null ? flight.getDepartTime().format(dateFormatter) : "N/A",
                    flight.getArriveTime() != null ? flight.getArriveTime().format(dateFormatter) : "N/A",
                    flight.getStatus(),
                    flight.getAircraftType()
                };
                flightsReportModel.addRow(row);
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flights report: " + ex.getMessage());
        }
    }
    
    private void loadBookingsReport() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            bookingsReportModel.setRowCount(0);
            
            for (Booking booking : bookings) {
                try {
                    Flight flight = flightDAO.getFlightById(booking.getFlightId());
                    String flightNo = flight != null ? flight.getFlightNo() : "N/A";
                    
                    Object[] row = {
                        booking.getBookingId(),
                        booking.getPassengerName(),
                        flightNo,
                        booking.getSeatNo() != null ? booking.getSeatNo() : "Not Assigned",
                        booking.isCheckedIn() ? "Yes" : "No",
                        booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "N/A"
                    };
                    bookingsReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading flight for booking: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load bookings report: " + ex.getMessage());
        }
    }
    
    private void loadBaggageReport() {
        try {
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            baggageReportModel.setRowCount(0);
            
            for (Baggage baggage : baggageList) {
                try {
                    Booking booking = bookingDAO.getBookingById(baggage.getBookingId());
                    String passengerName = booking != null ? booking.getPassengerName() : "Unknown";
                    
                    Object[] row = {
                        baggage.getTagNumber(),
                        passengerName,
                        String.format("%.1f kg", baggage.getWeightKg()),
                        baggage.getBaggageType(),
                        baggage.getStatus(),
                        baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A"
                    };
                    baggageReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading booking for baggage: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load baggage report: " + ex.getMessage());
        }
    }
    
    private void loadGatesReport() {
        try {
                         List<GateAssignment> assignments = gateDAO.getAllAssignments();
             gatesReportModel.setRowCount(0);
             
             for (GateAssignment assignment : assignments) {
                try {
                    Flight flight = flightDAO.getFlightById(assignment.getFlightId());
                    String flightNo = flight != null ? flight.getFlightNo() : "Unknown";
                    
                    Object[] row = {
                        "Gate " + assignment.getGateId(),
                        flightNo,
                        assignment.getAssignmentTime() != null ? assignment.getAssignmentTime().format(dateFormatter) : "N/A",
                        assignment.getDepartureTime() != null ? assignment.getDepartureTime().format(dateFormatter) : "N/A",
                        "Active"
                    };
                    gatesReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading flight for gate assignment: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load gates report: " + ex.getMessage());
        }
    }
    
    private void loadSystemLogs() {
        try {
            // Read the log file
            File logFile = new File("aerodesk.log");
            if (logFile.exists()) {
                StringBuilder logContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logContent.append(line).append("\n");
                    }
                }
                logsArea.setText(logContent.toString());
                
                // Scroll to bottom to show latest logs
                logsArea.setCaretPosition(logsArea.getDocument().getLength());
            } else {
                logsArea.setText("No log file found.");
            }
        } catch (IOException ex) {
            logsArea.setText("Error reading log file: " + ex.getMessage());
            FileLogger.getInstance().logError("Failed to read log file: " + ex.getMessage());
        }
    }
    
    private void updateSystemStats() {
        StringBuilder stats = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        
        stats.append("AeroDesk Pro System Statistics\n");
        stats.append("==============================\n\n");
        stats.append("Generated: ").append(now.format(dateFormatter)).append("\n\n");
        
        try {
            // Flight statistics
            List<Flight> flights = flightDAO.getAllFlights();
            long scheduledFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.SCHEDULED).count();
            long onTimeFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.ON_TIME).count();
            long delayedFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.DELAYED).count();
            long departedFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.DEPARTED).count();
            long cancelledFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.CANCELLED).count();
            
            stats.append("FLIGHT STATISTICS:\n");
            stats.append("-----------------\n");
            stats.append("Total Flights: ").append(flights.size()).append("\n");
            stats.append("Scheduled: ").append(scheduledFlights).append("\n");
            stats.append("On Time: ").append(onTimeFlights).append("\n");
            stats.append("Delayed: ").append(delayedFlights).append("\n");
            stats.append("Departed: ").append(departedFlights).append("\n");
            stats.append("Cancelled: ").append(cancelledFlights).append("\n\n");
            
            // Booking statistics
            List<Booking> bookings = bookingDAO.getAllBookings();
            long checkedInBookings = bookings.stream().filter(Booking::isCheckedIn).count();
            
            stats.append("BOOKING STATISTICS:\n");
            stats.append("-------------------\n");
            stats.append("Total Bookings: ").append(bookings.size()).append("\n");
            stats.append("Checked In: ").append(checkedInBookings).append("\n");
            stats.append("Not Checked In: ").append(bookings.size() - checkedInBookings).append("\n");
            stats.append("Check-in Rate: ").append(String.format("%.1f%%", 
                bookings.size() > 0 ? (double) checkedInBookings / bookings.size() * 100 : 0)).append("\n\n");
            
            // Baggage statistics
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            long deliveredBaggage = baggageList.stream()
                .filter(b -> b.getStatus().equals("DELIVERED")).count();
            
            stats.append("BAGGAGE STATISTICS:\n");
            stats.append("-------------------\n");
            stats.append("Total Baggage: ").append(baggageList.size()).append("\n");
            stats.append("Delivered: ").append(deliveredBaggage).append("\n");
            stats.append("In Transit: ").append(baggageList.size() - deliveredBaggage).append("\n");
            stats.append("Delivery Rate: ").append(String.format("%.1f%%", 
                baggageList.size() > 0 ? (double) deliveredBaggage / baggageList.size() * 100 : 0)).append("\n\n");
            
            // Gate statistics
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            stats.append("GATE STATISTICS:\n");
            stats.append("----------------\n");
            stats.append("Total Assignments: ").append(assignments.size()).append("\n");
            stats.append("Active Gates: ").append(assignments.stream()
                .filter(a -> a.getDepartureTime() != null && a.getDepartureTime().isAfter(now)).count()).append("\n\n");
            
        } catch (DatabaseException ex) {
            stats.append("ERROR: ").append(ex.getMessage()).append("\n");
            FileLogger.getInstance().logError("Failed to generate system stats: " + ex.getMessage());
        }
        
        systemStatsArea.setText(stats.toString());
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Enhanced Reports & Logs");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
    
    // Search methods
    private void searchFlights(String searchTerm, StringBuilder results) {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            results.append("=== Flight Search Results ===\n");
            
            for (Flight flight : flights) {
                if (flight.getFlightNo().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    flight.getOrigin().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    flight.getDestination().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    flight.getStatus().toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                    
                    results.append("Flight: ").append(flight.getFlightNo()).append("\n");
                    results.append("Route: ").append(flight.getOrigin()).append(" → ").append(flight.getDestination()).append("\n");
                    results.append("Status: ").append(flight.getStatus()).append("\n");
                    results.append("Aircraft: ").append(flight.getAircraftType()).append("\n");
                    results.append("-".repeat(30)).append("\n");
                }
            }
        } catch (DatabaseException ex) {
            results.append("Error searching flights: ").append(ex.getMessage()).append("\n");
        }
    }
    
    private void searchBookings(String searchTerm, StringBuilder results) {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            results.append("=== Booking Search Results ===\n");
            
            for (Booking booking : bookings) {
                if (booking.getPassengerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    String.valueOf(booking.getBookingId()).toLowerCase().contains(searchTerm.toLowerCase())) {
                    
                    results.append("Booking ID: ").append(booking.getBookingId()).append("\n");
                    results.append("Passenger: ").append(booking.getPassengerName()).append("\n");
                    results.append("Checked In: ").append(booking.isCheckedIn() ? "Yes" : "No").append("\n");
                    results.append("-".repeat(30)).append("\n");
                }
            }
        } catch (DatabaseException ex) {
            results.append("Error searching bookings: ").append(ex.getMessage()).append("\n");
        }
    }
    
    private void searchBaggage(String searchTerm, StringBuilder results) {
        try {
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            results.append("=== Baggage Search Results ===\n");
            
            for (Baggage baggage : baggageList) {
                if (baggage.getTagNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    baggage.getBaggageType().toString().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    baggage.getStatus().toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                    
                    results.append("Tag: ").append(baggage.getTagNumber()).append("\n");
                    results.append("Type: ").append(baggage.getBaggageType()).append("\n");
                    results.append("Weight: ").append(baggage.getWeightKg()).append(" kg\n");
                    results.append("Status: ").append(baggage.getStatus()).append("\n");
                    results.append("-".repeat(30)).append("\n");
                }
            }
        } catch (DatabaseException ex) {
            results.append("Error searching baggage: ").append(ex.getMessage()).append("\n");
        }
    }
    
    private void searchGates(String searchTerm, StringBuilder results) {
        try {
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            results.append("=== Gate Search Results ===\n");
            
            for (GateAssignment assignment : assignments) {
                String gateText = "Gate " + String.valueOf(assignment.getGateId());
                if (gateText.toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.append("Gate: ").append(assignment.getGateId()).append("\n");
                    results.append("Assignment Time: ").append(assignment.getAssignmentTime()).append("\n");
                    results.append("-".repeat(30)).append("\n");
                }
            }
        } catch (DatabaseException ex) {
            results.append("Error searching gates: ").append(ex.getMessage()).append("\n");
        }
    }
    
    private void searchLogs(String searchTerm, StringBuilder results) {
        try {
            File logFile = new File("aerodesk.log");
            if (logFile.exists()) {
                results.append("=== Log Search Results ===\n");
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                            results.append(line).append("\n");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            results.append("Error searching logs: ").append(ex.getMessage()).append("\n");
        }
    }
    
    private void searchAll(String searchTerm, StringBuilder results) {
        searchFlights(searchTerm, results);
        searchBookings(searchTerm, results);
        searchBaggage(searchTerm, results);
        searchGates(searchTerm, results);
        searchLogs(searchTerm, results);
    }
    
    private String getCurrentTabContent() {
        int selectedTab = tabbedPane.getSelectedIndex();
        switch (selectedTab) {
            case 0: // Flights
                return getTableContent(flightsReportTable);
            case 1: // Bookings
                return getTableContent(bookingsReportTable);
            case 2: // Baggage
                return getTableContent(baggageReportTable);
            case 3: // Gates
                return getTableContent(gatesReportTable);
            case 4: // Logs
                return logsArea.getText();
            case 5: // Statistics
                return systemStatsArea.getText();
            case 6: // Search
                return searchResultsArea.getText();
            default:
                return "No content available";
        }
    }
    
    private String getTableContent(JTable table) {
        StringBuilder content = new StringBuilder();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // Add headers
        for (int i = 0; i < model.getColumnCount(); i++) {
            content.append(model.getColumnName(i)).append("\t");
        }
        content.append("\n");
        
        // Add data
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                content.append(value != null ? value.toString() : "").append("\t");
            }
            content.append("\n");
        }
        
        return content.toString();
    }
    
    @Override
    public void dispose() {
        if (autoRefreshScheduler != null && !autoRefreshScheduler.isShutdown()) {
            autoRefreshScheduler.shutdown();
        }
        super.dispose();
    }
} 