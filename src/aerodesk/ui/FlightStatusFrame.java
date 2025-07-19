package aerodesk.ui;

import aerodesk.model.Flight;
import aerodesk.model.GateAssignment;
import aerodesk.dao.FlightDAO;
import aerodesk.dao.GateDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import aerodesk.util.ApiIntegrator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Flight Status Dashboard for AeroDesk Pro
 * Provides real-time flight tracking and weather information
 */
public class FlightStatusFrame extends JFrame {
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    private JTextArea weatherArea;
    private JTextArea systemStatusArea;
    private JButton refreshButton;
    private JButton startLiveUpdatesButton;
    private JButton stopLiveUpdatesButton;
    private JLabel lastUpdateLabel;
    private JLabel weatherLabel;
    
    private FlightDAO flightDAO;
    private GateDAO gateDAO;
    private ApiIntegrator apiIntegrator;
    private ScheduledExecutorService updateScheduler;
    private boolean liveUpdatesActive = false;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public FlightStatusFrame() {
        this.flightDAO = new FlightDAO();
        this.gateDAO = new GateDAO();
        this.apiIntegrator = new ApiIntegrator();
        this.updateScheduler = Executors.newScheduledThreadPool(1);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadFlightData();
        loadWeatherData();
        updateSystemStatus();
    }
    
    private void initializeComponents() {
        // Flights table
        String[] flightColumns = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Status", "Gate", "Delay"};
        flightsTableModel = new DefaultTableModel(flightColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Weather area
        weatherArea = new JTextArea();
        weatherArea.setEditable(false);
        weatherArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        weatherArea.setBackground(new Color(240, 248, 255));
        weatherArea.setBorder(BorderFactory.createTitledBorder("Weather Information"));
        
        // System status area
        systemStatusArea = new JTextArea();
        systemStatusArea.setEditable(false);
        systemStatusArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        systemStatusArea.setBackground(new Color(255, 250, 240));
        systemStatusArea.setBorder(BorderFactory.createTitledBorder("System Status"));
        
        // Buttons
        refreshButton = new JButton("Refresh Data");
        startLiveUpdatesButton = new JButton("Start Live Updates");
        stopLiveUpdatesButton = new JButton("Stop Live Updates");
        stopLiveUpdatesButton.setEnabled(false);
        
        // Labels
        lastUpdateLabel = new JLabel("Last Update: Never");
        lastUpdateLabel.setForeground(Color.GRAY);
        weatherLabel = new JLabel("Weather: Loading...");
        weatherLabel.setForeground(Color.BLUE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Flight Status Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Center panel - Flights table
        JPanel flightsPanel = new JPanel(new BorderLayout());
        flightsPanel.setBorder(BorderFactory.createTitledBorder("Flight Status"));
        JScrollPane flightsScrollPane = new JScrollPane(flightsTable);
        flightsPanel.add(flightsScrollPane, BorderLayout.CENTER);
        
        // Right panel - Weather and Status
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // Weather panel
        JPanel weatherPanel = new JPanel(new BorderLayout());
        JScrollPane weatherScrollPane = new JScrollPane(weatherArea);
        weatherScrollPane.setPreferredSize(new Dimension(300, 200));
        weatherPanel.add(weatherScrollPane, BorderLayout.CENTER);
        weatherPanel.add(weatherLabel, BorderLayout.SOUTH);
        
        // System status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        JScrollPane statusScrollPane = new JScrollPane(systemStatusArea);
        statusScrollPane.setPreferredSize(new Dimension(300, 150));
        statusPanel.add(statusScrollPane, BorderLayout.CENTER);
        
        rightPanel.add(weatherPanel);
        rightPanel.add(statusPanel);
        
        contentPanel.add(flightsPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(refreshButton);
        buttonPanel.add(startLiveUpdatesButton);
        buttonPanel.add(stopLiveUpdatesButton);
        buttonPanel.add(lastUpdateLabel);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        refreshButton.addActionListener(e -> handleRefresh());
        startLiveUpdatesButton.addActionListener(e -> handleStartLiveUpdates());
        stopLiveUpdatesButton.addActionListener(e -> handleStopLiveUpdates());
        
        // Table selection handler
        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleFlightSelection();
            }
        });
    }
    
    private void handleRefresh() {
        loadFlightData();
        loadWeatherData();
        updateSystemStatus();
        lastUpdateLabel.setText("Last Update: " + LocalDateTime.now().format(dateFormatter));
        FileLogger.getInstance().logInfo("Flight status data refreshed manually");
    }
    
    private void handleStartLiveUpdates() {
        liveUpdatesActive = true;
        startLiveUpdatesButton.setEnabled(false);
        stopLiveUpdatesButton.setEnabled(true);
        
        // Schedule periodic updates every 30 seconds
        updateScheduler.scheduleAtFixedRate(() -> {
            if (liveUpdatesActive) {
                SwingUtilities.invokeLater(() -> {
                    loadFlightData();
                    loadWeatherData();
                    updateSystemStatus();
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
        
        FileLogger.getInstance().logInfo("Live flight status updates stopped");
        JOptionPane.showMessageDialog(this, 
            "Live updates stopped.", 
            "Live Updates", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleFlightSelection() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String flightNo = flightsTableModel.getValueAt(selectedRow, 0).toString();
            showFlightDetails(flightNo);
        }
    }
    
    private void showFlightDetails(String flightNo) {
        try {
            Flight flight = flightDAO.getFlightByNumber(flightNo);
            if (flight != null) {
                StringBuilder details = new StringBuilder();
                details.append("Flight Details for ").append(flightNo).append("\n\n");
                details.append("Origin: ").append(flight.getOrigin()).append("\n");
                details.append("Destination: ").append(flight.getDestination()).append("\n");
                details.append("Aircraft: ").append(flight.getAircraftType()).append("\n");
                details.append("Status: ").append(flight.getStatus()).append("\n");
                
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
                
                JOptionPane.showMessageDialog(this, 
                    details.toString(), 
                    "Flight Details - " + flightNo, 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error getting flight details: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error retrieving flight details", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
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
                    
                    // Calculate delay (simplified)
                    String delayInfo = calculateDelay(flight);
                    
                    Object[] row = {
                        flight.getFlightNo(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartTime() != null ? flight.getDepartTime().format(dateFormatter) : "N/A",
                        flight.getArriveTime() != null ? flight.getArriveTime().format(dateFormatter) : "N/A",
                        flight.getStatus(),
                        gateInfo,
                        delayInfo
                    };
                    flightsTableModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading gate info for flight: " + ex.getMessage());
                }
            }
            
            FileLogger.getInstance().logInfo("Loaded " + flights.size() + " flights for status display");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flight data: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load flight data: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String calculateDelay(Flight flight) {
        if (flight.getDepartTime() == null) {
            return "N/A";
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledDeparture = flight.getDepartTime();
        
        if (now.isAfter(scheduledDeparture)) {
            long delayMinutes = java.time.Duration.between(scheduledDeparture, now).toMinutes();
            if (delayMinutes > 0) {
                return delayMinutes + " min late";
            }
        } else if (now.isBefore(scheduledDeparture)) {
            long earlyMinutes = java.time.Duration.between(now, scheduledDeparture).toMinutes();
            if (earlyMinutes < 15) {
                return "On time";
            }
        }
        
        return "On time";
    }
    
    private void loadWeatherData() {
        try {
            // Get weather data from API integrator
            String weatherData = apiIntegrator.getWeatherData();
            weatherArea.setText(weatherData);
            weatherLabel.setText("Weather: Updated at " + LocalDateTime.now().format(dateFormatter));
            
            FileLogger.getInstance().logInfo("Weather data updated");
            
        } catch (Exception ex) {
            weatherArea.setText("Weather data unavailable\n\nError: " + ex.getMessage());
            weatherLabel.setText("Weather: Error loading data");
            FileLogger.getInstance().logError("Failed to load weather data: " + ex.getMessage());
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
            
            status.append("Total Flights: ").append(flights.size()).append("\n");
            status.append("Active Flights: ").append(activeFlights).append("\n");
            
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            status.append("Gate Assignments: ").append(assignments.size()).append("\n");
            
        } catch (DatabaseException ex) {
            status.append("Database Status: Error - ").append(ex.getMessage()).append("\n");
        }
        
        status.append("\nAPI Status: ").append(apiIntegrator.isApiAvailable() ? "Available" : "Unavailable").append("\n");
        status.append("Last Refresh: ").append(now.format(dateFormatter)).append("\n");
        
        systemStatusArea.setText(status.toString());
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Flight Status Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
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
        super.dispose();
    }
} 