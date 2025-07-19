package aerodesk.ui;

import aerodesk.model.Baggage;
import aerodesk.model.Booking;
import aerodesk.model.Flight;
import aerodesk.dao.BaggageDAO;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import aerodesk.service.BaggageSimulator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Baggage Handling UI for AeroDesk Pro
 * Handles baggage tracking and management
 */
public class BaggageFrame extends JFrame {
    private JTable passengersTable;
    private DefaultTableModel passengersTableModel;
    private JTable baggageTable;
    private DefaultTableModel baggageTableModel;
    private JButton addBaggageButton;
    private JButton refreshButton;
    private JButton startSimulationButton;
    private JButton stopSimulationButton;
    private JLabel statusLabel;
    
    private BaggageDAO baggageDAO;
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private Booking selectedBooking;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public BaggageFrame() {
        this.baggageDAO = new BaggageDAO();
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadCheckedInPassengers();
        loadAllBaggage();
    }
    
    private void initializeComponents() {
        // Passengers table
        String[] passengerColumns = {"ID", "Passenger", "Flight No", "Seat", "Checked In", "Check-in Time"};
        passengersTableModel = new DefaultTableModel(passengerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengersTable = new JTable(passengersTableModel);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Baggage table
        String[] baggageColumns = {"ID", "Tag Number", "Passenger", "Weight (kg)", "Type", "Status", "Created"};
        baggageTableModel = new DefaultTableModel(baggageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baggageTable = new JTable(baggageTableModel);
        baggageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Buttons
        addBaggageButton = new JButton("Add Baggage");
        refreshButton = new JButton("Refresh");
        startSimulationButton = new JButton("Start Simulation");
        stopSimulationButton = new JButton("Stop Simulation");
        stopSimulationButton.setEnabled(false);
        
        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.GREEN);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Baggage Handling Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Left panel - Passengers
        JPanel passengersPanel = createPassengersPanel();
        
        // Right panel - Baggage
        JPanel baggagePanel = createBaggagePanel();
        
        contentPanel.add(passengersPanel);
        contentPanel.add(baggagePanel);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Status: "));
        statusPanel.add(statusLabel);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Checked-In Passengers"));
        
        JScrollPane scrollPane = new JScrollPane(passengersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBaggagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Baggage Tracking"));
        
        JScrollPane scrollPane = new JScrollPane(baggageTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.add(addBaggageButton);
        panel.add(refreshButton);
        panel.add(startSimulationButton);
        panel.add(stopSimulationButton);
        return panel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        addBaggageButton.addActionListener(e -> handleAddBaggage());
        refreshButton.addActionListener(e -> handleRefresh());
        startSimulationButton.addActionListener(e -> handleStartSimulation());
        stopSimulationButton.addActionListener(e -> handleStopSimulation());
        
        // Table selection handlers
        passengersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handlePassengerSelection();
            }
        });
        
        baggageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleBaggageSelection();
            }
        });
    }
    
    private void handleAddBaggage() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a passenger to add baggage for", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        showAddBaggageDialog();
    }
    
    private void showAddBaggageDialog() {
        JDialog dialog = new JDialog(this, "Add Baggage", true);
        dialog.setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Weight field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Weight (kg):"), gbc);
        JTextField weightField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(weightField, gbc);
        
        // Baggage type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        JComboBox<Baggage.BaggageType> typeComboBox = new JComboBox<>(Baggage.BaggageType.values());
        gbc.gridx = 1;
        formPanel.add(typeComboBox, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        addButton.addActionListener(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                if (weight <= 0 || weight > 100) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Weight must be between 0 and 100 kg", 
                        "Invalid Weight", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Baggage.BaggageType type = (Baggage.BaggageType) typeComboBox.getSelectedItem();
                String tagNumber = baggageDAO.generateTagNumber();
                
                Baggage baggage = new Baggage(selectedBooking.getBookingId(), weight, type, tagNumber);
                Baggage createdBaggage = baggageDAO.createBaggage(baggage);
                
                FileLogger.getInstance().logInfo("Added baggage: " + createdBaggage.getTagNumber());
                JOptionPane.showMessageDialog(dialog, 
                    "Baggage added successfully! Tag: " + createdBaggage.getTagNumber(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                loadAllBaggage();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid weight", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to add baggage: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, 
                    "Failed to add baggage: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private void handleRefresh() {
        loadCheckedInPassengers();
        loadAllBaggage();
        statusLabel.setText("Data refreshed at " + LocalDateTime.now().format(dateFormatter));
    }
    
    private void handleStartSimulation() {
        startSimulationButton.setEnabled(false);
        stopSimulationButton.setEnabled(true);
        statusLabel.setText("Simulation running...");
        statusLabel.setForeground(Color.ORANGE);
        
        // Start baggage simulation thread
        BaggageSimulator.getInstance().startSimulation();
        
        FileLogger.getInstance().logInfo("Baggage simulation started");
    }
    
    private void handleStopSimulation() {
        startSimulationButton.setEnabled(true);
        stopSimulationButton.setEnabled(false);
        statusLabel.setText("Simulation stopped");
        statusLabel.setForeground(Color.GREEN);
        
        // Stop baggage simulation thread
        BaggageSimulator.getInstance().stopSimulation();
        
        FileLogger.getInstance().logInfo("Baggage simulation stopped");
    }
    
    private void handlePassengerSelection() {
        int selectedRow = passengersTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedBooking = getBookingFromTableRow(selectedRow);
            addBaggageButton.setEnabled(true);
        } else {
            selectedBooking = null;
            addBaggageButton.setEnabled(false);
        }
    }
    
    private void handleBaggageSelection() {
        int selectedRow = baggageTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Handle baggage selection if needed
        }
    }
    
    private Booking getBookingFromTableRow(int row) {
        Booking booking = new Booking();
        booking.setBookingId(Integer.parseInt(passengersTableModel.getValueAt(row, 0).toString()));
        booking.setPassengerName(passengersTableModel.getValueAt(row, 1).toString());
        return booking;
    }
    
    private void loadCheckedInPassengers() {
        try {
            List<Booking> allBookings = bookingDAO.getAllBookings();
            passengersTableModel.setRowCount(0);
            
            for (Booking booking : allBookings) {
                if (booking.isCheckedIn()) {
                    try {
                        Flight flight = flightDAO.getFlightById(booking.getFlightId());
                        String flightNo = flight != null ? flight.getFlightNo() : "N/A";
                        
                        Object[] row = {
                            booking.getBookingId(),
                            booking.getPassengerName(),
                            flightNo,
                            booking.getSeatNo() != null ? booking.getSeatNo() : "Not Assigned",
                            "Yes",
                            booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "N/A"
                        };
                        passengersTableModel.addRow(row);
                    } catch (DatabaseException ex) {
                        FileLogger.getInstance().logError("Error loading flight for booking: " + ex.getMessage());
                    }
                }
            }
            
            FileLogger.getInstance().logInfo("Loaded " + passengersTableModel.getRowCount() + " checked-in passengers");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load passengers: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load passengers: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllBaggage() {
        try {
            List<Baggage> allBaggage = baggageDAO.getAllBaggage();
            baggageTableModel.setRowCount(0);
            
            for (Baggage baggage : allBaggage) {
                try {
                    Booking booking = bookingDAO.getBookingById(baggage.getBookingId());
                    String passengerName = booking != null ? booking.getPassengerName() : "Unknown";
                    
                    Object[] row = {
                        baggage.getBaggageId(),
                        baggage.getTagNumber(),
                        passengerName,
                        String.format("%.1f", baggage.getWeightKg()),
                        baggage.getBaggageType(),
                        baggage.getStatus(),
                        baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A"
                    };
                    baggageTableModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading booking for baggage: " + ex.getMessage());
                }
            }
            
            FileLogger.getInstance().logInfo("Loaded " + baggageTableModel.getRowCount() + " baggage items");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load baggage: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load baggage: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Baggage Handling");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set initial button states
        addBaggageButton.setEnabled(false);
    }
} 