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
import aerodesk.util.ThemeManager;
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
        String[] passengerColumns = {"Booking ID", "Passenger", "Flight", "Seat", "Checked In"};
        passengersTableModel = new DefaultTableModel(passengerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengersTable = new JTable(passengersTableModel);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(passengersTable);
        
        // Baggage table
        String[] baggageColumns = {"Tag Number", "Passenger", "Weight", "Type", "Status", "Created"};
        baggageTableModel = new DefaultTableModel(baggageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baggageTable = new JTable(baggageTableModel);
        baggageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(baggageTable);
        
        // Buttons
        addBaggageButton = new JButton("👜 Add Baggage");
        refreshButton = new JButton("🔄 Refresh");
        startSimulationButton = new JButton("▶️ Start Simulation");
        stopSimulationButton = new JButton("⏹️ Stop Simulation");
        
        // Apply modern styling
        ThemeManager.styleButton(addBaggageButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(startSimulationButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(stopSimulationButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        // Status label
        statusLabel = ThemeManager.createStatusLabel("Ready", ThemeManager.SUCCESS_GREEN);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("👜 Baggage Handling Management");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(ThemeManager.WHITE);
        
        // Left panel - Passengers
        JPanel passengersPanel = createPassengersPanel();
        
        // Right panel - Baggage
        JPanel baggagePanel = createBaggagePanel();
        
        contentPanel.add(passengersPanel);
        contentPanel.add(baggagePanel);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statusPanel.setBackground(ThemeManager.WHITE);
        statusPanel.add(ThemeManager.createBodyLabel("Status:"));
        statusPanel.add(statusLabel);
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPassengersPanel() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel panelTitle = ThemeManager.createSubheaderLabel("Checked-In Passengers");
        panelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(panelTitle, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(passengersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBaggagePanel() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel panelTitle = ThemeManager.createSubheaderLabel("Baggage Tracking");
        panelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(panelTitle, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(baggageTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(addBaggageButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(startSimulationButton);
        buttonPanel.add(stopSimulationButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        addBaggageButton.addActionListener(e -> addBaggage());
        refreshButton.addActionListener(e -> refreshData());
        startSimulationButton.addActionListener(e -> startSimulation());
        stopSimulationButton.addActionListener(e -> stopSimulation());
        
        // Table selection listeners
        passengersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = passengersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedBooking = getBookingFromTableRow(selectedRow);
                    addBaggageButton.setEnabled(true);
                } else {
                    selectedBooking = null;
                    addBaggageButton.setEnabled(false);
                }
            }
        });
    }
    
    private void addBaggage() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to add baggage", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show baggage input dialog
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBackground(ThemeManager.WHITE);
        
        JTextField weightField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Checked", "Carry-on", "Special"});
        
        ThemeManager.styleTextField(weightField);
        ThemeManager.styleComboBox(typeComboBox);
        
        inputPanel.add(ThemeManager.createBodyLabel("Weight (kg):"));
        inputPanel.add(weightField);
        inputPanel.add(ThemeManager.createBodyLabel("Type:"));
        inputPanel.add(typeComboBox);
        
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Baggage", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                String type = (String) typeComboBox.getSelectedItem();
                
                Baggage baggage = new Baggage();
                baggage.setBookingId(selectedBooking.getBookingId());
                baggage.setWeightKg(weight);
                baggage.setBaggageType(Baggage.BaggageType.CHECKED);
                baggage.setStatus(Baggage.BaggageStatus.LOADED);
                
                baggageDAO.createBaggage(baggage);
                
                FileLogger.getInstance().logInfo("Added baggage for passenger: " + selectedBooking.getPassengerName());
                JOptionPane.showMessageDialog(this, "Baggage added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadAllBaggage();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid weight", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Error adding baggage: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error adding baggage: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshData() {
        loadCheckedInPassengers();
        loadAllBaggage();
        statusLabel.setText("Data refreshed");
        statusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
    }
    
    private void startSimulation() {
        BaggageSimulator.getInstance().startSimulation();
        startSimulationButton.setEnabled(false);
        stopSimulationButton.setEnabled(true);
        statusLabel.setText("Simulation running");
        statusLabel.setForeground(ThemeManager.WARNING_AMBER);
        FileLogger.getInstance().logInfo("Baggage simulation started");
    }
    
    private void stopSimulation() {
        BaggageSimulator.getInstance().stopSimulation();
        startSimulationButton.setEnabled(true);
        stopSimulationButton.setEnabled(false);
        statusLabel.setText("Simulation stopped");
        statusLabel.setForeground(ThemeManager.SUCCESS_GREEN);
        FileLogger.getInstance().logInfo("Baggage simulation stopped");
    }
    
    private void loadCheckedInPassengers() {
        try {
            List<Booking> checkedInBookings = bookingDAO.getAllBookings().stream()
                .filter(Booking::isCheckedIn)
                .toList();
            
            passengersTableModel.setRowCount(0);
            for (Booking booking : checkedInBookings) {
                Object[] row = {
                    booking.getBookingId(),
                    booking.getPassengerName(),
                    "Flight " + booking.getFlightId(),
                    booking.getSeatNo(),
                    "Yes"
                };
                passengersTableModel.addRow(row);
            }
            
            FileLogger.getInstance().logInfo("Loaded " + checkedInBookings.size() + " checked-in passengers");
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error loading checked-in passengers: " + ex.getMessage());
        }
    }
    
    private void loadAllBaggage() {
        try {
            List<Baggage> allBaggage = baggageDAO.getAllBaggage();
            
            baggageTableModel.setRowCount(0);
            for (Baggage baggage : allBaggage) {
                Object[] row = {
                    baggage.getTagNumber(),
                    "Passenger " + baggage.getBookingId(), // Would need to get actual passenger name
                    baggage.getWeightKg() + " kg",
                    baggage.getBaggageType().toString(),
                    baggage.getStatus().toString(),
                    baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A"
                };
                baggageTableModel.addRow(row);
            }
            
            FileLogger.getInstance().logInfo("Loaded " + allBaggage.size() + " baggage items");
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error loading baggage: " + ex.getMessage());
        }
    }
    
    private Booking getBookingFromTableRow(int row) {
        int bookingId = (Integer) passengersTableModel.getValueAt(row, 0);
        try {
            return bookingDAO.getBookingById(bookingId);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error getting booking details: " + ex.getMessage());
            return null;
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Baggage Handling");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
} 