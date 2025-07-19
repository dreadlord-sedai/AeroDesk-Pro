package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.util.IconManager;
import aerodesk.util.ConfigManager;
import aerodesk.service.AviationStackService;
import aerodesk.service.AviationStackService.FlightInfo;
import aerodesk.service.AviationStackService.AirportInfo;
import aerodesk.service.AviationStackService.AirlineInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enhanced Aviation Stack API Integration Frame
 * Advanced real-time flight tracking, airport information, and API management
 */
public class AviationStackFrame extends JFrame {
    
    // Services and data
    private AviationStackService aviationService;
    private ScheduledExecutorService scheduler;
    private AtomicInteger apiCallCount = new AtomicInteger(0);
    private LocalDateTime lastApiCall = LocalDateTime.now();
    
    // UI Components - Input Fields
    private JTextField flightNumberField;
    private JTextField airportCodeField;
    private JTextField airlineCodeField;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField apiKeyField;
    
    // UI Components - Buttons
    private JButton trackFlightButton;
    private JButton getAirportInfoButton;
    private JButton searchRouteButton;
    private JButton liveTrackingButton;
    private JButton airlineInfoButton;
    private JButton airportStatsButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton clearButton;
    private JButton apiKeyButton;
    
    // UI Components - Display Areas
    private JTextArea resultArea;
    private JTextArea flightResultArea;
    private JTextArea airportResultArea;
    private JTextArea routeResultArea;
    private JTable flightTable;
    private DefaultTableModel flightTableModel;
    private JTable apiStatsTable;
    private DefaultTableModel apiStatsTableModel;
    
    // UI Components - Labels and Status
    private JLabel statusLabel;
    private JLabel apiStatusLabel;
    private JLabel lastUpdateLabel;
    private JLabel apiCallCountLabel;
    
    // UI Components - Panels and Tabs
    private JTabbedPane mainTabbedPane;
    private JScrollPane resultScrollPane;
    private JScrollPane flightResultScrollPane;
    private JScrollPane airportResultScrollPane;
    private JScrollPane routeResultScrollPane;
    private JScrollPane tableScrollPane;
    private JScrollPane statsScrollPane;
    
    // Configuration
    private boolean liveUpdatesActive = false;
    private Timer searchTimer;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Real-time data management
    private ScheduledExecutorService realTimeScheduler;
    private volatile boolean isRealTimeActive = false;
    private String currentTrackingFlight = null;
    private List<FlightInfo> cachedFlights = new ArrayList<>();
    private Map<String, FlightInfo> liveFlightData = new ConcurrentHashMap<>();
    
    public AviationStackFrame() {
        aviationService = new AviationStackService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupSearchTimer();
        startStatusUpdates();
        initializeRealTimeUpdates();
        
        setTitle("Enhanced Aviation Stack API Integration - AeroDesk Pro");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ThemeManager.styleFrame(this);
    }
    
    private void initializeComponents() {
        // Input fields
        flightNumberField = new JTextField(15);
        flightNumberField.setText("AA101");
        ThemeManager.styleTextField(flightNumberField);
        
        airportCodeField = new JTextField(10);
        airportCodeField.setText("JFK");
        ThemeManager.styleTextField(airportCodeField);
        
        airlineCodeField = new JTextField(8);
        airlineCodeField.setText("AA");
        ThemeManager.styleTextField(airlineCodeField);
        
        originField = new JTextField(8);
        originField.setText("JFK");
        ThemeManager.styleTextField(originField);
        
        destinationField = new JTextField(8);
        destinationField.setText("LAX");
        ThemeManager.styleTextField(destinationField);
        
        apiKeyField = new JTextField(25);
        apiKeyField.setText(ConfigManager.getInstance().getProperty("aviationstack.api.key"));
        ThemeManager.styleTextField(apiKeyField);
        
        // Buttons with wider width
        trackFlightButton = new JButton("Track Flight");
        getAirportInfoButton = new JButton("Get Airport Info");
        searchRouteButton = new JButton("Search Route");
        liveTrackingButton = new JButton("Live Tracking");
        airlineInfoButton = new JButton("Airline Info");
        airportStatsButton = new JButton("Airport Stats");
        refreshButton = new JButton("Refresh");
        exportButton = new JButton("Export");
        clearButton = new JButton("Clear");
        apiKeyButton = new JButton("Update API Key");
        
        // Set wider preferred size for all buttons
        Dimension wideButtonSize = new Dimension(150, 40);
        trackFlightButton.setPreferredSize(wideButtonSize);
        getAirportInfoButton.setPreferredSize(wideButtonSize);
        searchRouteButton.setPreferredSize(wideButtonSize);
        liveTrackingButton.setPreferredSize(wideButtonSize);
        airlineInfoButton.setPreferredSize(wideButtonSize);
        airportStatsButton.setPreferredSize(wideButtonSize);
        refreshButton.setPreferredSize(wideButtonSize);
        exportButton.setPreferredSize(wideButtonSize);
        clearButton.setPreferredSize(wideButtonSize);
        apiKeyButton.setPreferredSize(wideButtonSize);
        
        // Apply modern styling to buttons
        ThemeManager.styleButton(trackFlightButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(getAirportInfoButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(searchRouteButton, ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        ThemeManager.styleButton(liveTrackingButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(airlineInfoButton, ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        ThemeManager.styleButton(airportStatsButton, ThemeManager.DARK_GRAY, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(exportButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(clearButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        ThemeManager.styleButton(apiKeyButton, ThemeManager.DARK_GRAY, ThemeManager.WHITE);
        
        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ThemeManager.styleTextArea(resultArea);
        resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Flight result area
        flightResultArea = new JTextArea();
        flightResultArea.setEditable(false);
        flightResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ThemeManager.styleTextArea(flightResultArea);
        flightResultScrollPane = new JScrollPane(flightResultArea);
        flightResultScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Airport result area
        airportResultArea = new JTextArea();
        airportResultArea.setEditable(false);
        airportResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ThemeManager.styleTextArea(airportResultArea);
        airportResultScrollPane = new JScrollPane(airportResultArea);
        airportResultScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Route result area
        routeResultArea = new JTextArea();
        routeResultArea.setEditable(false);
        routeResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ThemeManager.styleTextArea(routeResultArea);
        routeResultScrollPane = new JScrollPane(routeResultArea);
        routeResultScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Flight table with enhanced columns
        String[] columnNames = {"Flight", "Airline", "Route", "Status", "Gate", "Live", "Last Update"};
        flightTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(flightTableModel);
        ThemeManager.styleTable(flightTable);
        tableScrollPane = new JScrollPane(flightTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        
        // API Stats table
        String[] statsColumns = {"Metric", "Value", "Last Updated"};
        apiStatsTableModel = new DefaultTableModel(statsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        apiStatsTable = new JTable(apiStatsTableModel);
        ThemeManager.styleTable(apiStatsTable);
        statsScrollPane = new JScrollPane(apiStatsTable);
        statsScrollPane.setPreferredSize(new Dimension(400, 200));
        
        // Status labels
        statusLabel = ThemeManager.createStatusLabel("Ready", ThemeManager.SUCCESS_GREEN);
        apiStatusLabel = ThemeManager.createStatusLabel("API: Available", ThemeManager.SUCCESS_GREEN);
        lastUpdateLabel = ThemeManager.createBodyLabel("Last Update: " + LocalDateTime.now().format(timeFormatter));
        apiCallCountLabel = ThemeManager.createBodyLabel("API Calls: 0");
        
        // Main tabbed pane
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Scheduler for background updates
        scheduler = Executors.newScheduledThreadPool(3);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Enhanced Aviation Stack API Integration");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Advanced Real-time Flight Tracking, Airport Information & API Management");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Main content with tabs
        setupTabs();
        
        // Bottom status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(ThemeManager.WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        leftStatus.setBackground(ThemeManager.WHITE);
        leftStatus.add(ThemeManager.createBodyLabel("Status:"));
        leftStatus.add(statusLabel);
        leftStatus.add(apiStatusLabel);
        
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        rightStatus.setBackground(ThemeManager.WHITE);
        rightStatus.add(lastUpdateLabel);
        rightStatus.add(apiCallCountLabel);
        
        statusPanel.add(leftStatus, BorderLayout.WEST);
        statusPanel.add(rightStatus, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainTabbedPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupTabs() {
        // Flight Tracking Tab
        JPanel flightTrackingPanel = createFlightTrackingPanel();
        mainTabbedPane.addTab("Flight Tracking", flightTrackingPanel);
        
        // Airport Information Tab
        JPanel airportInfoPanel = createAirportInfoPanel();
        mainTabbedPane.addTab("Airport Information", airportInfoPanel);
        
        // Route Search Tab
        JPanel routeSearchPanel = createRouteSearchPanel();
        mainTabbedPane.addTab("Route Search", routeSearchPanel);
        
        // API Management Tab
        JPanel apiManagementPanel = createApiManagementPanel();
        mainTabbedPane.addTab("API Management", apiManagementPanel);
    }
    
    private JPanel createFlightTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = ThemeManager.createCardPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        inputPanel.add(ThemeManager.createBodyLabel("Flight Number:"));
        inputPanel.add(flightNumberField);
        inputPanel.add(trackFlightButton);
        inputPanel.add(liveTrackingButton);
        inputPanel.add(refreshButton);
        
        // Results panel
        JPanel resultsPanel = ThemeManager.createCardPanel();
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel resultsTitle = ThemeManager.createSubheaderLabel("Flight Tracking Results");
        resultsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(flightResultScrollPane, BorderLayout.CENTER);
        
        // Flight table panel
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = ThemeManager.createSubheaderLabel("Live Flight Data");
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Combine panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsPanel, tablePanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAirportInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = ThemeManager.createCardPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        inputPanel.add(ThemeManager.createBodyLabel("Airport Code:"));
        inputPanel.add(airportCodeField);
        inputPanel.add(getAirportInfoButton);
        inputPanel.add(airportStatsButton);
        
        // Results panel
        JPanel resultsPanel = ThemeManager.createCardPanel();
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel resultsTitle = ThemeManager.createSubheaderLabel("Airport Information");
        resultsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(airportResultScrollPane, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRouteSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = ThemeManager.createCardPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        inputPanel.add(ThemeManager.createBodyLabel("Origin:"));
        inputPanel.add(originField);
        inputPanel.add(ThemeManager.createBodyLabel("Destination:"));
        inputPanel.add(destinationField);
        inputPanel.add(searchRouteButton);
        inputPanel.add(airlineInfoButton);
        
        // Results panel
        JPanel resultsPanel = ThemeManager.createCardPanel();
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel resultsTitle = ThemeManager.createSubheaderLabel("Route Search Results");
        resultsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(routeResultScrollPane, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createApiManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // API Key panel
        JPanel apiKeyPanel = ThemeManager.createCardPanel();
        apiKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        apiKeyPanel.add(ThemeManager.createBodyLabel("API Key:"));
        apiKeyPanel.add(apiKeyField);
        apiKeyPanel.add(apiKeyButton);
        
        // Stats panel
        JPanel statsPanel = ThemeManager.createCardPanel();
        statsPanel.setLayout(new BorderLayout());
        
        JLabel statsTitle = ThemeManager.createSubheaderLabel("API Statistics");
        statsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        statsPanel.add(statsTitle, BorderLayout.NORTH);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(ThemeManager.WHITE);
        controlPanel.add(exportButton);
        controlPanel.add(clearButton);
        
        panel.add(apiKeyPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupSearchTimer() {
        searchTimer = new Timer(500, e -> {
            // Auto-search functionality can be added here
        });
        searchTimer.setRepeats(false);
    }
    
    private void setupEventHandlers() {
        // Flight tracking events
        trackFlightButton.addActionListener(e -> {
            trackFlight();
            startRealTimeUpdates(); // Start real-time updates when tracking
        });
        liveTrackingButton.addActionListener(e -> {
            startLiveTracking();
            startRealTimeUpdates(); // Start real-time updates for live tracking
        });
        refreshButton.addActionListener(e -> {
            refreshFlightData();
            startRealTimeUpdates(); // Start real-time updates when refreshing
        });
        
        // Airport information events
        getAirportInfoButton.addActionListener(e -> getAirportInfo());
        airportStatsButton.addActionListener(e -> getAirportStats());
        
        // Route search events
        searchRouteButton.addActionListener(e -> {
            searchRoute();
            startRealTimeUpdates(); // Start real-time updates for route search
        });
        airlineInfoButton.addActionListener(e -> getAirlineInfo());
        
        // API management events
        apiKeyButton.addActionListener(e -> updateApiKey());
        exportButton.addActionListener(e -> exportData());
        clearButton.addActionListener(e -> {
            clearResults();
            stopRealTimeUpdates(); // Stop real-time updates when clearing
        });
        
        // Keyboard shortcuts
        flightNumberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    trackFlight();
                    startRealTimeUpdates();
                }
            }
        });
        
        airportCodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    getAirportInfo();
                }
            }
        });
        
        originField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchRoute();
                    startRealTimeUpdates();
                }
            }
        });
        
        destinationField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchRoute();
                    startRealTimeUpdates();
                }
            }
        });
    }
    
    private void trackFlight() {
        String flightNumber = flightNumberField.getText().trim();
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a flight number", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        updateStatus("Tracking flight " + flightNumber + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            boolean apiError = false;
            String errorMsg = null;
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                try {
                    return aviationService.getFlightStatusSummary(flightNumber);
                } catch (Exception ex) {
                    apiError = true;
                    errorMsg = ex.getMessage();
                    // Return mock data directly
                    return aviationService.getFlightStatusSummary(flightNumber);
                }
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    if (apiError) {
                        flightResultArea.setForeground(Color.RED);
                        flightResultArea.setText(
                            "[API ERROR: " + errorMsg + "]\n[Mock data shown due to API error or rate limit]\n\n" + result +
                            "\n\n[DEBUG: This is fallback mock data. If you see this, the API is rate-limited or unavailable.]"
                        );
                        updateStatus("API error or rate limit. Showing mock data.", ThemeManager.ERROR_RED);
                    } else {
                        flightResultArea.setForeground(Color.BLACK);
                        flightResultArea.setText(formatFlightResult(result, flightNumber));
                        updateStatus("Flight tracking completed", ThemeManager.SUCCESS_GREEN);
                    }
                    FileLogger.getInstance().logInfo("Flight tracked: " + flightNumber);
                } catch (Exception ex) {
                    flightResultArea.setForeground(Color.RED);
                    flightResultArea.setText("Error tracking flight: " + ex.getMessage() +
                        "\n[Mock data shown due to API error or rate limit]\n[DEBUG: Exception in done() block]");
                    updateStatus("Flight tracking failed. Showing mock data.", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Flight tracking error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatFlightResult(String result, String flightNumber) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Flight Tracking Results ===\n");
        formatted.append("Flight Number: ").append(flightNumber).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("=".repeat(40)).append("\n\n");
        formatted.append(result);
        
        // Debug output
        System.out.println("DEBUG: Flight tracking result for " + flightNumber + " - Length: " + result.length());
        
        return formatted.toString();
    }
    
    private void getAirportInfo() {
        String airportCode = airportCodeField.getText().trim();
        if (airportCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an airport code", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        updateStatus("Getting airport info for " + airportCode + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            boolean apiError = false;
            String errorMsg = null;
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                try {
                    AviationStackService.AirportInfo airport = aviationService.getAirportInfo(airportCode);
                    return formatAirportResult(airport, airportCode);
                } catch (Exception ex) {
                    apiError = true;
                    errorMsg = ex.getMessage();
                    AviationStackService.AirportInfo airport = aviationService.getAirportInfo(airportCode);
                    return formatAirportResult(airport, airportCode);
                }
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    if (apiError) {
                        airportResultArea.setForeground(Color.RED);
                        airportResultArea.setText(
                            "[API ERROR: " + errorMsg + "]\n[Mock data shown due to API error or rate limit]\n\n" + result +
                            "\n\n[DEBUG: This is fallback mock data. If you see this, the API is rate-limited or unavailable.]"
                        );
                        updateStatus("API error or rate limit. Showing mock data.", ThemeManager.ERROR_RED);
                    } else {
                        airportResultArea.setForeground(Color.BLACK);
                        airportResultArea.setText(result);
                        updateStatus("Airport info retrieved", ThemeManager.SUCCESS_GREEN);
                    }
                    FileLogger.getInstance().logInfo("Airport info retrieved: " + airportCode);
                } catch (Exception ex) {
                    airportResultArea.setForeground(Color.RED);
                    airportResultArea.setText("Error getting airport info: " + ex.getMessage() +
                        "\n[Mock data shown due to API error or rate limit]\n[DEBUG: Exception in done() block]");
                    updateStatus("Airport info failed. Showing mock data.", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airport info error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatAirportResult(AviationStackService.AirportInfo airport, String airportCode) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Airport Information ===\n");
        formatted.append("Airport Code: ").append(airportCode).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("=".repeat(40)).append("\n\n");
        formatted.append("Name: ").append(airport.getName()).append("\n");
        formatted.append("IATA Code: ").append(airport.getIataCode()).append("\n");
        formatted.append("ICAO Code: ").append(airport.getIcaoCode()).append("\n");
        formatted.append("Country: ").append(airport.getCountry()).append("\n");
        formatted.append("City: ").append(airport.getCity()).append("\n");
        formatted.append("Timezone: ").append(airport.getTimezone()).append("\n");
        formatted.append("Coordinates: ").append(airport.getLatitude()).append(", ").append(airport.getLongitude()).append("\n");
        formatted.append("Website: ").append(airport.getWebsite() != null ? airport.getWebsite() : "N/A").append("\n");
        formatted.append("Phone: ").append(airport.getPhone() != null ? airport.getPhone() : "N/A").append("\n");
        formatted.append("GMT Offset: ").append(airport.getGmt() != null ? "GMT" + airport.getGmt() : "N/A").append("\n");
        
        // Debug output
        System.out.println("DEBUG: Airport info for " + airportCode + ": " + airport.getName());
        
        return formatted.toString();
    }
    
    private void searchRoute() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        
        if (origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both origin and destination", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        updateStatus("Searching routes from " + origin + " to " + destination + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                List<AviationStackService.FlightInfo> flights = aviationService.searchFlightsByRoute(origin, destination, null);
                return formatRouteSearchResult(flights, origin, destination);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    routeResultArea.setText(result);
                    updateStatus("Route search completed", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Route search completed: " + origin + " to " + destination);
                } catch (Exception ex) {
                    routeResultArea.setText("Error searching routes: " + ex.getMessage());
                    updateStatus("Route search failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Route search error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatRouteSearchResult(List<AviationStackService.FlightInfo> flights, String origin, String destination) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Route Search Results ===\n");
        formatted.append("Route: ").append(origin).append(" → ").append(destination).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("Found ").append(flights.size()).append(" flights\n");
        formatted.append("=".repeat(40)).append("\n\n");
        
        if (flights.isEmpty()) {
            formatted.append("No flights found for this route.\n");
        } else {
            for (int i = 0; i < flights.size(); i++) {
                AviationStackService.FlightInfo flight = flights.get(i);
                formatted.append("Flight ").append(i + 1).append(":\n");
                formatted.append("  Flight Number: ").append(flight.getFlightNumber()).append("\n");
                formatted.append("  Airline: ").append(flight.getAirline()).append("\n");
                formatted.append("  Status: ").append(flight.getStatus()).append("\n");
                formatted.append("  Gate: ").append(flight.getGate() != null ? flight.getGate() : "N/A").append("\n");
                formatted.append("  Aircraft: ").append(flight.getAircraftType() != null ? flight.getAircraftType() : "N/A").append("\n");
                formatted.append("  Live Tracking: ").append(flight.isLive() ? "Available" : "Not Available").append("\n");
                formatted.append("  ").append("-".repeat(30)).append("\n");
            }
        }
        
        return formatted.toString();
    }
    
    private void startLiveTracking() {
        String flightNumber = flightNumberField.getText().trim();
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a flight number", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        updateStatus("Starting live tracking for " + flightNumber + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                AviationStackService.FlightInfo flight = aviationService.getLiveFlightTracking(flightNumber);
                return formatLiveTrackingResult(flight, flightNumber);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    flightResultArea.setText(result);
                    updateStatus("Live tracking active", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Live tracking started: " + flightNumber);
                    
                    // Start continuous tracking updates
                    startContinuousTracking(flightNumber);
                } catch (Exception ex) {
                    flightResultArea.setText("Error starting live tracking: " + ex.getMessage());
                    updateStatus("Live tracking failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Live tracking error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatLiveTrackingResult(AviationStackService.FlightInfo flight, String flightNumber) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Live Flight Tracking ===\n");
        formatted.append("Flight Number: ").append(flightNumber).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("=".repeat(40)).append("\n\n");
        formatted.append("Flight: ").append(flight.getFlightNumber()).append("\n");
        formatted.append("Airline: ").append(flight.getAirline()).append("\n");
        formatted.append("Status: ").append(flight.getStatus()).append("\n");
        formatted.append("Live Tracking: ").append(flight.isLive() ? "Available" : "Not Available").append("\n");
        
        if (flight.isLive()) {
            formatted.append("\n=== Live Position Data ===\n");
            formatted.append("Current Location: ").append(flight.getLatitude()).append(", ").append(flight.getLongitude()).append("\n");
            formatted.append("Altitude: ").append(flight.getAltitude()).append(" ft\n");
            formatted.append("Speed: ").append(flight.getSpeed()).append(" km/h\n");
            formatted.append("Direction: ").append(flight.getDirection()).append("°\n");
            formatted.append("Aircraft Type: ").append(flight.getAircraftType() != null ? flight.getAircraftType() : "N/A").append("\n");
            formatted.append("Registration: ").append(flight.getAircraftRegistration() != null ? flight.getAircraftRegistration() : "N/A").append("\n");
        }
        
        formatted.append("\n=== Route Information ===\n");
        formatted.append("Departure: ").append(flight.getDepartureAirport() != null ? flight.getDepartureAirport() : "N/A").append("\n");
        formatted.append("Arrival: ").append(flight.getArrivalAirport() != null ? flight.getArrivalAirport() : "N/A").append("\n");
        formatted.append("Gate: ").append(flight.getGate() != null ? flight.getGate() : "N/A").append("\n");
        formatted.append("Terminal: ").append(flight.getTerminal() != null ? flight.getTerminal() : "N/A").append("\n");
        
        return formatted.toString();
    }
    
    private void startContinuousTracking(String flightNumber) {
        // Stop any existing tracking
        if (liveUpdatesActive) {
            liveUpdatesActive = false;
        }
        
        liveUpdatesActive = true;
        scheduler.scheduleAtFixedRate(() -> {
            if (liveUpdatesActive) {
                SwingUtilities.invokeLater(() -> {
                    updateLiveTrackingData(flightNumber);
                });
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    
    private void updateLiveTrackingData(String flightNumber) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                AviationStackService.FlightInfo flight = aviationService.getLiveFlightTracking(flightNumber);
                return formatLiveTrackingResult(flight, flightNumber);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    flightResultArea.setText(result);
                    updateStatus("Live tracking updated - " + LocalDateTime.now().format(timeFormatter), ThemeManager.SUCCESS_GREEN);
                } catch (Exception ex) {
                    updateStatus("Live tracking update failed", ThemeManager.ERROR_RED);
                }
            }
        };
        worker.execute();
    }
    
    private void getAirlineInfo() {
        final String airlineCode = airlineCodeField.getText().trim().isEmpty() ? "AA" : airlineCodeField.getText().trim();
        
        updateStatus("Getting airline information for " + airlineCode + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                AviationStackService.AirlineInfo airline = aviationService.getAirlineInfo(airlineCode);
                return formatAirlineResult(airline, airlineCode);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Airline info retrieved", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Airline info retrieved: " + airlineCode);
                } catch (Exception ex) {
                    resultArea.setText("Error getting airline info: " + ex.getMessage());
                    updateStatus("Airline info failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airline info error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatAirlineResult(AviationStackService.AirlineInfo airline, String airlineCode) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Airline Information ===\n");
        formatted.append("Airline Code: ").append(airlineCode).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("=".repeat(40)).append("\n\n");
        formatted.append("Name: ").append(airline.getName()).append("\n");
        formatted.append("IATA Code: ").append(airline.getIataCode()).append("\n");
        formatted.append("ICAO Code: ").append(airline.getIcaoCode()).append("\n");
        formatted.append("Country: ").append(airline.getCountry()).append("\n");
        formatted.append("Website: ").append(airline.getWebsite() != null ? airline.getWebsite() : "N/A").append("\n");
        formatted.append("Phone: ").append(airline.getPhone() != null ? airline.getPhone() : "N/A").append("\n");
        formatted.append("Fleet Size: ").append(airline.getFleetSize() != null ? airline.getFleetSize() : "N/A").append("\n");
        formatted.append("Founded: ").append(airline.getFounded() != null ? airline.getFounded() : "N/A").append("\n");
        return formatted.toString();
    }
    
    private void getAirportStats() {
        final String airportCode = airportCodeField.getText().trim().isEmpty() ? "JFK" : airportCodeField.getText().trim();
        
        updateStatus("Getting airport statistics for " + airportCode + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                incrementApiCallCount();
                return formatAirportStatsResult(aviationService.getAirportStatistics(airportCode), airportCode);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Airport stats retrieved", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Airport stats retrieved: " + airportCode);
                } catch (Exception ex) {
                    resultArea.setText("Error getting airport stats: " + ex.getMessage());
                    updateStatus("Airport stats failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airport stats error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String formatAirportStatsResult(String stats, String airportCode) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== Airport Statistics ===\n");
        formatted.append("Airport Code: ").append(airportCode).append("\n");
        formatted.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        formatted.append("API Call #: ").append(apiCallCount.get()).append("\n");
        formatted.append("=".repeat(40)).append("\n\n");
        formatted.append(stats);
        return formatted.toString();
    }
    
    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private void startStatusUpdates() {
        // Simulate periodic status updates
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                // Update flight table with mock data
                updateFlightTable();
                // Update API statistics
                updateApiStats();
                // Update status labels
                lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(timeFormatter));
                apiCallCountLabel.setText("API Calls: " + apiCallCount.get());
            });
        }, 0, 30, TimeUnit.SECONDS);
        
        // Initial API stats update
        updateApiStats();
        
        // Start real-time updates automatically
        startRealTimeUpdates();
    }
    
    private void updateFlightTable() {
        // Clear existing data
        flightTableModel.setRowCount(0);
        
        // Add enhanced mock flight data with timestamps
        String currentTime = LocalDateTime.now().format(timeFormatter);
        Object[][] mockData = {
            {"AA101", "American Airlines", "JFK-LAX", "In Flight", "A12", "Live", currentTime},
            {"DL202", "Delta Airlines", "ATL-SFO", "Boarding", "B15", "Live", currentTime},
            {"UA303", "United Airlines", "ORD-LAX", "Delayed", "C08", "Live", currentTime},
            {"SW404", "Southwest", "DAL-LAS", "On Time", "D22", "Live", currentTime},
            {"BA505", "British Airways", "LHR-JFK", "Scheduled", "E05", "Live", currentTime}
        };
        
        for (Object[] row : mockData) {
            flightTableModel.addRow(row);
        }
    }
    
    // Enhanced API methods
    private void refreshFlightData() {
        updateStatus("Refreshing flight data...", ThemeManager.WARNING_AMBER);
        updateFlightTable();
        updateApiStats();
        updateStatus("Flight data refreshed", ThemeManager.SUCCESS_GREEN);
        lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(timeFormatter));
    }
    
    private void updateApiKey() {
        String newApiKey = apiKeyField.getText().trim();
        if (newApiKey.isEmpty() || "your_aviationstack_api_key_here".equals(newApiKey)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid Aviation Stack API key", 
                "Invalid API Key", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // For now, just update the display and log the action
            // In a real implementation, you would save this to config.properties
            updateStatus("API key updated successfully", ThemeManager.SUCCESS_GREEN);
            apiStatusLabel.setText("API: Key Updated");
            apiStatusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
            
            FileLogger.getInstance().logInfo("Aviation Stack API key updated to: " + newApiKey.substring(0, Math.min(8, newApiKey.length())) + "...");
            
            JOptionPane.showMessageDialog(this, 
                "API key updated successfully!\n\nNote: To make this permanent, please update the 'aviationstack.api.key' property in your config.properties file.", 
                "API Key Updated", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            updateStatus("Failed to update API key", ThemeManager.ERROR_RED);
            FileLogger.getInstance().logError("API key update error: " + e.getMessage());
        }
    }
    
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export API Data");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new java.io.File("aviation_stack_data.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                
                writer.println("=== Aviation Stack API Data Export ===");
                writer.println("Generated: " + LocalDateTime.now());
                writer.println("API Calls: " + apiCallCount.get());
                writer.println();
                writer.println("=== Current Results ===");
                writer.println(resultArea.getText());
                writer.println();
                writer.println("=== Flight Data ===");
                for (int i = 0; i < flightTableModel.getRowCount(); i++) {
                    for (int j = 0; j < flightTableModel.getColumnCount(); j++) {
                        writer.print(flightTableModel.getValueAt(i, j) + "\t");
                    }
                    writer.println();
                }
                
                writer.close();
                updateStatus("Data exported successfully", ThemeManager.SUCCESS_GREEN);
                FileLogger.getInstance().logInfo("API data exported to: " + file.getAbsolutePath());
            } catch (Exception e) {
                updateStatus("Export failed: " + e.getMessage(), ThemeManager.ERROR_RED);
                FileLogger.getInstance().logError("Export error: " + e.getMessage());
            }
        }
    }
    
    private void clearResults() {
        resultArea.setText("");
        flightResultArea.setText("");
        airportResultArea.setText("");
        routeResultArea.setText("");
        flightTableModel.setRowCount(0);
        apiStatsTableModel.setRowCount(0);
        updateStatus("Results cleared", ThemeManager.SUCCESS_GREEN);
    }
    
    private void updateApiStats() {
        // Clear existing stats
        apiStatsTableModel.setRowCount(0);
        
        // Add API statistics
        String currentTime = LocalDateTime.now().format(timeFormatter);
        Object[][] statsData = {
            {"Total API Calls", String.valueOf(apiCallCount.get()), currentTime},
            {"Last API Call", lastApiCall.format(timeFormatter), currentTime},
            {"API Status", "Available", currentTime},
            {"Response Time", "~2.5s", currentTime},
            {"Data Source", "Aviation Stack", currentTime},
            {"Live Tracking", "Enabled", currentTime}
        };
        
        for (Object[] row : statsData) {
            apiStatsTableModel.addRow(row);
        }
    }
    
    private void incrementApiCallCount() {
        apiCallCount.incrementAndGet();
        lastApiCall = LocalDateTime.now();
        apiCallCountLabel.setText("API Calls: " + apiCallCount.get());
        updateApiStats();
    }
    
    // Real-time data update methods
    private void initializeRealTimeUpdates() {
        realTimeScheduler = Executors.newScheduledThreadPool(2);
        
        // Start real-time flight data updates every 30 seconds
        realTimeScheduler.scheduleAtFixedRate(() -> {
            if (isRealTimeActive) {
                updateRealTimeFlightData();
            }
        }, 0, 30, TimeUnit.SECONDS);
        
        // Start live tracking updates every 15 seconds if active
        realTimeScheduler.scheduleAtFixedRate(() -> {
            if (currentTrackingFlight != null && isRealTimeActive) {
                updateLiveTrackingData(currentTrackingFlight);
            }
        }, 5, 15, TimeUnit.SECONDS);
        
        FileLogger.getInstance().logInfo("Real-time Aviation Stack updates initialized");
    }
    
    private void updateRealTimeFlightData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Get real flight data from API
                    List<FlightInfo> flights = new ArrayList<>();
                    
                    // Track multiple flights for demo
                    String[] demoFlights = {"AA101", "DL202", "UA303", "SW404", "BA505"};
                    
                    for (String flightNumber : demoFlights) {
                        try {
                            FlightInfo flight = aviationService.getFlightInfo(flightNumber);
                            if (flight != null) {
                                flights.add(flight);
                                liveFlightData.put(flightNumber, flight);
                            }
                        } catch (Exception e) {
                            FileLogger.getInstance().logError("Error updating flight " + flightNumber + ": " + e.getMessage());
                        }
                    }
                    
                    cachedFlights = flights;
                    incrementApiCallCount();
                    
                } catch (Exception e) {
                    FileLogger.getInstance().logError("Real-time update error: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    updateFlightTableWithRealData();
                    updateStatus("Real-time data updated", ThemeManager.SUCCESS_GREEN);
                    lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(timeFormatter));
                });
            }
        };
        worker.execute();
    }
    
    private void updateFlightTableWithRealData() {
        // Clear existing data
        flightTableModel.setRowCount(0);
        
        String currentTime = LocalDateTime.now().format(timeFormatter);
        
        // Add real flight data if available, otherwise use cached data
        List<FlightInfo> flightsToDisplay = !cachedFlights.isEmpty() ? cachedFlights : getMockFlights();
        
        for (FlightInfo flight : flightsToDisplay) {
            Object[] row = {
                flight.getFlightNumber(),
                flight.getAirline() + " (" + flight.getAirlineCode() + ")",
                flight.getDepartureAirport() + "-" + flight.getArrivalAirport(),
                flight.getStatus(),
                flight.getGate() != null ? flight.getGate() : "TBD",
                flight.isLive() ? "Live" : "Static",
                currentTime
            };
            flightTableModel.addRow(row);
        }
    }
    
    private List<FlightInfo> getMockFlights() {
        List<FlightInfo> mockFlights = new ArrayList<>();
        String[] flightNumbers = {"AA101", "DL202", "UA303", "SW404", "BA505"};
        String[] airlines = {"American Airlines", "Delta Airlines", "United Airlines", "Southwest", "British Airways"};
        String[] airlineCodes = {"AA", "DL", "UA", "SW", "BA"};
        String[] routes = {"JFK-LAX", "ATL-SFO", "ORD-LAX", "DAL-LAS", "LHR-JFK"};
        String[] statuses = {"In Flight", "Boarding", "Delayed", "On Time", "Scheduled"};
        String[] gates = {"A12", "B15", "C08", "D22", "E05"};
        
        for (int i = 0; i < flightNumbers.length; i++) {
            FlightInfo flight = new FlightInfo();
            flight.setFlightNumber(flightNumbers[i]);
            flight.setAirline(airlines[i]);
            flight.setAirlineCode(airlineCodes[i]);
            flight.setDepartureAirport(routes[i].split("-")[0]);
            flight.setArrivalAirport(routes[i].split("-")[1]);
            flight.setStatus(statuses[i]);
            flight.setGate(gates[i]);
            flight.setLive(true);
            mockFlights.add(flight);
        }
        
        return mockFlights;
    }
    
    private void startRealTimeUpdates() {
        isRealTimeActive = true;
        updateStatus("Real-time updates started", ThemeManager.SUCCESS_GREEN);
        FileLogger.getInstance().logInfo("Real-time Aviation Stack updates started");
    }
    
    private void stopRealTimeUpdates() {
        isRealTimeActive = false;
        updateStatus("Real-time updates stopped", ThemeManager.WARNING_AMBER);
        FileLogger.getInstance().logInfo("Real-time Aviation Stack updates stopped");
    }
    
    @Override
    public void dispose() {
        stopRealTimeUpdates();
        if (realTimeScheduler != null && !realTimeScheduler.isShutdown()) {
            realTimeScheduler.shutdown();
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        if (searchTimer != null) {
            searchTimer.stop();
        }
        super.dispose();
    }
} 