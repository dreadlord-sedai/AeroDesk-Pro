package aerodesk.ui;

import aerodesk.model.Flight;
import aerodesk.dao.FlightDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Flight Scheduling UI for AeroDesk Pro
 * Handles flight creation, editing, deletion, and viewing
 */
public class FlightSchedulingFrame extends JFrame {
    private JTextField flightNoField;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField departTimeField;
    private JTextField arriveTimeField;
    private JTextField aircraftTypeField;
    private JComboBox<Flight.FlightStatus> statusComboBox;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    
    private FlightDAO flightDAO;
    private Flight selectedFlight;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public FlightSchedulingFrame() {
        this.flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadFlights();
    }
    
    private void initializeComponents() {
        // Form fields
        flightNoField = new JTextField(15);
        originField = new JTextField(15);
        destinationField = new JTextField(15);
        departTimeField = new JTextField(15);
        arriveTimeField = new JTextField(15);
        aircraftTypeField = new JTextField(15);
        statusComboBox = new JComboBox<>(Flight.FlightStatus.values());
        
        // Buttons
        createButton = new JButton("Create Flight");
        updateButton = new JButton("Update Flight");
        deleteButton = new JButton("Delete Flight");
        clearButton = new JButton("Clear Form");
        refreshButton = new JButton("Refresh");
        
        // Table
        String[] columnNames = {"ID", "Flight No", "Origin", "Destination", "Depart", "Arrive", "Aircraft", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        flightsTable = new JTable(tableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set preferred sizes
        Dimension fieldSize = new Dimension(200, 25);
        flightNoField.setPreferredSize(fieldSize);
        originField.setPreferredSize(fieldSize);
        destinationField.setPreferredSize(fieldSize);
        departTimeField.setPreferredSize(fieldSize);
        arriveTimeField.setPreferredSize(fieldSize);
        aircraftTypeField.setPreferredSize(fieldSize);
        statusComboBox.setPreferredSize(fieldSize);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Flight Scheduling Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Flight Details"));
        formPanel.setPreferredSize(new Dimension(300, 400));
        
        // Add form fields
        formPanel.add(createFormField("Flight No:", flightNoField));
        formPanel.add(createFormField("Origin:", originField));
        formPanel.add(createFormField("Destination:", destinationField));
        formPanel.add(createFormField("Depart Time (yyyy-MM-dd HH:mm):", departTimeField));
        formPanel.add(createFormField("Arrive Time (yyyy-MM-dd HH:mm):", arriveTimeField));
        formPanel.add(createFormField("Aircraft Type:", aircraftTypeField));
        formPanel.add(createFormField("Status:", statusComboBox));
        
        return formPanel;
    }
    
    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(180, 25));
        panel.add(jLabel);
        panel.add(field);
        panel.setMaximumSize(new Dimension(300, 35));
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Flight List"));
        
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        createButton.addActionListener(e -> handleCreateFlight());
        updateButton.addActionListener(e -> handleUpdateFlight());
        deleteButton.addActionListener(e -> handleDeleteFlight());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> loadFlights());
        
        // Table selection handler
        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
        
        // Enter key handlers
        departTimeField.addActionListener(e -> arriveTimeField.requestFocus());
        arriveTimeField.addActionListener(e -> aircraftTypeField.requestFocus());
        aircraftTypeField.addActionListener(e -> handleCreateFlight());
    }
    
    private void handleCreateFlight() {
        try {
            if (!validateForm()) {
                return;
            }
            
            Flight flight = createFlightFromForm();
            Flight createdFlight = flightDAO.createFlight(flight);
            
            FileLogger.getInstance().logInfo("Created flight: " + createdFlight.getFlightNo());
            JOptionPane.showMessageDialog(this, 
                "Flight created successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            loadFlights();
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to create flight: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to create flight: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateFlight() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a flight to update", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (!validateForm()) {
                return;
            }
            
            updateFlightFromForm(selectedFlight);
            boolean updated = flightDAO.updateFlight(selectedFlight);
            
            if (updated) {
                FileLogger.getInstance().logInfo("Updated flight: " + selectedFlight.getFlightNo());
                JOptionPane.showMessageDialog(this, 
                    "Flight updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                loadFlights();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update flight", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to update flight: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to update flight: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteFlight() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a flight to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete flight " + selectedFlight.getFlightNo() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = flightDAO.deleteFlight(selectedFlight.getFlightId());
                
                if (deleted) {
                    FileLogger.getInstance().logInfo("Deleted flight: " + selectedFlight.getFlightNo());
                    JOptionPane.showMessageDialog(this, 
                        "Flight deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    clearForm();
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete flight", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to delete flight: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete flight: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleTableSelection() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedFlight = getFlightFromTableRow(selectedRow);
            populateFormFromFlight(selectedFlight);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            selectedFlight = null;
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    private boolean validateForm() {
        if (flightNoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Flight number is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (originField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Origin is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (destinationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Destination is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            LocalDateTime.parse(departTimeField.getText().trim(), dateFormatter);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid departure time format. Use yyyy-MM-dd HH:mm", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            LocalDateTime.parse(arriveTimeField.getText().trim(), dateFormatter);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid arrival time format. Use yyyy-MM-dd HH:mm", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private Flight createFlightFromForm() {
        Flight flight = new Flight();
        flight.setFlightNo(flightNoField.getText().trim());
        flight.setOrigin(originField.getText().trim());
        flight.setDestination(destinationField.getText().trim());
        flight.setDepartTime(LocalDateTime.parse(departTimeField.getText().trim(), dateFormatter));
        flight.setArriveTime(LocalDateTime.parse(arriveTimeField.getText().trim(), dateFormatter));
        flight.setAircraftType(aircraftTypeField.getText().trim());
        flight.setStatus((Flight.FlightStatus) statusComboBox.getSelectedItem());
        return flight;
    }
    
    private void updateFlightFromForm(Flight flight) {
        flight.setFlightNo(flightNoField.getText().trim());
        flight.setOrigin(originField.getText().trim());
        flight.setDestination(destinationField.getText().trim());
        flight.setDepartTime(LocalDateTime.parse(departTimeField.getText().trim(), dateFormatter));
        flight.setArriveTime(LocalDateTime.parse(arriveTimeField.getText().trim(), dateFormatter));
        flight.setAircraftType(aircraftTypeField.getText().trim());
        flight.setStatus((Flight.FlightStatus) statusComboBox.getSelectedItem());
    }
    
    private void populateFormFromFlight(Flight flight) {
        flightNoField.setText(flight.getFlightNo());
        originField.setText(flight.getOrigin());
        destinationField.setText(flight.getDestination());
        departTimeField.setText(flight.getDepartTime().format(dateFormatter));
        arriveTimeField.setText(flight.getArriveTime().format(dateFormatter));
        aircraftTypeField.setText(flight.getAircraftType());
        statusComboBox.setSelectedItem(flight.getStatus());
    }
    
    private Flight getFlightFromTableRow(int row) {
        // This is a simplified approach - in a real app, you'd store the actual Flight objects
        // For now, we'll create a new Flight object from table data
        Flight flight = new Flight();
        flight.setFlightId(Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
        flight.setFlightNo(tableModel.getValueAt(row, 1).toString());
        flight.setOrigin(tableModel.getValueAt(row, 2).toString());
        flight.setDestination(tableModel.getValueAt(row, 3).toString());
        // Note: We'd need to parse the datetime strings back to LocalDateTime
        flight.setAircraftType(tableModel.getValueAt(row, 6).toString());
        flight.setStatus(Flight.FlightStatus.valueOf(tableModel.getValueAt(row, 7).toString()));
        return flight;
    }
    
    private void clearForm() {
        flightNoField.setText("");
        originField.setText("");
        destinationField.setText("");
        departTimeField.setText("");
        arriveTimeField.setText("");
        aircraftTypeField.setText("");
        statusComboBox.setSelectedItem(Flight.FlightStatus.SCHEDULED);
        
        selectedFlight = null;
        flightsTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        flightNoField.requestFocus();
    }
    
    private void loadFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            tableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                Object[] row = {
                    flight.getFlightId(),
                    flight.getFlightNo(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartTime().format(dateFormatter),
                    flight.getArriveTime().format(dateFormatter),
                    flight.getAircraftType(),
                    flight.getStatus()
                };
                tableModel.addRow(row);
            }
            
            FileLogger.getInstance().logInfo("Loaded " + flights.size() + " flights");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flights: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load flights: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Flight Scheduling");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set initial button states
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
} 