package aerodesk.ui;

import aerodesk.model.Gate;
import aerodesk.model.GateAssignment;
import aerodesk.model.Flight;
import aerodesk.dao.GateDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.exception.GateConflictException;
import aerodesk.util.FileLogger;
import aerodesk.util.ThemeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Gate Management UI for AeroDesk Pro
 * Handles gate assignments and conflict detection
 */
public class GateManagementFrame extends JFrame {
    private JTable gatesTable;
    private DefaultTableModel gatesTableModel;
    private JTable assignmentsTable;
    private DefaultTableModel assignmentsTableModel;
    private JButton addGateButton;
    private JButton assignFlightButton;
    private JButton removeAssignmentButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    
    private GateDAO gateDAO;
    private FlightDAO flightDAO;
    private Gate selectedGate;
    private GateAssignment selectedAssignment;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public GateManagementFrame() {
        this.gateDAO = new GateDAO();
        this.flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadGates();
        loadAssignments();
    }
    
    private void initializeComponents() {
        // Gates table
        String[] gateColumns = {"Gate ID", "Gate Name", "Active", "Created At"};
        gatesTableModel = new DefaultTableModel(gateColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gatesTable = new JTable(gatesTableModel);
        gatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(gatesTable);
        
        // Assignments table
        String[] assignmentColumns = {"Assignment ID", "Gate", "Flight No", "Assigned From", "Assigned To"};
        assignmentsTableModel = new DefaultTableModel(assignmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentsTable = new JTable(assignmentsTableModel);
        assignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(assignmentsTable);
        
        // Buttons
        addGateButton = new JButton("➕ Add Gate");
        assignFlightButton = new JButton("✈️ Assign Flight");
        removeAssignmentButton = new JButton("❌ Remove Assignment");
        refreshButton = new JButton("🔄 Refresh");
        
        ThemeManager.styleButton(addGateButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(assignFlightButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(removeAssignmentButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Status label
        statusLabel = ThemeManager.createStatusLabel("Ready", ThemeManager.SUCCESS_GREEN);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Gradient header panel
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(800, 80));
        
        JLabel titleLabel = ThemeManager.createTitleLabel("🚪 Gate Management System");
        titleLabel.setForeground(ThemeManager.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(ThemeManager.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left panel - Gates
        JPanel gatesPanel = createGatesPanel();
        
        // Right panel - Assignments
        JPanel assignmentsPanel = createAssignmentsPanel();
        
        contentPanel.add(gatesPanel);
        contentPanel.add(assignmentsPanel);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Status panel
        JPanel statusPanel = ThemeManager.createCardPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.add(ThemeManager.createBodyLabel("Status: "));
        statusPanel.add(statusLabel);
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGatesPanel() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("🚪 Available Gates");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(gatesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAssignmentsPanel() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ThemeManager.WHITE);
        JLabel headerLabel = ThemeManager.createSubheaderLabel("✈️ Gate Assignments");
        headerPanel.add(headerLabel);
        
        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = ThemeManager.createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.add(addGateButton);
        panel.add(assignFlightButton);
        panel.add(removeAssignmentButton);
        panel.add(refreshButton);
        return panel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        addGateButton.addActionListener(e -> handleAddGate());
        assignFlightButton.addActionListener(e -> handleAssignFlight());
        removeAssignmentButton.addActionListener(e -> handleRemoveAssignment());
        refreshButton.addActionListener(e -> handleRefresh());
        
        // Table selection handlers
        gatesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleGateSelection();
            }
        });
        
        assignmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleAssignmentSelection();
            }
        });
    }
    
    private void handleAddGate() {
        showAddGateDialog();
    }
    
    private void showAddGateDialog() {
        JDialog dialog = new JDialog(this, "Add New Gate", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ThemeManager.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Gate name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ThemeManager.createBodyLabel("Gate Name:"), gbc);
        JTextField gateNameField = new JTextField(15);
        ThemeManager.styleTextField(gateNameField);
        gbc.gridx = 1;
        formPanel.add(gateNameField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        JButton addButton = new JButton("➕ Add");
        JButton cancelButton = new JButton("❌ Cancel");
        
        ThemeManager.styleButton(addButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(cancelButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        addButton.addActionListener(e -> {
            try {
                String gateName = gateNameField.getText().trim();
                
                if (gateName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Gate name is required", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Gate gate = new Gate(gateName);
                Gate createdGate = gateDAO.createGate(gate);
                
                FileLogger.getInstance().logInfo("Added gate: " + createdGate.getGateName());
                JOptionPane.showMessageDialog(dialog, 
                    "Gate added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                loadGates();
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to add gate: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, 
                    "Failed to add gate: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private void handleAssignFlight() {
        if (selectedGate == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a gate to assign a flight to", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        showAssignFlightDialog();
    }
    
    private void showAssignFlightDialog() {
        JDialog dialog = new JDialog(this, "Assign Flight to Gate", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ThemeManager.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Gate info
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ThemeManager.createBodyLabel("Gate:"), gbc);
        gbc.gridx = 1;
        formPanel.add(ThemeManager.createSubheaderLabel(selectedGate.getGateName()), gbc);
        
        // Flight selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(ThemeManager.createBodyLabel("Flight:"), gbc);
        JComboBox<Flight> flightComboBox = new JComboBox<>();
        ThemeManager.styleComboBox(flightComboBox);
        gbc.gridx = 1;
        formPanel.add(flightComboBox, gbc);
        
        // Load flights
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            for (Flight flight : flights) {
                flightComboBox.addItem(flight);
            }
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flights: " + ex.getMessage());
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        JButton assignButton = new JButton("✈️ Assign");
        JButton cancelButton = new JButton("❌ Cancel");
        
        ThemeManager.styleButton(assignButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(cancelButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        assignButton.addActionListener(e -> {
            Flight selectedFlight = (Flight) flightComboBox.getSelectedItem();
            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please select a flight", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                GateAssignment assignment = new GateAssignment(
                    selectedFlight.getFlightId(),
                    selectedGate.getGateId(),
                    selectedFlight.getDepartTime(),
                    selectedFlight.getArriveTime()
                );
                
                GateAssignment createdAssignment = gateDAO.createAssignment(assignment);
                
                FileLogger.getInstance().logInfo("Assigned flight " + selectedFlight.getFlightNo() + 
                    " to gate " + selectedGate.getGateName());
                JOptionPane.showMessageDialog(dialog, 
                    "Flight assigned successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                loadAssignments();
                
            } catch (GateConflictException ex) {
                FileLogger.getInstance().logError("Gate conflict: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, 
                    "Gate conflict detected: " + ex.getMessage(), 
                    "Conflict Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to assign flight: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, 
                    "Failed to assign flight: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(450, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private void handleRemoveAssignment() {
        if (selectedAssignment == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select an assignment to remove", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove this assignment?", 
            "Confirm Remove", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean removed = gateDAO.removeAssignment(selectedAssignment.getAssignmentId());
                
                if (removed) {
                    FileLogger.getInstance().logInfo("Removed gate assignment: " + selectedAssignment.getAssignmentId());
                    JOptionPane.showMessageDialog(this, 
                        "Assignment removed successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadAssignments();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to remove assignment", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to remove assignment: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Failed to remove assignment: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleRefresh() {
        loadGates();
        loadAssignments();
        statusLabel.setText("Data refreshed at " + LocalDateTime.now().format(dateFormatter));
    }
    
    private void handleGateSelection() {
        int selectedRow = gatesTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedGate = getGateFromTableRow(selectedRow);
            assignFlightButton.setEnabled(true);
        } else {
            selectedGate = null;
            assignFlightButton.setEnabled(false);
        }
    }
    
    private void handleAssignmentSelection() {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedAssignment = getAssignmentFromTableRow(selectedRow);
            removeAssignmentButton.setEnabled(true);
        } else {
            selectedAssignment = null;
            removeAssignmentButton.setEnabled(false);
        }
    }
    
    private Gate getGateFromTableRow(int row) {
        Gate gate = new Gate();
        gate.setGateId(Integer.parseInt(gatesTableModel.getValueAt(row, 0).toString()));
        gate.setGateName(gatesTableModel.getValueAt(row, 1).toString());
        gate.setActive(Boolean.parseBoolean(gatesTableModel.getValueAt(row, 2).toString()));
        return gate;
    }
    
    private GateAssignment getAssignmentFromTableRow(int row) {
        GateAssignment assignment = new GateAssignment();
        assignment.setAssignmentId(Integer.parseInt(assignmentsTableModel.getValueAt(row, 0).toString()));
        return assignment;
    }
    
    private void loadGates() {
        try {
            List<Gate> gates = gateDAO.getAllGates();
            gatesTableModel.setRowCount(0);
            
            for (Gate gate : gates) {
                Object[] row = {
                    gate.getGateId(),
                    gate.getGateName(),
                    gate.isActive(),
                    gate.getCreatedAt() != null ? gate.getCreatedAt().format(dateFormatter) : "N/A"
                };
                gatesTableModel.addRow(row);
            }
            
            FileLogger.getInstance().logInfo("Loaded " + gates.size() + " gates");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load gates: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load gates: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAssignments() {
        try {
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            assignmentsTableModel.setRowCount(0);
            
            for (GateAssignment assignment : assignments) {
                try {
                    Gate gate = gateDAO.getGateById(assignment.getGateId());
                    Flight flight = flightDAO.getFlightById(assignment.getFlightId());
                    
                    String gateInfo = gate != null ? gate.getGateName() : "Unknown";
                    String flightInfo = flight != null ? flight.getFlightNo() : "Unknown";
                    
                    Object[] row = {
                        assignment.getAssignmentId(),
                        gateInfo,
                        flightInfo,
                        assignment.getAssignedFrom() != null ? assignment.getAssignedFrom().format(dateFormatter) : "N/A",
                        assignment.getAssignedTo() != null ? assignment.getAssignedTo().format(dateFormatter) : "N/A"
                    };
                    assignmentsTableModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading assignment details: " + ex.getMessage());
                }
            }
            
            FileLogger.getInstance().logInfo("Loaded " + assignments.size() + " gate assignments");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load assignments: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load assignments: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Gate Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
        
        // Set initial button states
        assignFlightButton.setEnabled(false);
        removeAssignmentButton.setEnabled(false);
    }
} 