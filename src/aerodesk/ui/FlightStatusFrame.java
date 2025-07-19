package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.dao.FlightDAO;
import aerodesk.dao.GateDAO;
import aerodesk.model.Flight;
import aerodesk.model.GateAssignment;
import aerodesk.exception.DatabaseException;
import aerodesk.util.ApiIntegrator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Enhanced Flight Status Dashboard for AeroDesk Pro
 * Provides real-time flight tracking, weather information, and advanced API integration
 */
public class FlightStatusFrame extends JFrame {
    
    // Table components
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    private TableRowSorter<DefaultTableModel> flightsTableSorter;
    
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> statusFilterComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton refreshButton;
    
    // Live update controls
    private JButton startLiveUpdatesButton;
    private JButton stopLiveUpdatesButton;
    private JLabel lastUpdateLabel;
    private JLabel liveStatusLabel;
    
    // Information panels
    private JTextArea weatherArea;
    private JTextArea systemStatusArea;
    private JTextArea apiStatusArea;
    private JLabel weatherLabel;
    private JLabel apiStatusLabel;
    
    // Weather location selector
    private JComboBox<String> weatherLocationComboBox;
    private JButton refreshWeatherButton;
    
    // Tabbed pane for information panels
    private JTabbedPane informationTabbedPane;
    
    // Progress and status
    private JProgressBar progressBar;
    private JLabel statsLabel;
    private JLabel connectionStatusLabel;
    
    // Data access objects
    private FlightDAO flightDAO;
    private GateDAO gateDAO;
    private ApiIntegrator apiIntegrator;
    private ScheduledExecutorService updateScheduler;
    
    // State management
    private boolean liveUpdatesActive = false;
    private Flight selectedFlight;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    // Timer for search debouncing
    private Timer searchTimer;
    
    public FlightStatusFrame() {
        this.flightDAO = new FlightDAO();
        this.gateDAO = new GateDAO();
        this.apiIntegrator = new ApiIntegrator();
        this.updateScheduler = Executors.newScheduledThreadPool(2);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadFlightData();
        loadWeatherData();
        updateSystemStatus();
        updateApiStatus();
    }
    
    private void initializeComponents() {
        // Flights table with enhanced columns
        String[] flightColumns = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Status", "Gate", "Delay", "Aircraft"};
        flightsTableModel = new DefaultTableModel(flightColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setRowHeight(25);
        ThemeManager.styleTable(flightsTable);
        
        // Table sorter for advanced filtering
        flightsTableSorter = new TableRowSorter<>(flightsTableModel);
        flightsTable.setRowSorter(flightsTableSorter);
        
        // Search components
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        ThemeManager.styleTextField(searchField);
        
        searchTypeComboBox = new JComboBox<>(new String[]{"Flight No", "Origin", "Destination", "Aircraft"});
        ThemeManager.styleComboBox(searchTypeComboBox);
        
        statusFilterComboBox = new JComboBox<>(new String[]{"All Statuses", "SCHEDULED", "ON_TIME", "DELAYED", "DEPARTED", "CANCELLED"});
        ThemeManager.styleComboBox(statusFilterComboBox);
        
        // Buttons with enhanced styling
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");
        refreshButton = new JButton("Refresh Data");
        startLiveUpdatesButton = new JButton("Start Live Updates");
        stopLiveUpdatesButton = new JButton("Stop Live Updates");
        stopLiveUpdatesButton.setEnabled(false);
        
        // Weather controls
        weatherLocationComboBox = new JComboBox<>(new String[]{
            "New York (JFK)", "Los Angeles (LAX)", "Chicago (ORD)", 
            "Atlanta (ATL)", "Dallas (DFW)", "Miami (MIA)", 
            "Denver (DEN)", "Seattle (SEA)", "San Francisco (SFO)",
            "Boston (BOS)", "Las Vegas (LAS)", "Phoenix (PHX)"
        });
        refreshWeatherButton = new JButton("Refresh Weather");
        
        ThemeManager.styleComboBox(weatherLocationComboBox);
        ThemeManager.styleButton(refreshWeatherButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        refreshWeatherButton.setPreferredSize(new Dimension(140, 30));
        
        // Apply modern button styling
        ThemeManager.styleButton(searchButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(clearSearchButton, ThemeManager.DARK_GRAY, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(startLiveUpdatesButton, ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        ThemeManager.styleButton(stopLiveUpdatesButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        // Make buttons wider
        searchButton.setPreferredSize(new Dimension(100, 35));
        clearSearchButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setPreferredSize(new Dimension(140, 35));
        startLiveUpdatesButton.setPreferredSize(new Dimension(160, 35));
        stopLiveUpdatesButton.setPreferredSize(new Dimension(160, 35));
        
        // Information areas
        weatherArea = new JTextArea();
        weatherArea.setEditable(false);
        weatherArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ThemeManager.styleTextArea(weatherArea);
        
        systemStatusArea = new JTextArea();
        systemStatusArea.setEditable(false);
        systemStatusArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ThemeManager.styleTextArea(systemStatusArea);
        
        apiStatusArea = new JTextArea();
        apiStatusArea.setEditable(false);
        apiStatusArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ThemeManager.styleTextArea(apiStatusArea);
        
        // Tabbed pane for information
        informationTabbedPane = new JTabbedPane();
        informationTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Labels
        lastUpdateLabel = ThemeManager.createStatusLabel("Last Update: Never", ThemeManager.DARK_GRAY);
        liveStatusLabel = ThemeManager.createStatusLabel("Live Updates: Inactive", ThemeManager.WARNING_AMBER);
        weatherLabel = ThemeManager.createStatusLabel("Weather: Loading...", ThemeManager.PRIMARY_BLUE);
        apiStatusLabel = ThemeManager.createStatusLabel("API Status: Checking...", ThemeManager.PRIMARY_BLUE);
        statsLabel = ThemeManager.createStatusLabel("Flights: 0 | Active: 0", ThemeManager.DARK_GRAY);
        connectionStatusLabel = ThemeManager.createStatusLabel("Database: Connected", ThemeManager.SUCCESS_GREEN);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 8));
        
        // Search timer
        searchTimer = new Timer(500, e -> performSearch());
        searchTimer.setRepeats(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Enhanced header panel
        JPanel headerPanel = createEnhancedHeader();
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBackground(ThemeManager.LIGHT_GRAY);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = createEnhancedSearchPanel();
        
        // Center panel with flights table
        JPanel centerPanel = createEnhancedCenterPanel();
        
        // Right panel with tabbed information
        JPanel rightPanel = createEnhancedRightPanel();
        
        // Bottom panel with controls
        JPanel bottomPanel = createEnhancedBottomPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        mainContentPanel.add(searchPanel, BorderLayout.NORTH);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        mainContentPanel.add(rightPanel, BorderLayout.EAST);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedHeader() {
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel titleLabel = ThemeManager.createTitleLabel("Flight Status Dashboard");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Real-time Flight Tracking & Weather Information");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel titlePanel = new JPanel(new BorderLayout(5, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createEnhancedSearchPanel() {
        JPanel searchPanel = ThemeManager.createCardPanel();
        searchPanel.setLayout(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Search controls
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchControlsPanel.setOpaque(false);
        
        searchControlsPanel.add(ThemeManager.createBodyLabel("Search:"));
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(ThemeManager.createBodyLabel("Type:"));
        searchControlsPanel.add(searchTypeComboBox);
        searchControlsPanel.add(ThemeManager.createBodyLabel("Status:"));
        searchControlsPanel.add(statusFilterComboBox);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(clearSearchButton);
        
        // Status indicators
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(connectionStatusLabel);
        statusPanel.add(statsLabel);
        
        searchPanel.add(searchControlsPanel, BorderLayout.CENTER);
        searchPanel.add(statusPanel, BorderLayout.SOUTH);
        
        return searchPanel;
    }
    
    private JPanel createEnhancedCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(ThemeManager.LIGHT_GRAY);
        
        // Flights table panel
        JPanel flightsPanel = ThemeManager.createCardPanel();
        flightsPanel.setLayout(new BorderLayout());
        flightsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table header
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(ThemeManager.WHITE);
        tableHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitleLabel = ThemeManager.createSubheaderLabel("Flight Status Information");
        tableHeaderPanel.add(tableTitleLabel, BorderLayout.WEST);
        
        // Table with enhanced styling
        JScrollPane flightsScrollPane = new JScrollPane(flightsTable);
        flightsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        flightsScrollPane.setPreferredSize(new Dimension(800, 400));
        
        flightsPanel.add(tableHeaderPanel, BorderLayout.NORTH);
        flightsPanel.add(flightsScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(flightsPanel, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createEnhancedRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeManager.LIGHT_GRAY);
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Create tabbed pane with information panels
        setupInformationTabs();
        
        rightPanel.add(informationTabbedPane, BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    private void setupInformationTabs() {
        // Weather Tab
        JPanel weatherTabPanel = new JPanel(new BorderLayout());
        weatherTabPanel.setBackground(ThemeManager.WHITE);
        weatherTabPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Weather header with title
        JPanel weatherTitlePanel = new JPanel(new BorderLayout());
        weatherTitlePanel.setBackground(ThemeManager.WHITE);
        weatherTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel weatherTitleLabel = ThemeManager.createSubheaderLabel("Weather Information");
        weatherTitlePanel.add(weatherTitleLabel, BorderLayout.CENTER);
        
        // Weather controls panel - separate from title
        JPanel weatherControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        weatherControlsPanel.setBackground(ThemeManager.WHITE);
        weatherControlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        weatherControlsPanel.add(ThemeManager.createBodyLabel("Location:"));
        weatherControlsPanel.add(weatherLocationComboBox);
        weatherControlsPanel.add(refreshWeatherButton);
        
        // Combine title and controls in a vertical layout
        JPanel weatherHeaderPanel = new JPanel(new BorderLayout(0, 5));
        weatherHeaderPanel.setBackground(ThemeManager.WHITE);
        weatherHeaderPanel.add(weatherTitlePanel, BorderLayout.NORTH);
        weatherHeaderPanel.add(weatherControlsPanel, BorderLayout.CENTER);
        
        JScrollPane weatherScrollPane = new JScrollPane(weatherArea);
        weatherScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        weatherTabPanel.add(weatherHeaderPanel, BorderLayout.NORTH);
        weatherTabPanel.add(weatherScrollPane, BorderLayout.CENTER);
        weatherTabPanel.add(weatherLabel, BorderLayout.SOUTH);
        
        // System Status Tab
        JPanel systemTabPanel = new JPanel(new BorderLayout());
        systemTabPanel.setBackground(ThemeManager.WHITE);
        systemTabPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel systemTitleLabel = ThemeManager.createSubheaderLabel("System Status");
        JScrollPane systemScrollPane = new JScrollPane(systemStatusArea);
        systemScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        systemTabPanel.add(systemTitleLabel, BorderLayout.NORTH);
        systemTabPanel.add(systemScrollPane, BorderLayout.CENTER);
        
        // API Status Tab
        JPanel apiTabPanel = new JPanel(new BorderLayout());
        apiTabPanel.setBackground(ThemeManager.WHITE);
        apiTabPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel apiTitleLabel = ThemeManager.createSubheaderLabel("API Status");
        JScrollPane apiScrollPane = new JScrollPane(apiStatusArea);
        apiScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        apiTabPanel.add(apiTitleLabel, BorderLayout.NORTH);
        apiTabPanel.add(apiScrollPane, BorderLayout.CENTER);
        apiTabPanel.add(apiStatusLabel, BorderLayout.SOUTH);
        
        // Add tabs to tabbed pane
        informationTabbedPane.addTab("Weather", null, weatherTabPanel, "Weather information and controls");
        informationTabbedPane.addTab("System Status", null, systemTabPanel, "System status and performance");
        informationTabbedPane.addTab("API Status", null, apiTabPanel, "API connectivity and status");
        
        // Set tab icons (optional - using text instead of icons as requested)
        informationTabbedPane.setTabPlacement(JTabbedPane.TOP);
    }
    
    private JPanel createEnhancedBottomPanel() {
        JPanel bottomPanel = ThemeManager.createCardPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(startLiveUpdatesButton);
        buttonPanel.add(stopLiveUpdatesButton);
        
        // Status information
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setOpaque(false);
        
        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftStatusPanel.setOpaque(false);
        leftStatusPanel.add(lastUpdateLabel);
        leftStatusPanel.add(liveStatusLabel);
        leftStatusPanel.add(progressBar);
        
        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightStatusPanel.setOpaque(false);
        rightStatusPanel.add(ThemeManager.createBodyLabel("Double-click flight for details"));
        
        statusPanel.add(leftStatusPanel, BorderLayout.WEST);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        refreshButton.addActionListener(e -> handleRefresh());
        startLiveUpdatesButton.addActionListener(e -> handleStartLiveUpdates());
        stopLiveUpdatesButton.addActionListener(e -> handleStopLiveUpdates());
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        refreshWeatherButton.addActionListener(e -> handleRefreshWeather());
        
        // Weather location change handler
        weatherLocationComboBox.addActionListener(e -> handleWeatherLocationChange());
        
        // Search field key listener for real-time search
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {}
            
            @Override
            public void keyReleased(KeyEvent e) {
                searchTimer.restart();
            }
        });
        
        // Combo box change listeners
        searchTypeComboBox.addActionListener(e -> performSearch());
        statusFilterComboBox.addActionListener(e -> performSearch());
        
        // Table selection and double-click handlers
        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleFlightSelection();
            }
        });
        
        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleFlightDoubleClick();
                }
            }
        });
    }
    
    private void handleRefresh() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadFlightData();
                loadWeatherData();
                updateSystemStatus();
                updateApiStatus();
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(dateFormatter));
                FileLogger.getInstance().logInfo("Flight status data refreshed manually");
            }
        };
        worker.execute();
    }
    
    private void handleStartLiveUpdates() {
        liveUpdatesActive = true;
        startLiveUpdatesButton.setEnabled(false);
        stopLiveUpdatesButton.setEnabled(true);
        liveStatusLabel.setText("Live Updates: Active");
        liveStatusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
        
        // Schedule periodic updates every 30 seconds
        updateScheduler.scheduleAtFixedRate(() -> {
            if (liveUpdatesActive) {
                SwingUtilities.invokeLater(() -> {
                    loadFlightData();
                    loadWeatherData();
                    updateSystemStatus();
                    updateApiStatus();
                    lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(dateFormatter));
                });
            }
        }, 30, 30, TimeUnit.SECONDS);
        
        FileLogger.getInstance().logInfo("Live flight status updates started");
        JOptionPane.showMessageDialog(this, 
            "Live updates started. Data will refresh every 30 seconds.", 
            "Live Updates", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleStopLiveUpdates() {
        liveUpdatesActive = false;
        startLiveUpdatesButton.setEnabled(true);
        stopLiveUpdatesButton.setEnabled(false);
        liveStatusLabel.setText("Live Updates: Inactive");
        liveStatusLabel.setForeground(ThemeManager.WARNING_AMBER);
        
        FileLogger.getInstance().logInfo("Live flight status updates stopped");
        JOptionPane.showMessageDialog(this, 
            "Live updates stopped.", 
            "Live Updates", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String searchType = searchTypeComboBox.getSelectedItem().toString();
        String statusFilter = statusFilterComboBox.getSelectedItem().toString();
        
        // Clear existing filters
        flightsTableSorter.setRowFilter(null);
        
        // Create combined filter
        RowFilter<DefaultTableModel, Object> combinedFilter = null;
        
        // Apply search filter
        if (!searchText.isEmpty()) {
            int columnIndex = getColumnIndex(searchType);
            if (columnIndex >= 0) {
                combinedFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), columnIndex);
            }
        }
        
        // Apply status filter
        if (!"All Statuses".equals(statusFilter)) {
            RowFilter<DefaultTableModel, Object> statusRowFilter = RowFilter.regexFilter(statusFilter, 5); // Status column
            
            if (combinedFilter != null) {
                combinedFilter = RowFilter.andFilter(java.util.Arrays.asList(combinedFilter, statusRowFilter));
            } else {
                combinedFilter = statusRowFilter;
            }
        }
        
        // Apply the combined filter
        if (combinedFilter != null) {
            flightsTableSorter.setRowFilter(combinedFilter);
        }
        
        updateStats();
    }
    
    private void clearSearch() {
        searchField.setText("");
        searchTypeComboBox.setSelectedIndex(0);
        statusFilterComboBox.setSelectedIndex(0);
        flightsTableSorter.setRowFilter(null);
        updateStats();
    }
    
    private int getColumnIndex(String columnName) {
        switch (columnName) {
            case "Flight No": return 0;
            case "Origin": return 1;
            case "Destination": return 2;
            case "Aircraft": return 8;
            default: return -1;
        }
    }
    
    private void handleFlightSelection() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = flightsTable.convertRowIndexToModel(selectedRow);
            String flightNo = flightsTableModel.getValueAt(modelRow, 0).toString();
            try {
                selectedFlight = flightDAO.getFlightByNumber(flightNo);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Error getting selected flight: " + ex.getMessage());
            }
        }
    }
    
    private void handleFlightDoubleClick() {
        if (selectedFlight != null) {
            showEnhancedFlightDetails(selectedFlight);
        }
    }
    
    private void showEnhancedFlightDetails(Flight flight) {
        StringBuilder details = new StringBuilder();
        details.append("Flight Details for ").append(flight.getFlightNo()).append("\n");
        details.append("=====================================\n\n");
        details.append("Origin: ").append(flight.getOrigin()).append("\n");
        details.append("Destination: ").append(flight.getDestination()).append("\n");
        details.append("Aircraft: ").append(flight.getAircraftType()).append("\n");
        details.append("Status: ").append(flight.getStatus()).append("\n\n");
        
        if (flight.getDepartTime() != null) {
            details.append("Departure: ").append(flight.getDepartTime().format(dateFormatter)).append("\n");
        }
        if (flight.getArriveTime() != null) {
            details.append("Arrival: ").append(flight.getArriveTime().format(dateFormatter)).append("\n");
        }
        
        // Get gate assignment
        try {
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            for (GateAssignment assignment : assignments) {
                if (assignment.getFlightId() == flight.getFlightId()) {
                    details.append("Gate: ").append(assignment.getGateId()).append("\n");
                    break;
                }
            }
        } catch (DatabaseException ex) {
            details.append("Gate: Not assigned\n");
        }
        
        // Calculate delay information
        String delayInfo = calculateEnhancedDelay(flight);
        details.append("Delay Status: ").append(delayInfo).append("\n");
        
        // Try to get real-time API data
        try {
            String apiData = ApiIntegrator.getFlightData(flight.getFlightNo());
            if (apiData != null && !apiData.contains("mock")) {
                details.append("\nReal-time API Data:\n");
                details.append("API Status: Available\n");
                details.append("Live Tracking: Active\n");
            } else {
                details.append("\nReal-time API Data:\n");
                details.append("API Status: Using Mock Data\n");
                details.append("Live Tracking: Simulated\n");
            }
        } catch (Exception ex) {
            details.append("\nReal-time API Data:\n");
            details.append("API Status: Unavailable\n");
            details.append("Live Tracking: Offline\n");
        }
        
        JTextArea detailsArea = new JTextArea(details.toString());
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Flight Details - " + flight.getFlightNo(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadFlightData() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            flightsTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                try {
                    // Get gate assignment
                    String gateInfo = "Not Assigned";
                    List<GateAssignment> assignments = gateDAO.getAllAssignments();
                    for (GateAssignment assignment : assignments) {
                        if (assignment.getFlightId() == flight.getFlightId()) {
                            gateInfo = "Gate " + assignment.getGateId();
                            break;
                        }
                    }
                    
                    // Calculate enhanced delay
                    String delayInfo = calculateEnhancedDelay(flight);
                    
                    Object[] row = {
                        flight.getFlightNo(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartTime() != null ? flight.getDepartTime().format(shortDateFormatter) : "N/A",
                        flight.getArriveTime() != null ? flight.getArriveTime().format(shortDateFormatter) : "N/A",
                        flight.getStatus(),
                        gateInfo,
                        delayInfo,
                        flight.getAircraftType()
                    };
                    flightsTableModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading gate info for flight: " + ex.getMessage());
                }
            }
            
            updateStats();
            FileLogger.getInstance().logInfo("Loaded " + flights.size() + " flights for status display");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flight data: " + ex.getMessage());
            connectionStatusLabel.setText("Database: Error");
            connectionStatusLabel.setForeground(ThemeManager.ERROR_RED);
        }
    }
    
    private String calculateEnhancedDelay(Flight flight) {
        if (flight.getDepartTime() == null) {
            return "N/A";
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledDeparture = flight.getDepartTime();
        
        if (now.isAfter(scheduledDeparture)) {
            long delayMinutes = java.time.Duration.between(scheduledDeparture, now).toMinutes();
            if (delayMinutes > 0) {
                if (delayMinutes < 60) {
                    return delayMinutes + " min late";
                } else {
                    long hours = delayMinutes / 60;
                    long remainingMinutes = delayMinutes % 60;
                    return hours + "h " + remainingMinutes + "m late";
                }
            }
        } else if (now.isBefore(scheduledDeparture)) {
            long earlyMinutes = java.time.Duration.between(now, scheduledDeparture).toMinutes();
            if (earlyMinutes < 15) {
                return "Boarding";
            } else if (earlyMinutes < 60) {
                return "On time";
            } else {
                long hours = earlyMinutes / 60;
                long remainingMinutes = earlyMinutes % 60;
                return hours + "h " + remainingMinutes + "m early";
            }
        }
        
        return "On time";
    }
    
    private void loadWeatherData() {
        try {
            String selectedLocation = weatherLocationComboBox.getSelectedItem().toString();
            String cityName = extractCityName(selectedLocation);
            
            String weatherInfo = apiIntegrator.getWeatherData(cityName);
            weatherArea.setText(formatWeatherData(weatherInfo, selectedLocation));
            weatherLabel.setText("Weather: " + selectedLocation + " - Updated");
            weatherLabel.setForeground(ThemeManager.SUCCESS_GREEN);
        } catch (Exception ex) {
            String selectedLocation = weatherLocationComboBox.getSelectedItem().toString();
            weatherArea.setText(getMockWeatherForLocation(selectedLocation));
            weatherLabel.setText("Weather: " + selectedLocation + " - Using Mock Data");
            weatherLabel.setForeground(ThemeManager.WARNING_AMBER);
            FileLogger.getInstance().logError("Error loading weather data: " + ex.getMessage());
        }
    }
    
    private String extractCityName(String locationString) {
        // Extract city name from "City (CODE)" format
        if (locationString.contains("(")) {
            return locationString.substring(0, locationString.indexOf("(")).trim();
        }
        return locationString;
    }
    
    private String formatWeatherData(String weatherJson, String location) {
        // Simple formatting for weather data
        return "Weather Information for " + location + "\n" +
               "=====================================\n\n" +
               weatherJson + "\n\n" +
               "Last Updated: " + LocalDateTime.now().format(dateFormatter);
    }
    
    private String getMockWeatherForLocation(String location) {
        // Generate different mock weather for different locations
        String cityName = extractCityName(location);
        
        switch (cityName) {
            case "New York":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 18°C\n" +
                       "Conditions: Partly Cloudy\n" +
                       "Wind: 20 km/h NE\n" +
                       "Visibility: 12 km\n" +
                       "Humidity: 70%\n" +
                       "Pressure: 1012 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Los Angeles":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 25°C\n" +
                       "Conditions: Sunny\n" +
                       "Wind: 8 km/h W\n" +
                       "Visibility: 15 km\n" +
                       "Humidity: 45%\n" +
                       "Pressure: 1015 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Chicago":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 12°C\n" +
                       "Conditions: Cloudy\n" +
                       "Wind: 25 km/h NW\n" +
                       "Visibility: 8 km\n" +
                       "Humidity: 80%\n" +
                       "Pressure: 1008 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Atlanta":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 22°C\n" +
                       "Conditions: Clear\n" +
                       "Wind: 12 km/h S\n" +
                       "Visibility: 16 km\n" +
                       "Humidity: 60%\n" +
                       "Pressure: 1014 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Dallas":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 28°C\n" +
                       "Conditions: Sunny\n" +
                       "Wind: 15 km/h SW\n" +
                       "Visibility: 14 km\n" +
                       "Humidity: 55%\n" +
                       "Pressure: 1010 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Miami":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 30°C\n" +
                       "Conditions: Partly Cloudy\n" +
                       "Wind: 18 km/h SE\n" +
                       "Visibility: 13 km\n" +
                       "Humidity: 75%\n" +
                       "Pressure: 1013 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Denver":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 15°C\n" +
                       "Conditions: Clear\n" +
                       "Wind: 22 km/h W\n" +
                       "Visibility: 18 km\n" +
                       "Humidity: 40%\n" +
                       "Pressure: 1005 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Seattle":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 14°C\n" +
                       "Conditions: Rainy\n" +
                       "Wind: 16 km/h SW\n" +
                       "Visibility: 6 km\n" +
                       "Humidity: 85%\n" +
                       "Pressure: 1010 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "San Francisco":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 16°C\n" +
                       "Conditions: Foggy\n" +
                       "Wind: 12 km/h NW\n" +
                       "Visibility: 4 km\n" +
                       "Humidity: 80%\n" +
                       "Pressure: 1012 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Boston":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 16°C\n" +
                       "Conditions: Partly Cloudy\n" +
                       "Wind: 18 km/h NE\n" +
                       "Visibility: 10 km\n" +
                       "Humidity: 65%\n" +
                       "Pressure: 1011 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Las Vegas":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 32°C\n" +
                       "Conditions: Sunny\n" +
                       "Wind: 8 km/h NW\n" +
                       "Visibility: 20 km\n" +
                       "Humidity: 25%\n" +
                       "Pressure: 1008 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            case "Phoenix":
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 35°C\n" +
                       "Conditions: Sunny\n" +
                       "Wind: 10 km/h SW\n" +
                       "Visibility: 18 km\n" +
                       "Humidity: 20%\n" +
                       "Pressure: 1006 hPa\n\n" +
                       "Note: Using simulated weather data";
                       
            default:
                return "Weather Information for " + location + "\n" +
                       "=====================================\n\n" +
                       "Temperature: 22°C\n" +
                       "Conditions: Partly Cloudy\n" +
                       "Wind: 15 km/h NW\n" +
                       "Visibility: 10 km\n" +
                       "Humidity: 65%\n" +
                       "Pressure: 1013 hPa\n\n" +
                       "Note: Using simulated weather data";
        }
    }
    
    private void updateSystemStatus() {
        StringBuilder status = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        
        status.append("System Status Report\n");
        status.append("===================\n\n");
        status.append("Timestamp: ").append(now.format(dateFormatter)).append("\n");
        status.append("Live Updates: ").append(liveUpdatesActive ? "Active" : "Inactive").append("\n");
        
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            long activeFlights = flights.stream()
                .filter(f -> f.getStatus() == Flight.FlightStatus.ON_TIME || 
                           f.getStatus() == Flight.FlightStatus.DEPARTED ||
                           f.getStatus() == Flight.FlightStatus.DELAYED)
                .count();
            
            long delayedFlights = flights.stream()
                .filter(f -> f.getStatus() == Flight.FlightStatus.DELAYED)
                .count();
            
            status.append("Total Flights: ").append(flights.size()).append("\n");
            status.append("Active Flights: ").append(activeFlights).append("\n");
            status.append("Delayed Flights: ").append(delayedFlights).append("\n");
            
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            status.append("Gate Assignments: ").append(assignments.size()).append("\n");
            
            connectionStatusLabel.setText("Database: Connected");
            connectionStatusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
            
        } catch (DatabaseException ex) {
            status.append("Database Status: Error - ").append(ex.getMessage()).append("\n");
            connectionStatusLabel.setText("Database: Error");
            connectionStatusLabel.setForeground(ThemeManager.ERROR_RED);
        }
        
        status.append("\nSystem Memory: ").append(Runtime.getRuntime().totalMemory() / 1024 / 1024).append(" MB\n");
        status.append("Free Memory: ").append(Runtime.getRuntime().freeMemory() / 1024 / 1024).append(" MB\n");
        status.append("Last Refresh: ").append(now.format(dateFormatter)).append("\n");
        
        systemStatusArea.setText(status.toString());
    }
    
    private void updateApiStatus() {
        StringBuilder status = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        
        status.append("API Status Report\n");
        status.append("=================\n\n");
        
        // Check Aviation Stack API
        boolean aviationStackAvailable = apiIntegrator.isAviationStackAvailable();
        status.append("Aviation Stack API: ").append(aviationStackAvailable ? "Available" : "Unavailable").append("\n");
        
        // Check general API availability
        boolean apiAvailable = apiIntegrator.isApiAvailable();
        status.append("Weather API: ").append(apiAvailable ? "Available" : "Unavailable").append("\n");
        
        // API response times (simulated)
        status.append("Avg Response Time: ").append(aviationStackAvailable ? "~2.5s" : "N/A").append("\n");
        status.append("Last API Call: ").append(now.format(dateFormatter)).append("\n");
        
        // API usage statistics
        status.append("\nAPI Usage:\n");
        status.append("- Flight Tracking: ").append(aviationStackAvailable ? "Active" : "Offline").append("\n");
        status.append("- Weather Data: ").append(apiAvailable ? "Active" : "Mock Data").append("\n");
        status.append("- Live Updates: ").append(liveUpdatesActive ? "Enabled" : "Disabled").append("\n");
        
        apiStatusArea.setText(status.toString());
        
        if (aviationStackAvailable && apiAvailable) {
            apiStatusLabel.setText("API Status: All Systems Operational");
            apiStatusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
        } else if (aviationStackAvailable || apiAvailable) {
            apiStatusLabel.setText("API Status: Partial Availability");
            apiStatusLabel.setForeground(ThemeManager.WARNING_AMBER);
        } else {
            apiStatusLabel.setText("API Status: Using Mock Data");
            apiStatusLabel.setForeground(ThemeManager.ERROR_RED);
        }
    }
    
    private void updateStats() {
        int totalRows = flightsTableModel.getRowCount();
        int visibleRows = flightsTable.getRowCount();
        
        statsLabel.setText(String.format("Flights: %d | Visible: %d", totalRows, visibleRows));
    }
    
    private void handleRefreshWeather() {
        loadWeatherData();
    }
    
    private void handleWeatherLocationChange() {
        loadWeatherData();
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Enhanced Flight Status Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
        
        // Set initial button states
        stopLiveUpdatesButton.setEnabled(false);
    }
    
    @Override
    public void dispose() {
        // Clean up resources
        if (updateScheduler != null && !updateScheduler.isShutdown()) {
            liveUpdatesActive = false;
            updateScheduler.shutdown();
        }
        if (searchTimer != null) {
            searchTimer.stop();
        }
        super.dispose();
    }
} 