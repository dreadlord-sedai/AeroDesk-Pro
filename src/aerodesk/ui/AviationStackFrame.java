package aerodesk.ui;

import aerodesk.service.AviationStackService;
import aerodesk.service.AviationStackService.FlightInfo;
import aerodesk.service.AviationStackService.AirportInfo;
import aerodesk.util.FileLogger;
import aerodesk.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Aviation Stack API Integration Frame
 * Demonstrates real-time flight tracking and airport information
 */
public class AviationStackFrame extends JFrame {
    
    private AviationStackService aviationService;
    private JTextField flightNumberField;
    private JTextField airportCodeField;
    private JTextArea resultArea;
    private JTable flightTable;
    private DefaultTableModel flightTableModel;
    private JLabel statusLabel;
    private JButton trackFlightButton;
    private JButton getAirportInfoButton;
    private JButton searchRouteButton;
    private JButton liveTrackingButton;
    private JButton airlineInfoButton;
    private JButton airportStatsButton;
    private ScheduledExecutorService scheduler;
    
    public AviationStackFrame() {
        aviationService = new AviationStackService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        startStatusUpdates();
        
        setTitle("Aviation Stack API Integration - AeroDesk Pro");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ThemeManager.styleFrame(this);
    }
    
    private JScrollPane resultScrollPane;
    private JScrollPane tableScrollPane;
    
    private void initializeComponents() {
        // Input fields
        flightNumberField = new JTextField(15);
        flightNumberField.setText("AA101");
        ThemeManager.styleTextField(flightNumberField);
        
        airportCodeField = new JTextField(10);
        airportCodeField.setText("JFK");
        ThemeManager.styleTextField(airportCodeField);
        
        // Buttons
        trackFlightButton = new JButton("✈️ Track Flight");
        getAirportInfoButton = new JButton("🏢 Get Airport Info");
        searchRouteButton = new JButton("🛣️ Search Route");
        liveTrackingButton = new JButton("📍 Live Tracking");
        airlineInfoButton = new JButton("✈️ Airline Info");
        airportStatsButton = new JButton("📊 Airport Stats");
        
        // Apply modern styling to buttons
        ThemeManager.styleButton(trackFlightButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(getAirportInfoButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(searchRouteButton, ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        ThemeManager.styleButton(liveTrackingButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(airlineInfoButton, ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        ThemeManager.styleButton(airportStatsButton, ThemeManager.DARK_GRAY, ThemeManager.WHITE);
        
        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        ThemeManager.styleTextArea(resultArea);
        resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Flight table
        String[] columnNames = {"Flight", "Airline", "Route", "Status", "Gate", "Live"};
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
        
        // Status label
        statusLabel = ThemeManager.createStatusLabel("Ready", ThemeManager.SUCCESS_GREEN);
        
        // Scheduler for background updates
        scheduler = Executors.newScheduledThreadPool(2);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("🌐 Aviation Stack API Integration");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = ThemeManager.createBodyLabel("Real-time Flight Tracking & Airport Information");
        subtitleLabel.setForeground(ThemeManager.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Top panel for inputs
        JPanel inputPanel = ThemeManager.createCardPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        inputPanel.add(ThemeManager.createBodyLabel("✈️ Flight Number:"));
        inputPanel.add(flightNumberField);
        inputPanel.add(trackFlightButton);
        inputPanel.add(liveTrackingButton);
        
        inputPanel.add(ThemeManager.createBodyLabel("🏢 Airport Code:"));
        inputPanel.add(airportCodeField);
        inputPanel.add(getAirportInfoButton);
        inputPanel.add(airportStatsButton);
        inputPanel.add(airlineInfoButton);
        inputPanel.add(searchRouteButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        
        // Center panel for results
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Results area with title
        JPanel resultsPanel = ThemeManager.createCardPanel();
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel resultsTitle = ThemeManager.createSubheaderLabel("API Results");
        resultsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        // Flight table with title
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = ThemeManager.createSubheaderLabel("Live Flight Data");
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Combine results and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsPanel, tablePanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);
        
        centerPanel.add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel for status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statusPanel.setBackground(ThemeManager.WHITE);
        statusPanel.add(ThemeManager.createBodyLabel("Status:"));
        statusPanel.add(statusLabel);
        
        add(centerPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        trackFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackFlight();
            }
        });
        
        getAirportInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAirportInfo();
            }
        });
        
        searchRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRoute();
            }
        });
        
        liveTrackingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startLiveTracking();
            }
        });
        
        airlineInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAirlineInfo();
            }
        });
        
        airportStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAirportStats();
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
            @Override
            protected String doInBackground() throws Exception {
                return aviationService.getFlightStatusSummary(flightNumber);
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Flight tracking completed", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Flight tracked: " + flightNumber);
                } catch (Exception ex) {
                    resultArea.setText("Error tracking flight: " + ex.getMessage());
                    updateStatus("Flight tracking failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Flight tracking error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void getAirportInfo() {
        String airportCode = airportCodeField.getText().trim();
        if (airportCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an airport code", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        updateStatus("Getting airport info for " + airportCode + "...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                AviationStackService.AirportInfo airport = aviationService.getAirportInfo(airportCode);
                return "=== Airport Information ===\n" +
                       "Name: " + airport.getName() + "\n" +
                       "IATA Code: " + airport.getIataCode() + "\n" +
                       "ICAO Code: " + airport.getIcaoCode() + "\n" +
                       "Country: " + airport.getCountry() + "\n" +
                       "City: " + airport.getCity() + "\n" +
                       "Timezone: " + airport.getTimezone() + "\n" +
                       "Coordinates: " + airport.getLatitude() + ", " + airport.getLongitude() + "\n" +
                       "Website: " + (airport.getWebsite() != null ? airport.getWebsite() : "N/A") + "\n";
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Airport info retrieved", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Airport info retrieved: " + airportCode);
                } catch (Exception ex) {
                    resultArea.setText("Error getting airport info: " + ex.getMessage());
                    updateStatus("Airport info failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airport info error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void searchRoute() {
        updateStatus("Searching routes...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                List<AviationStackService.FlightInfo> flights = aviationService.searchFlightsByRoute("JFK", "LAX", null);
                StringBuilder result = new StringBuilder("=== Flight Search Results ===\n");
                result.append("Route: JFK → LAX\n");
                result.append("Found ").append(flights.size()).append(" flights:\n\n");
                
                for (AviationStackService.FlightInfo flight : flights) {
                    result.append("Flight: ").append(flight.getFlightNumber()).append("\n");
                    result.append("Airline: ").append(flight.getAirline()).append("\n");
                    result.append("Status: ").append(flight.getStatus()).append("\n");
                    result.append("Gate: ").append(flight.getGate()).append("\n");
                    result.append("---\n");
                }
                return result.toString();
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Route search completed", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Route search completed");
                } catch (Exception ex) {
                    resultArea.setText("Error searching routes: " + ex.getMessage());
                    updateStatus("Route search failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Route search error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
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
                AviationStackService.FlightInfo flight = aviationService.getLiveFlightTracking(flightNumber);
                StringBuilder result = new StringBuilder("=== Live Flight Tracking ===\n");
                result.append("Flight: ").append(flight.getFlightNumber()).append("\n");
                result.append("Airline: ").append(flight.getAirline()).append("\n");
                result.append("Status: ").append(flight.getStatus()).append("\n");
                result.append("Live Tracking: ").append(flight.isLive() ? "Available" : "Not Available").append("\n");
                
                if (flight.isLive()) {
                    result.append("Current Location: ").append(flight.getLatitude()).append(", ").append(flight.getLongitude()).append("\n");
                    result.append("Altitude: ").append(flight.getAltitude()).append(" ft\n");
                    result.append("Speed: ").append(flight.getSpeed()).append(" km/h\n");
                }
                return result.toString();
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Live tracking active", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Live tracking started: " + flightNumber);
                } catch (Exception ex) {
                    resultArea.setText("Error starting live tracking: " + ex.getMessage());
                    updateStatus("Live tracking failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Live tracking error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void getAirlineInfo() {
        updateStatus("Getting airline information...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                AviationStackService.AirlineInfo airline = aviationService.getAirlineInfo("AA");
                return "=== Airline Information ===\n" +
                       "Name: " + airline.getName() + "\n" +
                       "IATA Code: " + airline.getIataCode() + "\n" +
                       "ICAO Code: " + airline.getIcaoCode() + "\n" +
                       "Country: " + airline.getCountry() + "\n" +
                       "Website: " + (airline.getWebsite() != null ? airline.getWebsite() : "N/A") + "\n" +
                       "Phone: " + (airline.getPhone() != null ? airline.getPhone() : "N/A") + "\n" +
                       "Fleet Size: " + (airline.getFleetSize() != null ? airline.getFleetSize() : "N/A") + "\n" +
                       "Founded: " + (airline.getFounded() != null ? airline.getFounded() : "N/A") + "\n";
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Airline info retrieved", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Airline info retrieved");
                } catch (Exception ex) {
                    resultArea.setText("Error getting airline info: " + ex.getMessage());
                    updateStatus("Airline info failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airline info error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void getAirportStats() {
        updateStatus("Getting airport statistics...", ThemeManager.WARNING_AMBER);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return aviationService.getAirportStatistics("JFK");
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    updateStatus("Airport stats retrieved", ThemeManager.SUCCESS_GREEN);
                    FileLogger.getInstance().logInfo("Airport stats retrieved");
                } catch (Exception ex) {
                    resultArea.setText("Error getting airport stats: " + ex.getMessage());
                    updateStatus("Airport stats failed", ThemeManager.ERROR_RED);
                    FileLogger.getInstance().logError("Airport stats error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
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
            });
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    private void updateFlightTable() {
        // Clear existing data
        flightTableModel.setRowCount(0);
        
        // Add mock flight data
        Object[][] mockData = {
            {"AA101", "American Airlines", "JFK-LAX", "In Flight", "A12", "📍 Live"},
            {"DL202", "Delta Airlines", "ATL-SFO", "Boarding", "B15", "📍 Live"},
            {"UA303", "United Airlines", "ORD-LAX", "Delayed", "C08", "📍 Live"},
            {"SW404", "Southwest", "DAL-LAS", "On Time", "D22", "📍 Live"}
        };
        
        for (Object[] row : mockData) {
            flightTableModel.addRow(row);
        }
    }
    
    @Override
    public void dispose() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        super.dispose();
    }
} 