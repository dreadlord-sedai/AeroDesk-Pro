package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.dao.BaggageDAO;
import aerodesk.dao.BookingDAO;
import aerodesk.model.Baggage;
import aerodesk.model.Booking;
import aerodesk.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/**
 * Enhanced Baggage Handling UI for AeroDesk Pro
 * Handles baggage tracking and management with real-time updates
 */
public class BaggageFrame extends JFrame {
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton refreshButton;
    
    // Table components
    private JTable passengersTable;
    private DefaultTableModel passengersTableModel;
    private TableRowSorter<DefaultTableModel> passengersTableSorter;
    private JTable baggageTable;
    private DefaultTableModel baggageTableModel;
    private TableRowSorter<DefaultTableModel> baggageTableSorter;
    
    // Action buttons
    private JButton addBaggageButton;
    private JButton editBaggageButton;
    private JButton deleteBaggageButton;
    private JButton trackBaggageButton;
    private JButton exportButton;
    private JButton startSimulationButton;
    private JButton stopSimulationButton;
    
    // Status and progress
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel statsLabel;
    private JLabel simulationStatusLabel;
    
    // Data components
    private BaggageDAO baggageDAO;
    private BookingDAO bookingDAO;
    private Booking selectedBooking;
    private Baggage selectedBaggage;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Simulation components
    private Timer simulationTimer;
    private boolean simulationRunning = false;
    private Random random = new Random();
    
    // Search timer for debouncing
    private Timer searchTimer;
    
    public BaggageFrame() {
        this.baggageDAO = new BaggageDAO();
        this.bookingDAO = new BookingDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadCheckedInPassengers();
        loadAllBaggage();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(25);
        statusFilterComboBox = new JComboBox<>(new String[]{"All Status", "CHECKED_IN", "LOADED", "DELIVERED", "LOST"});
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");
        refreshButton = new JButton("Refresh");
        
        // Action buttons
        addBaggageButton = new JButton("Add Baggage");
        editBaggageButton = new JButton("Edit Baggage");
        deleteBaggageButton = new JButton("Delete Baggage");
        trackBaggageButton = new JButton("Track Baggage");
        exportButton = new JButton("Export Data");
        startSimulationButton = new JButton("Start Simulation");
        stopSimulationButton = new JButton("Stop Simulation");
        
        // Status components
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statsLabel = new JLabel("Total: 0 | Loaded: 0 | In Transit: 0 | Delivered: 0");
        simulationStatusLabel = new JLabel("Simulation: Stopped");
        
        // Apply enhanced styling
        applyEnhancedStyling();
        
        // Table setup
        setupTables();
        
        // Initialize simulation timer
        simulationTimer = new Timer(3000, e -> updateBaggageStatus());
        simulationTimer.setRepeats(true);
    }
    
    private void applyEnhancedStyling() {
        // Enhanced button styling
        styleEnhancedButton(addBaggageButton, "Add Baggage", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(editBaggageButton, "Edit Baggage", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(deleteBaggageButton, "Delete Baggage", ThemeManager.ERROR_RED, ThemeManager.WHITE);
        styleEnhancedButton(trackBaggageButton, "Track Baggage", ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        styleEnhancedButton(exportButton, "Export Data", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(startSimulationButton, "Start Simulation", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(stopSimulationButton, "Stop Simulation", ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        // Search button styling
        styleEnhancedButton(searchButton, "Search", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(clearSearchButton, "Clear", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        styleEnhancedButton(refreshButton, "Refresh", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Enhanced field styling
        styleEnhancedTextField(searchField, "Search baggage by tag number or passenger...");
        styleEnhancedComboBox(statusFilterComboBox);
        
        // Status label styling
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsLabel.setForeground(ThemeManager.PRIMARY_BLUE);
        simulationStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        simulationStatusLabel.setForeground(ThemeManager.ACCENT_ORANGE);
    }
    
    private void styleEnhancedButton(JButton button, String text, Color backgroundColor, Color textColor) {
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
    
    private void styleEnhancedTextField(JTextField textField, String tooltip) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setToolTipText(tooltip);
        textField.setPreferredSize(new Dimension(0, 35));
    }
    
    private void styleEnhancedComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(0, 35));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void setupTables() {
        // Passengers table
        String[] passengerColumns = {"Booking ID", "Passenger Name", "Flight No", "Seat", "Checked In", "Baggage Count"};
        passengersTableModel = new DefaultTableModel(passengerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengersTable = new JTable(passengersTableModel);
        passengersTableSorter = new TableRowSorter<>(passengersTableModel);
        passengersTable.setRowSorter(passengersTableSorter);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enhanced passengers table styling
        passengersTable.setRowHeight(35);
        passengersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passengersTable.setGridColor(ThemeManager.LIGHT_GRAY);
        passengersTable.setShowGrid(true);
        passengersTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced passengers table header
        passengersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        passengersTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        passengersTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        passengersTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        // Baggage table
        String[] baggageColumns = {"Tag Number", "Passenger Name", "Weight (kg)", "Type", "Status", "Created", "Last Updated"};
        baggageTableModel = new DefaultTableModel(baggageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baggageTable = new JTable(baggageTableModel);
        baggageTableSorter = new TableRowSorter<>(baggageTableModel);
        baggageTable.setRowSorter(baggageTableSorter);
        baggageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enhanced baggage table styling
        baggageTable.setRowHeight(35);
        baggageTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        baggageTable.setGridColor(ThemeManager.LIGHT_GRAY);
        baggageTable.setShowGrid(true);
        baggageTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced baggage table header
        baggageTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        baggageTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        baggageTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        baggageTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        // Set column widths
        baggageTable.getColumnModel().getColumn(0).setPreferredWidth(120);  // Tag Number
        baggageTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Passenger Name
        baggageTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Weight
        baggageTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Type
        baggageTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Status
        baggageTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Created
        baggageTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Last Updated
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Header panel with enhanced gradient
        JPanel headerPanel = createEnhancedHeader();
        
        // Main content panel with better organization
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBackground(ThemeManager.LIGHT_GRAY);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Search panel with improved design
        JPanel searchPanel = createEnhancedSearchPanel();
        
        // Content panels
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(ThemeManager.LIGHT_GRAY);
        
        // Left panel - Passengers
        JPanel passengersPanel = createEnhancedPassengersPanel();
        
        // Right panel - Baggage
        JPanel baggagePanel = createEnhancedBaggagePanel();
        
        contentPanel.add(passengersPanel);
        contentPanel.add(baggagePanel);
        
        // Status and button panel
        JPanel bottomPanel = createEnhancedBottomPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        mainContentPanel.add(searchPanel, BorderLayout.NORTH);
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        // Create gradient background
        headerPanel.setBackground(ThemeManager.ACCENT_ORANGE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Main title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Baggage Handling Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.WHITE);
        
        titlePanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Track and manage passenger baggage with real-time updates");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        subtitlePanel.setOpaque(false);
        subtitlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(subtitlePanel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    private JPanel createEnhancedSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(ThemeManager.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Search field panel
        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchFieldPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(ThemeManager.DARK_GRAY);
        
        searchFieldPanel.add(searchLabel);
        searchFieldPanel.add(searchField);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLabel.setForeground(ThemeManager.DARK_GRAY);
        
        filterPanel.add(filterLabel);
        filterPanel.add(statusFilterComboBox);
        
        // Button panel
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchButtonPanel.setOpaque(false);
        searchButtonPanel.add(searchButton);
        searchButtonPanel.add(clearSearchButton);
        searchButtonPanel.add(refreshButton);
        
        searchPanel.add(searchFieldPanel, BorderLayout.WEST);
        searchPanel.add(filterPanel, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);
        
        return searchPanel;
    }
    
    private JPanel createEnhancedPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel panelTitle = new JLabel("Checked-In Passengers");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelTitle.setForeground(ThemeManager.DARK_GRAY);
        
        titlePanel.add(panelTitle);
        
        // Table with enhanced scroll pane
        JScrollPane scrollPane = new JScrollPane(passengersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEnhancedBaggagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel panelTitle = new JLabel("Baggage Tracking");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelTitle.setForeground(ThemeManager.DARK_GRAY);
        
        titlePanel.add(panelTitle);
        
        // Table with enhanced scroll pane
        JScrollPane scrollPane = new JScrollPane(baggageTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEnhancedBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(ThemeManager.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Action buttons panel
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.add(addBaggageButton);
        actionButtonPanel.add(editBaggageButton);
        actionButtonPanel.add(deleteBaggageButton);
        actionButtonPanel.add(trackBaggageButton);
        actionButtonPanel.add(exportButton);
        
        // Simulation buttons panel
        JPanel simulationButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        simulationButtonPanel.setOpaque(false);
        simulationButtonPanel.add(startSimulationButton);
        simulationButtonPanel.add(stopSimulationButton);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setOpaque(false);
        
        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftStatusPanel.setOpaque(false);
        leftStatusPanel.add(new JLabel("Status:"));
        leftStatusPanel.add(statusLabel);
        leftStatusPanel.add(progressBar);
        
        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightStatusPanel.setOpaque(false);
        rightStatusPanel.add(statsLabel);
        rightStatusPanel.add(simulationStatusLabel);
        
        statusPanel.add(leftStatusPanel, BorderLayout.WEST);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);
        
        // Combine all panels
        JPanel buttonContainer = new JPanel(new BorderLayout(0, 10));
        buttonContainer.setOpaque(false);
        buttonContainer.add(actionButtonPanel, BorderLayout.NORTH);
        buttonContainer.add(simulationButtonPanel, BorderLayout.CENTER);
        
        bottomPanel.add(buttonContainer, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Enhanced button event handlers
        addBaggageButton.addActionListener(e -> addBaggage());
        editBaggageButton.addActionListener(e -> editBaggage());
        deleteBaggageButton.addActionListener(e -> deleteBaggage());
        trackBaggageButton.addActionListener(e -> trackBaggage());
        exportButton.addActionListener(e -> exportData());
        refreshButton.addActionListener(e -> refreshData());
        startSimulationButton.addActionListener(e -> startSimulation());
        stopSimulationButton.addActionListener(e -> stopSimulation());
        
        // Search functionality
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        statusFilterComboBox.addActionListener(e -> performSearch());
        
        // Real-time search with debouncing
        searchTimer = new Timer(500, e -> performSearch());
        searchTimer.setRepeats(false);
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchTimer.restart();
            }
        });
        
        // Enhanced table selection listeners
        passengersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = passengersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedBooking = getBookingFromTableRow(selectedRow);
                    addBaggageButton.setEnabled(true);
                    updateStatus("Selected passenger: " + selectedBooking.getPassengerName());
                } else {
                    selectedBooking = null;
                    addBaggageButton.setEnabled(false);
                    updateStatus("No passenger selected");
                }
            }
        });
        
        baggageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = baggageTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedBaggage = getBaggageFromTableRow(selectedRow);
                    editBaggageButton.setEnabled(true);
                    deleteBaggageButton.setEnabled(true);
                    trackBaggageButton.setEnabled(true);
                    updateStatus("Selected baggage: " + selectedBaggage.getTagNumber());
                } else {
                    selectedBaggage = null;
                    editBaggageButton.setEnabled(false);
                    deleteBaggageButton.setEnabled(false);
                    trackBaggageButton.setEnabled(false);
                    updateStatus("No baggage selected");
                }
            }
        });
        
        // Initialize button states
        editBaggageButton.setEnabled(false);
        deleteBaggageButton.setEnabled(false);
        trackBaggageButton.setEnabled(false);
        stopSimulationButton.setEnabled(false);
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
        // BaggageSimulator.getInstance().startSimulation(); // This line was removed as per the new_code
        startSimulationButton.setEnabled(false);
        stopSimulationButton.setEnabled(true);
        statusLabel.setText("Simulation running");
        statusLabel.setForeground(ThemeManager.WARNING_AMBER);
        FileLogger.getInstance().logInfo("Baggage simulation started");
    }
    
    private void stopSimulation() {
        // BaggageSimulator.getInstance().stopSimulation(); // This line was removed as per the new_code
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
        setTitle("AeroDesk Pro - Enhanced Baggage Handling");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
    
    // Enhanced baggage management methods
    private void editBaggage() {
        if (selectedBaggage == null) {
            JOptionPane.showMessageDialog(this, "Please select a baggage to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        showBaggageDialog("Edit Baggage", selectedBaggage);
    }
    
    private void showBaggageDialog(String title, Baggage baggage) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(400, 300));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.WHITE);
        
        JTextField tagNumberField = new JTextField();
        JTextField weightField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"CHECKED", "CARRY_ON", "SPECIAL"});
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"CHECKED_IN", "LOADED", "DELIVERED", "LOST"});
        
        // Pre-fill fields if editing
        if (baggage != null) {
            tagNumberField.setText(baggage.getTagNumber());
            weightField.setText(String.valueOf(baggage.getWeightKg()));
            typeComboBox.setSelectedItem(baggage.getBaggageType().toString());
            statusComboBox.setSelectedItem(baggage.getStatus().toString());
            tagNumberField.setEnabled(false); // Don't allow tag number editing
        }
        
        formPanel.add(createStyledLabel("Tag Number:"));
        formPanel.add(tagNumberField);
        formPanel.add(createStyledLabel("Weight (kg):"));
        formPanel.add(weightField);
        formPanel.add(createStyledLabel("Type:"));
        formPanel.add(typeComboBox);
        formPanel.add(createStyledLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(createStyledLabel("Passenger:"));
        formPanel.add(createStyledLabel(baggage != null ? "Passenger " + baggage.getBookingId() : selectedBooking.getPassengerName()));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        styleEnhancedButton(saveButton, "Save", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(cancelButton, "Cancel", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        saveButton.addActionListener(e -> {
            try {
                String tagNumber = tagNumberField.getText().trim();
                double weight = Double.parseDouble(weightField.getText().trim());
                String type = (String) typeComboBox.getSelectedItem();
                String status = (String) statusComboBox.getSelectedItem();
                
                if (tagNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tag number is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (baggage == null) {
                    // Adding new baggage
                    Baggage newBaggage = new Baggage();
                    newBaggage.setTagNumber(tagNumber);
                    newBaggage.setBookingId(selectedBooking.getBookingId());
                    newBaggage.setWeightKg(weight);
                    newBaggage.setBaggageType(Baggage.BaggageType.valueOf(type));
                    newBaggage.setStatus(Baggage.BaggageStatus.valueOf(status));
                    newBaggage.setCreatedAt(LocalDateTime.now());
                    newBaggage.setUpdatedAt(LocalDateTime.now());
                    
                    baggageDAO.createBaggage(newBaggage);
                    updateStatus("Baggage added successfully: " + tagNumber);
                } else {
                    // Updating existing baggage
                    baggage.setWeightKg(weight);
                    baggage.setBaggageType(Baggage.BaggageType.valueOf(type));
                    baggage.setStatus(Baggage.BaggageStatus.valueOf(status));
                    baggage.setUpdatedAt(LocalDateTime.now());
                    
                    baggageDAO.updateBaggage(baggage);
                    updateStatus("Baggage updated successfully: " + tagNumber);
                }
                
                dialog.dispose();
                loadAllBaggage();
                updateStats();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid weight", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving baggage: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeManager.DARK_GRAY);
        return label;
    }
    
    private void deleteBaggage() {
        if (selectedBaggage == null) {
            JOptionPane.showMessageDialog(this, "Please select a baggage to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete baggage " + selectedBaggage.getTagNumber() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                baggageDAO.deleteBaggage(selectedBaggage.getBaggageId());
                loadAllBaggage();
                updateStatus("Baggage deleted successfully: " + selectedBaggage.getTagNumber());
                updateStats();
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting baggage: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void trackBaggage() {
        if (selectedBaggage == null) {
            JOptionPane.showMessageDialog(this, "Please select a baggage to track", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Track Baggage: " + selectedBaggage.getTagNumber(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(500, 300));
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(ThemeManager.WHITE);
        
        // Baggage details
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        detailsPanel.setOpaque(false);
        
        detailsPanel.add(createStyledLabel("Tag Number:"));
        detailsPanel.add(new JLabel(selectedBaggage.getTagNumber()));
        detailsPanel.add(createStyledLabel("Passenger:"));
        detailsPanel.add(new JLabel("Passenger " + selectedBaggage.getBookingId()));
        detailsPanel.add(createStyledLabel("Weight:"));
        detailsPanel.add(new JLabel(selectedBaggage.getWeightKg() + " kg"));
        detailsPanel.add(createStyledLabel("Type:"));
        detailsPanel.add(new JLabel(selectedBaggage.getBaggageType().toString()));
        detailsPanel.add(createStyledLabel("Status:"));
        detailsPanel.add(new JLabel(selectedBaggage.getStatus().toString()));
        detailsPanel.add(createStyledLabel("Last Updated:"));
        detailsPanel.add(new JLabel(selectedBaggage.getUpdatedAt() != null ? selectedBaggage.getUpdatedAt().format(dateFormatter) : "N/A"));
        
        // Status timeline (simplified)
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBorder(BorderFactory.createTitledBorder("Status Timeline"));
        timelinePanel.setOpaque(false);
        
        JTextArea timelineArea = new JTextArea();
        timelineArea.setEditable(false);
        timelineArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timelineArea.setText("Created: " + (selectedBaggage.getCreatedAt() != null ? selectedBaggage.getCreatedAt().format(dateFormatter) : "N/A") + "\n" +
                           "Current Status: " + selectedBaggage.getStatus() + " (" + (selectedBaggage.getUpdatedAt() != null ? selectedBaggage.getUpdatedAt().format(dateFormatter) : "N/A") + ")");
        
        timelinePanel.add(new JScrollPane(timelineArea), BorderLayout.CENTER);
        
        contentPanel.add(detailsPanel, BorderLayout.NORTH);
        contentPanel.add(timelinePanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        styleEnhancedButton(closeButton, "Close", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(closeButton);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void exportData() {
        try {
            List<Baggage> allBaggage = baggageDAO.getAllBaggage();
            if (allBaggage.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No baggage data to export", "Export", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder csv = new StringBuilder();
            csv.append("Tag Number,Passenger ID,Weight,Type,Status,Created,Last Updated\n");
            
            for (Baggage baggage : allBaggage) {
                csv.append(String.format("\"%s\",\"%d\",%.2f,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    baggage.getTagNumber(),
                    baggage.getBookingId(),
                    baggage.getWeightKg(),
                    baggage.getBaggageType(),
                    baggage.getStatus(),
                    baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A",
                    baggage.getUpdatedAt() != null ? baggage.getUpdatedAt().format(dateFormatter) : "N/A"
                ));
            }
            
            // For simplicity, show in dialog (in real app, save to file)
            JDialog dialog = new JDialog(this, "Export Data", true);
            dialog.setLayout(new BorderLayout());
            dialog.setPreferredSize(new Dimension(600, 400));
            
            JTextArea exportArea = new JTextArea(csv.toString());
            exportArea.setEditable(false);
            exportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(exportArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JButton closeButton = new JButton("Close");
            styleEnhancedButton(closeButton, "Close", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
            closeButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            updateStatus("Data exported successfully");
            
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Search and filter methods
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String statusFilter = (String) statusFilterComboBox.getSelectedItem();
        
        try {
            List<Baggage> baggageList;
            
            baggageList = baggageDAO.getAllBaggage();
            
            // Apply search filter
            if (!searchTerm.isEmpty()) {
                List<Baggage> filteredList = new ArrayList<>();
                for (Baggage baggage : baggageList) {
                    if (baggage.getTagNumber().toLowerCase().contains(searchTerm.toLowerCase())) {
                        filteredList.add(baggage);
                    }
                }
                baggageList = filteredList;
            }
            
            // Apply status filter
            if (!statusFilter.equals("All Status")) {
                List<Baggage> filteredList = new ArrayList<>();
                for (Baggage baggage : baggageList) {
                    if (baggage.getStatus().toString().equals(statusFilter)) {
                        filteredList.add(baggage);
                    }
                }
                baggageList = filteredList;
            }
            
            updateBaggageTable(baggageList);
            updateStatus("Found " + baggageList.size() + " baggage items");
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error searching baggage: " + ex.getMessage());
            updateStatus("Search failed: " + ex.getMessage());
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        statusFilterComboBox.setSelectedItem("All Status");
        performSearch();
    }
    
    private void updateBaggageTable(List<Baggage> baggageList) {
        baggageTableModel.setRowCount(0);
        for (Baggage baggage : baggageList) {
            Object[] row = {
                baggage.getTagNumber(),
                "Passenger " + baggage.getBookingId(),
                baggage.getWeightKg() + " kg",
                baggage.getBaggageType().toString(),
                baggage.getStatus().toString(),
                baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A",
                baggage.getUpdatedAt() != null ? baggage.getUpdatedAt().format(dateFormatter) : "N/A"
            };
            baggageTableModel.addRow(row);
        }
    }
    
    private Baggage getBaggageFromTableRow(int row) {
        String tagNumber = (String) baggageTableModel.getValueAt(row, 0);
        try {
            return baggageDAO.getBaggageByTagNumber(tagNumber);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error getting baggage details: " + ex.getMessage());
            return null;
        }
    }
    
    // Status update methods
    private void updateStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
    }
    
    private void updateStats() {
        try {
            List<Baggage> allBaggage = baggageDAO.getAllBaggage();
            int total = allBaggage.size();
            int checkedIn = 0, delivered = 0;
            
            for (Baggage baggage : allBaggage) {
                switch (baggage.getStatus().toString()) {
                    case "CHECKED_IN": checkedIn++; break;
                    case "LOADED": checkedIn++; break;
                    case "DELIVERED": delivered++; break;
                }
            }
            
            statsLabel.setText(String.format("Total: %d | Checked In: %d | Delivered: %d", 
                total, checkedIn, delivered));
                
        } catch (DatabaseException ex) {
            statsLabel.setText("Stats: Error loading data");
        }
    }
    
    // Simulation methods
    private void updateBaggageStatus() {
        if (!simulationRunning) return;
        
        try {
            List<Baggage> allBaggage = baggageDAO.getAllBaggage();
            if (allBaggage.isEmpty()) return;
            
            // Randomly update status of some baggage
            int updateCount = 0;
            for (Baggage baggage : allBaggage) {
                if (random.nextInt(10) < 3) { // 30% chance to update
                    Baggage.BaggageStatus currentStatus = baggage.getStatus();
                    Baggage.BaggageStatus newStatus = getNextStatus(currentStatus);
                    
                    if (newStatus != currentStatus) {
                        baggage.setStatus(newStatus);
                        baggage.setUpdatedAt(LocalDateTime.now());
                        baggageDAO.updateBaggage(baggage);
                        updateCount++;
                    }
                }
            }
            
            if (updateCount > 0) {
                loadAllBaggage();
                updateStats();
                updateStatus("Simulation updated " + updateCount + " baggage items");
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error in simulation: " + ex.getMessage());
        }
    }
    
    private Baggage.BaggageStatus getNextStatus(Baggage.BaggageStatus currentStatus) {
        switch (currentStatus) {
            case CHECKED_IN: return random.nextBoolean() ? Baggage.BaggageStatus.LOADED : Baggage.BaggageStatus.CHECKED_IN;
            case LOADED: return random.nextBoolean() ? Baggage.BaggageStatus.DELIVERED : Baggage.BaggageStatus.LOADED;
            case DELIVERED: return Baggage.BaggageStatus.DELIVERED;
            case LOST: return Baggage.BaggageStatus.LOST;
            default: return Baggage.BaggageStatus.CHECKED_IN;
        }
    }
} 