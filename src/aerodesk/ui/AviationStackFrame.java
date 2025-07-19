package aerodesk.ui;

import aerodesk.service.AviationStackService;
import aerodesk.service.AviationStackService.FlightInfo;
import aerodesk.service.AviationStackService.AirportInfo;
import aerodesk.util.FileLogger;

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
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private JScrollPane resultScrollPane;
    private JScrollPane tableScrollPane;
    
    private void initializeComponents() {
        // Input fields
        flightNumberField = new JTextField(15);
        flightNumberField.setText("AA101");
        
        airportCodeField = new JTextField(10);
        airportCodeField.setText("JFK");
        
        // Buttons
        trackFlightButton = new JButton("Track Flight");
        getAirportInfoButton = new JButton("Get Airport Info");
        searchRouteButton = new JButton("Search Route");
        liveTrackingButton = new JButton("Live Tracking");
        airlineInfoButton = new JButton("Airline Info");
        airportStatsButton = new JButton("Airport Stats");
        
        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
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
        tableScrollPane = new JScrollPane(flightTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        
        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.BLUE);
        
        // Scheduler for background updates
        scheduler = Executors.newScheduledThreadPool(2);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel for inputs
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Flight Information"));
        
        inputPanel.add(new JLabel("Flight Number:"));
        inputPanel.add(flightNumberField);
        inputPanel.add(trackFlightButton);
        inputPanel.add(liveTrackingButton);
        
        inputPanel.add(new JLabel("Airport Code:"));
        inputPanel.add(airportCodeField);
        inputPanel.add(getAirportInfoButton);
        inputPanel.add(airportStatsButton);
        inputPanel.add(airlineInfoButton);
        inputPanel.add(searchRouteButton);
        
        add(inputPanel, BorderLayout.NORTH);
        
        // Center panel for results
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Results area
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        // Flight table
        centerPanel.add(tableScrollPane, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel for status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
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
                getAirportStatistics();
            }
        });
        
        // Enter key handlers
        flightNumberField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackFlight();
            }
        });
        
        airportCodeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAirportInfo();
            }
        });
    }
    
    private void trackFlight() {
        String flightNumber = flightNumberField.getText().trim();
        if (flightNumber.isEmpty()) {
            showMessage("Please enter a flight number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setStatus("Tracking flight " + flightNumber + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    FlightInfo flight = aviationService.getFlightInfo(flightNumber);
                    String summary = aviationService.getFlightStatusSummary(flightNumber);
                    
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("=== Flight Information ===\n" + summary);
                        updateFlightTable(flight);
                        setStatus("Flight " + flightNumber + " tracked successfully");
                        FileLogger.getInstance().logInfo("Tracked flight: " + flightNumber);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error tracking flight: " + ex.getMessage());
                        setStatus("Error tracking flight");
                        FileLogger.getInstance().logError("Error tracking flight " + flightNumber + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void getAirportInfo() {
        String airportCode = airportCodeField.getText().trim().toUpperCase();
        if (airportCode.isEmpty()) {
            showMessage("Please enter an airport code", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setStatus("Getting airport information for " + airportCode + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    AirportInfo airport = aviationService.getAirportInfo(airportCode);
                    
                    SwingUtilities.invokeLater(() -> {
                        StringBuilder info = new StringBuilder();
                        info.append("=== Airport Information ===\n");
                        info.append("Name: ").append(airport.getName()).append("\n");
                        info.append("IATA Code: ").append(airport.getIataCode()).append("\n");
                        info.append("ICAO Code: ").append(airport.getIcaoCode()).append("\n");
                        info.append("Country: ").append(airport.getCountry()).append("\n");
                        info.append("Timezone: ").append(airport.getTimezone()).append("\n");
                        info.append("Coordinates: ").append(airport.getLatitude()).append(", ").append(airport.getLongitude()).append("\n");
                        if (airport.getWebsite() != null) {
                            info.append("Website: ").append(airport.getWebsite()).append("\n");
                        }
                        
                        resultArea.setText(info.toString());
                        setStatus("Airport information retrieved for " + airportCode);
                        FileLogger.getInstance().logInfo("Retrieved airport info: " + airportCode);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error getting airport information: " + ex.getMessage());
                        setStatus("Error getting airport information");
                        FileLogger.getInstance().logError("Error getting airport info for " + airportCode + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void searchRoute() {
        String departure = JOptionPane.showInputDialog(this, "Enter departure airport code:", "JFK");
        if (departure == null || departure.trim().isEmpty()) return;
        
        String arrival = JOptionPane.showInputDialog(this, "Enter arrival airport code:", "LAX");
        if (arrival == null || arrival.trim().isEmpty()) return;
        
        setStatus("Searching flights from " + departure + " to " + arrival + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<FlightInfo> flights = aviationService.searchFlightsByRoute(departure, arrival, null);
                    
                    SwingUtilities.invokeLater(() -> {
                        StringBuilder info = new StringBuilder();
                        info.append("=== Flight Search Results ===\n");
                        info.append("Route: ").append(departure).append(" → ").append(arrival).append("\n");
                        info.append("Found ").append(flights.size()).append(" flights:\n\n");
                        
                        for (FlightInfo flight : flights) {
                            info.append("Flight: ").append(flight.getFlightNumber()).append("\n");
                            info.append("Airline: ").append(flight.getAirline()).append("\n");
                            info.append("Status: ").append(flight.getStatus()).append("\n");
                            info.append("Gate: ").append(flight.getGate()).append("\n");
                            info.append("---\n");
                        }
                        
                        resultArea.setText(info.toString());
                        updateFlightTable(flights);
                        setStatus("Found " + flights.size() + " flights for route " + departure + " → " + arrival);
                        FileLogger.getInstance().logInfo("Searched route: " + departure + " → " + arrival + " (" + flights.size() + " flights)");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error searching flights: " + ex.getMessage());
                        setStatus("Error searching flights");
                        FileLogger.getInstance().logError("Error searching route " + departure + " → " + arrival + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void startLiveTracking() {
        String flightNumber = flightNumberField.getText().trim();
        if (flightNumber.isEmpty()) {
            showMessage("Please enter a flight number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setStatus("Starting live tracking for " + flightNumber + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    FlightInfo flight = aviationService.getLiveFlightTracking(flightNumber);
                    
                    SwingUtilities.invokeLater(() -> {
                        StringBuilder info = new StringBuilder();
                        info.append("=== Live Flight Tracking ===\n");
                        info.append("Flight: ").append(flight.getFlightNumber()).append("\n");
                        info.append("Airline: ").append(flight.getAirline()).append("\n");
                        info.append("Status: ").append(flight.getStatus()).append("\n");
                        info.append("Live Tracking: ").append(flight.isLive() ? "Available" : "Not Available").append("\n");
                        
                        if (flight.isLive()) {
                            info.append("Current Location: ").append(flight.getLatitude()).append(", ").append(flight.getLongitude()).append("\n");
                            info.append("Altitude: ").append(flight.getAltitude()).append(" ft\n");
                            info.append("Speed: ").append(flight.getSpeed()).append(" km/h\n");
                        }
                        
                        resultArea.setText(info.toString());
                        updateFlightTable(flight);
                        setStatus("Live tracking active for " + flightNumber);
                        FileLogger.getInstance().logInfo("Started live tracking: " + flightNumber);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error starting live tracking: " + ex.getMessage());
                        setStatus("Error starting live tracking");
                        FileLogger.getInstance().logError("Error starting live tracking for " + flightNumber + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void getAirlineInfo() {
        String airlineCode = JOptionPane.showInputDialog(this, "Enter airline IATA code:", "AA");
        if (airlineCode == null || airlineCode.trim().isEmpty()) return;
        
        setStatus("Getting airline information for " + airlineCode + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    AviationStackService.AirlineInfo airline = aviationService.getAirlineInfo(airlineCode);
                    
                    SwingUtilities.invokeLater(() -> {
                        StringBuilder info = new StringBuilder();
                        info.append("=== Airline Information ===\n");
                        info.append("Name: ").append(airline.getName()).append("\n");
                        info.append("IATA Code: ").append(airline.getIataCode()).append("\n");
                        info.append("ICAO Code: ").append(airline.getIcaoCode()).append("\n");
                        info.append("Country: ").append(airline.getCountry()).append("\n");
                        if (airline.getWebsite() != null) {
                            info.append("Website: ").append(airline.getWebsite()).append("\n");
                        }
                        if (airline.getPhone() != null) {
                            info.append("Phone: ").append(airline.getPhone()).append("\n");
                        }
                        if (airline.getFleetSize() != null) {
                            info.append("Fleet Size: ").append(airline.getFleetSize()).append("\n");
                        }
                        if (airline.getFounded() != null) {
                            info.append("Founded: ").append(airline.getFounded()).append("\n");
                        }
                        
                        resultArea.setText(info.toString());
                        setStatus("Airline information retrieved for " + airlineCode);
                        FileLogger.getInstance().logInfo("Retrieved airline info: " + airlineCode);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error getting airline information: " + ex.getMessage());
                        setStatus("Error getting airline information");
                        FileLogger.getInstance().logError("Error getting airline info for " + airlineCode + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void getAirportStatistics() {
        String airportCode = airportCodeField.getText().trim().toUpperCase();
        if (airportCode.isEmpty()) {
            showMessage("Please enter an airport code", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setStatus("Getting airport statistics for " + airportCode + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String stats = aviationService.getAirportStatistics(airportCode);
                    
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText(stats);
                        setStatus("Airport statistics retrieved for " + airportCode);
                        FileLogger.getInstance().logInfo("Retrieved airport statistics: " + airportCode);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Error getting airport statistics: " + ex.getMessage());
                        setStatus("Error getting airport statistics");
                        FileLogger.getInstance().logError("Error getting airport statistics for " + airportCode + ": " + ex.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void updateFlightTable(FlightInfo flight) {
        flightTableModel.setRowCount(0);
        if (flight != null) {
            flightTableModel.addRow(new Object[]{
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getDepartureAirport() + " → " + flight.getArrivalAirport(),
                flight.getStatus(),
                flight.getGate(),
                flight.isLive() ? "Yes" : "No"
            });
        }
    }
    
    private void updateFlightTable(List<FlightInfo> flights) {
        flightTableModel.setRowCount(0);
        for (FlightInfo flight : flights) {
            flightTableModel.addRow(new Object[]{
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getDepartureAirport() + " → " + flight.getArrivalAirport(),
                flight.getStatus(),
                flight.getGate(),
                flight.isLive() ? "Yes" : "No"
            });
        }
    }
    
    private void startStatusUpdates() {
        // Update API availability status every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                boolean apiAvailable = aviationService.isApiAvailable();
                if (apiAvailable) {
                    statusLabel.setText("Aviation Stack API: Available");
                    statusLabel.setForeground(Color.GREEN);
                } else {
                    statusLabel.setText("Aviation Stack API: Using Mock Data");
                    statusLabel.setForeground(Color.ORANGE);
                }
            });
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    private void setStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.BLUE);
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    @Override
    public void dispose() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        super.dispose();
    }
} 