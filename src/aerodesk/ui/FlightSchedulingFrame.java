package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.util.IconManager;
import aerodesk.dao.FlightDAO;
import aerodesk.model.Flight;
import aerodesk.exception.DatabaseException;

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
        flightNoField = new JTextField(15);
        originField = new JTextField(15);
        destinationField = new JTextField(15);
        departTimeField = new JTextField(15);
        arriveTimeField = new JTextField(15);
        aircraftTypeField = new JTextField(15);
        statusComboBox = new JComboBox<>(Flight.FlightStatus.values());
        
        createButton = new JButton(IconManager.getTextIcon("flight") + " Create Flight");
        updateButton = new JButton("🔄 Update Flight");
        deleteButton = new JButton("🗑️ Delete Flight");
        clearButton = new JButton("🧹 Clear Form");
        refreshButton = new JButton("🔄 Refresh");
        
        // Apply modern styling
        ThemeManager.styleTextField(flightNoField);
        ThemeManager.styleTextField(originField);
        ThemeManager.styleTextField(destinationField);
        ThemeManager.styleTextField(departTimeField);
        ThemeManager.styleTextField(arriveTimeField);
        ThemeManager.styleTextField(aircraftTypeField);
        ThemeManager.styleComboBox(statusComboBox);
        
        ThemeManager.styleButton(createButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(updateButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(deleteButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        ThemeManager.styleButton(clearButton, ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Table setup
        String[] columnNames = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Aircraft", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        ThemeManager.styleTable(flightsTable);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createHeaderLabel(IconManager.getTextIcon("flight") + " Flight Scheduling Management");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeManager.WHITE);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = ThemeManager.createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(350, 0));
        
        // Form title
        JLabel formTitle = ThemeManager.createSubheaderLabel("Flight Details");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Add form fields
        formPanel.add(createFormField(IconManager.getTextIcon("flight") + " Flight No:", flightNoField));
        formPanel.add(createFormField("🛫 Origin:", originField));
        formPanel.add(createFormField("🛬 Destination:", destinationField));
        formPanel.add(createFormField("🕐 Depart Time (yyyy-MM-dd HH:mm):", departTimeField));
        formPanel.add(createFormField("🕐 Arrive Time (yyyy-MM-dd HH:mm):", arriveTimeField));
        formPanel.add(createFormField(IconManager.getTextIcon("flight") + " Aircraft Type:", aircraftTypeField));
        formPanel.add(createFormField(IconManager.getTextIcon("status") + " Status:", statusComboBox));
        
        return formPanel;
    }
    
    private JPanel createFormField(String labelText, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(ThemeManager.WHITE);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel label = ThemeManager.createBodyLabel(labelText);
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(component, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = ThemeManager.createSubheaderLabel("Flight Schedule");
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFlight();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFlight();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFlight();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFlights();
            }
        });
        
        // Table selection listener
        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = flightsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadFlightToForm(selectedRow);
                }
            }
        });
    }
    
    private void createFlight() {
        try {
            Flight flight = getFlightFromForm();
            if (flight != null) {
                flightDAO.createFlight(flight);
                FileLogger.getInstance().logInfo("Flight created: " + flight.getFlightNo());
                JOptionPane.showMessageDialog(this, "Flight created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            }
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error creating flight: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error creating flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateFlight() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Flight flight = getFlightFromForm();
            if (flight != null) {
                flight.setFlightId(selectedFlight.getFlightId());
                flightDAO.updateFlight(flight);
                FileLogger.getInstance().logInfo("Flight updated: " + flight.getFlightNo());
                JOptionPane.showMessageDialog(this, "Flight updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            }
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error updating flight: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteFlight() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete flight " + selectedFlight.getFlightNo() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                flightDAO.deleteFlight(selectedFlight.getFlightId());
                FileLogger.getInstance().logInfo("Flight deleted: " + selectedFlight.getFlightNo());
                JOptionPane.showMessageDialog(this, "Flight deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Error deleting flight: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
    }
    
    private Flight getFlightFromForm() {
        String flightNo = flightNoField.getText().trim();
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String departTimeStr = departTimeField.getText().trim();
        String arriveTimeStr = arriveTimeField.getText().trim();
        String aircraftType = aircraftTypeField.getText().trim();
        Flight.FlightStatus status = (Flight.FlightStatus) statusComboBox.getSelectedItem();
        
        if (flightNo.isEmpty() || origin.isEmpty() || destination.isEmpty() || 
            departTimeStr.isEmpty() || arriveTimeStr.isEmpty() || aircraftType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        try {
            LocalDateTime departTime = LocalDateTime.parse(departTimeStr, dateFormatter);
            LocalDateTime arriveTime = LocalDateTime.parse(arriveTimeStr, dateFormatter);
            
            if (arriveTime.isBefore(departTime)) {
                JOptionPane.showMessageDialog(this, "Arrival time must be after departure time", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            Flight flight = new Flight(flightNo, origin, destination, departTime, arriveTime, aircraftType);
            flight.setStatus(status);
            return flight;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd HH:mm", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private void loadFlightToForm(int row) {
        String flightNo = (String) tableModel.getValueAt(row, 0);
        String origin = (String) tableModel.getValueAt(row, 1);
        String destination = (String) tableModel.getValueAt(row, 2);
        String departTime = (String) tableModel.getValueAt(row, 3);
        String arriveTime = (String) tableModel.getValueAt(row, 4);
        String aircraftType = (String) tableModel.getValueAt(row, 5);
        String statusStr = (String) tableModel.getValueAt(row, 6);
        
        flightNoField.setText(flightNo);
        originField.setText(origin);
        destinationField.setText(destination);
        departTimeField.setText(departTime);
        arriveTimeField.setText(arriveTime);
        aircraftTypeField.setText(aircraftType);
        
        try {
            Flight.FlightStatus status = Flight.FlightStatus.valueOf(statusStr);
            statusComboBox.setSelectedItem(status);
        } catch (IllegalArgumentException ex) {
            statusComboBox.setSelectedItem(Flight.FlightStatus.SCHEDULED);
        }
        
        // Find and set selected flight
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            for (Flight flight : flights) {
                if (flight.getFlightNo().equals(flightNo)) {
                    selectedFlight = flight;
                    break;
                }
            }
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error loading flight details: " + ex.getMessage());
        }
    }
    
    private void loadFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            tableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                Object[] row = {
                    flight.getFlightNo(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartTime().format(dateFormatter),
                    flight.getArriveTime().format(dateFormatter),
                    flight.getAircraftType(),
                    flight.getStatus().toString()
                };
                tableModel.addRow(row);
            }
            
            FileLogger.getInstance().logInfo("Loaded " + flights.size() + " flights");
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error loading flights: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Flight Scheduling");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
} 