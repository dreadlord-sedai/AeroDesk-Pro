package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.model.Booking;
import aerodesk.model.Flight;
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
import java.util.regex.Pattern;
import javax.swing.Timer;

/**
 * Passenger Check-In UI for AeroDesk Pro
 * Handles passenger check-in and boarding pass generation with enhanced features
 */
public class CheckInFrame extends JFrame {
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> statusFilterComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton refreshButton;
    
    // Table components
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Action buttons
    private JButton checkInButton;
    private JButton generateBoardingPassButton;
    private JButton exportButton;
    private JButton bulkCheckInButton;
    
    // Status and progress
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel statsLabel;
    
    // Data components
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private Booking selectedBooking;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Validation patterns
    private static final Pattern BOOKING_REF_PATTERN = Pattern.compile("^[A-Z]{2}\\d{6}$");
    private static final Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}\\d{3,4}$");
    
    // Search timer for debouncing
    private Timer searchTimer;
    
    public CheckInFrame() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadBookings();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(25);
        searchTypeComboBox = new JComboBox<>(new String[]{"Booking Reference", "Flight Number", "Passenger Name", "Passport Number"});
        statusFilterComboBox = new JComboBox<>(new String[]{"All Status", "Not Checked In", "Checked In"});
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");
        refreshButton = new JButton("Refresh");
        
        // Action buttons
        checkInButton = new JButton("Check In Passenger");
        generateBoardingPassButton = new JButton("Generate Boarding Pass");
        exportButton = new JButton("Export Data");
        bulkCheckInButton = new JButton("Bulk Check-In");
        
        // Status components
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statsLabel = new JLabel("Total: 0 | Checked In: 0 | Pending: 0");
        
        // Apply enhanced styling
        applyEnhancedStyling();
        
        // Table setup
        setupTable();
    }
    
    private void applyEnhancedStyling() {
        // Enhanced button styling
        styleEnhancedButton(checkInButton, "Check In Passenger", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(generateBoardingPassButton, "Generate Boarding Pass", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(exportButton, "Export Data", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(bulkCheckInButton, "Bulk Check-In", ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        
        // Search button styling
        styleEnhancedButton(searchButton, "Search", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(clearSearchButton, "Clear", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        styleEnhancedButton(refreshButton, "Refresh", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Enhanced field styling
        styleEnhancedTextField(searchField, "Enter search term...");
        styleEnhancedComboBox(searchTypeComboBox);
        styleEnhancedComboBox(statusFilterComboBox);
        
        // Status label styling
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsLabel.setForeground(ThemeManager.PRIMARY_BLUE);
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
    
    private void setupTable() {
        String[] columnNames = {"ID", "Booking Ref", "Passenger Name", "Flight No", "Seat", "Status", "Check-in Time", "Passport"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(tableSorter);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enhanced table styling
        bookingsTable.setRowHeight(35);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsTable.setGridColor(ThemeManager.LIGHT_GRAY);
        bookingsTable.setShowGrid(true);
        bookingsTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced table header
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        bookingsTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        bookingsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Booking Ref
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Passenger Name
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Flight No
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Seat
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Status
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Check-in Time
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Passport
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
        
        // Table panel
        JPanel tablePanel = createEnhancedTablePanel();
        
        // Status and button panel
        JPanel bottomPanel = createEnhancedBottomPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        mainContentPanel.add(searchPanel, BorderLayout.NORTH);
        mainContentPanel.add(tablePanel, BorderLayout.CENTER);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        // Create gradient background
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Main title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Passenger Check-In Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.WHITE);
        
        titlePanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage passenger check-ins and boarding pass generation");
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
        JPanel searchPanel = ThemeManager.createCardPanel();
        searchPanel.setLayout(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Search title
        JLabel searchTitle = new JLabel("Search & Filter Passengers");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchTitle.setForeground(ThemeManager.DARK_GRAY);
        searchPanel.add(searchTitle, BorderLayout.NORTH);
        
        // Search controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlsPanel.setOpaque(false);
        
        // Search field with placeholder
        JPanel searchFieldPanel = new JPanel(new BorderLayout(5, 0));
        searchFieldPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchFieldPanel.add(searchLabel, BorderLayout.NORTH);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        
        // Search type dropdown
        JPanel typePanel = new JPanel(new BorderLayout(5, 0));
        typePanel.setOpaque(false);
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(searchTypeComboBox, BorderLayout.CENTER);
        
        // Status filter dropdown
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setOpaque(false);
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusFilterComboBox, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(clearSearchButton);
        buttonsPanel.add(refreshButton);
        
        controlsPanel.add(searchFieldPanel);
        controlsPanel.add(typePanel);
        controlsPanel.add(statusPanel);
        controlsPanel.add(buttonsPanel);
        
        searchPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = ThemeManager.createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        searchPanel.add(ThemeManager.createBodyLabel("Search by:"));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(ThemeManager.createBodyLabel("Filter by Status:"));
        searchPanel.add(statusFilterComboBox);
        searchPanel.add(ThemeManager.createBodyLabel("Search term:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }
    
    private JPanel createEnhancedTablePanel() {
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout(0, 0));
        
        // Table header
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(ThemeManager.SECONDARY_BLUE);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel tableTitle = new JLabel("Passenger Bookings");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(ThemeManager.WHITE);
        tableHeader.add(tableTitle, BorderLayout.CENTER);
        
        // Stats label
        tableHeader.add(statsLabel, BorderLayout.EAST);
        
        tablePanel.add(tableHeader, BorderLayout.NORTH);
        
        // Enhanced table
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = ThemeManager.createSubheaderLabel("Passenger Bookings");
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createEnhancedBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(ThemeManager.LIGHT_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Status panel
        JPanel statusPanel = createEnhancedStatusPanel();
        
        // Button panel
        JPanel buttonPanel = createEnhancedButtonPanel();
        
        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private JPanel createEnhancedStatusPanel() {
        JPanel statusPanel = ThemeManager.createCardPanel();
        statusPanel.setLayout(new BorderLayout(10, 0));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Status text
        JPanel statusInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusInfoPanel.setOpaque(false);
        
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        
        statusInfoPanel.add(statusLabel);
        
        // Progress bar
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        statusPanel.add(statusInfoPanel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private JPanel createEnhancedButtonPanel() {
        JPanel buttonPanel = ThemeManager.createCardPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Primary actions group
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        primaryActions.setOpaque(false);
        primaryActions.add(checkInButton);
        primaryActions.add(generateBoardingPassButton);
        
        // Secondary actions group
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        secondaryActions.setOpaque(false);
        secondaryActions.add(exportButton);
        secondaryActions.add(bulkCheckInButton);
        
        // Add separators
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setBackground(ThemeManager.LIGHT_GRAY);
        
        buttonPanel.add(primaryActions);
        buttonPanel.add(separator);
        buttonPanel.add(secondaryActions);
        
        return buttonPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(checkInButton);
        buttonPanel.add(generateBoardingPassButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(bulkCheckInButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        searchButton.addActionListener(e -> searchBookings());
        clearSearchButton.addActionListener(e -> clearSearch());
        refreshButton.addActionListener(e -> loadBookings());
        checkInButton.addActionListener(e -> checkInPassenger());
        generateBoardingPassButton.addActionListener(e -> generateBoardingPass());
        exportButton.addActionListener(e -> exportData());
        bulkCheckInButton.addActionListener(e -> bulkCheckIn());
        
        // Initialize search timer for debouncing
        searchTimer = new Timer(500, e -> searchBookings());
        searchTimer.setRepeats(false);
        
        // Table selection listener
        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedBooking = getBookingFromTableRow(selectedRow);
                    checkInButton.setEnabled(true);
                    generateBoardingPassButton.setEnabled(true);
                } else {
                    selectedBooking = null;
                    checkInButton.setEnabled(false);
                    generateBoardingPassButton.setEnabled(false);
                }
            }
        });
        
        // Real-time search with debouncing
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Cancel previous timer
                searchTimer.restart();
            }
        });
        
        // Enter key in search field for immediate search
        searchField.addActionListener(e -> {
            searchTimer.stop();
            searchBookings();
        });
        
        // ComboBox change listeners for immediate search
        searchTypeComboBox.addActionListener(e -> {
            if (!searchField.getText().trim().isEmpty()) {
                searchTimer.restart();
            }
        });
        
        statusFilterComboBox.addActionListener(e -> {
            searchTimer.restart();
        });
    }
    
    private void searchBookings() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        String statusFilter = (String) statusFilterComboBox.getSelectedItem();
        
        if (searchTerm.isEmpty() && statusFilter.equals("All Status")) {
            loadBookings();
            return;
        }
        
        try {
            List<Booking> bookings = new ArrayList<>();
            
            switch (searchType) {
                case "Booking Reference":
                    if (BOOKING_REF_PATTERN.matcher(searchTerm).matches()) {
                        Booking booking = bookingDAO.getBookingByReference(searchTerm);
                        if (booking != null) {
                            bookings.add(booking);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Booking Reference format. Example: AA123456", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    break;
                case "Flight Number":
                    if (FLIGHT_NUMBER_PATTERN.matcher(searchTerm).matches()) {
                        bookings = bookingDAO.getAllBookings(); // This case needs actual flight number lookup
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Flight Number format. Example: AA123 or A1234", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    break;
                case "Passenger Name":
                    bookings = bookingDAO.getAllBookings(); // This case needs actual passenger name lookup
                    break;
                case "Passport Number":
                    bookings = bookingDAO.getAllBookings(); // This case needs actual passport number lookup
                    break;
            }
            
            updateTable(bookings);
            FileLogger.getInstance().logInfo("Searched bookings: " + searchType + " = " + searchTerm + " (Status: " + statusFilter + ")");
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error searching bookings: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error searching bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        searchTypeComboBox.setSelectedIndex(0);
        statusFilterComboBox.setSelectedIndex(0);
        loadBookings();
    }
    
    private void loadBookings() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            updateTable(bookings);
            FileLogger.getInstance().logInfo("Loaded " + bookings.size() + " bookings");
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error loading bookings: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking booking : bookings) {
            Object[] row = {
                booking.getBookingId(),
                booking.getBookingReference(),
                booking.getPassengerName(),
                "Flight " + booking.getFlightId(), // Would need to get actual flight number
                booking.getSeatNo(),
                booking.isCheckedIn() ? "Checked In" : "Not Checked In",
                booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "Not checked in",
                "N/A"
            };
            tableModel.addRow(row);
        }
        updateStats();
    }
    
    private Booking getBookingFromTableRow(int row) {
        int bookingId = (Integer) tableModel.getValueAt(row, 0);
        try {
            return bookingDAO.getBookingById(bookingId);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error getting booking details: " + ex.getMessage());
            return null;
        }
    }
    
    private void checkInPassenger() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to check in", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedBooking.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, "Passenger is already checked in", "Already Checked In", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            selectedBooking.setCheckedIn(true);
            selectedBooking.setCheckInTime(LocalDateTime.now());
            bookingDAO.updateBooking(selectedBooking);
            
            FileLogger.getInstance().logInfo("Passenger checked in: " + selectedBooking.getPassengerName());
            JOptionPane.showMessageDialog(this, 
                "Passenger " + selectedBooking.getPassengerName() + " checked in successfully!", 
                "Check-In Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadBookings();
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error checking in passenger: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error checking in passenger: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateBoardingPass() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to generate boarding pass", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!selectedBooking.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, "Passenger must be checked in first", "Not Checked In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Flight flight = flightDAO.getFlightById(selectedBooking.getFlightId());
            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight information not found", "Flight Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String boardingPass = generateBoardingPassText(selectedBooking, flight);
            
            // Show boarding pass in a dialog
            JTextArea boardingPassArea = new JTextArea(boardingPass);
            boardingPassArea.setEditable(false);
            boardingPassArea.setFont(ThemeManager.MONOSPACE_FONT);
            ThemeManager.styleTextArea(boardingPassArea);
            
            JScrollPane scrollPane = new JScrollPane(boardingPassArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Boarding Pass - " + selectedBooking.getPassengerName(), JOptionPane.INFORMATION_MESSAGE);
            
            FileLogger.getInstance().logInfo("Boarding pass generated for: " + selectedBooking.getPassengerName());
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error generating boarding pass: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error generating boarding pass: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateBoardingPassText(Booking booking, Flight flight) {
        StringBuilder boardingPass = new StringBuilder();
        boardingPass.append("╔══════════════════════════════════════════════════════════════╗\n");
        boardingPass.append("║                        BOARDING PASS                        ║\n");
        boardingPass.append("╠══════════════════════════════════════════════════════════════╣\n");
        boardingPass.append("║ Passenger: ").append(String.format("%-45s", booking.getPassengerName())).append("║\n");
        boardingPass.append("║ Booking Ref: ").append(String.format("%-42s", booking.getBookingReference())).append("║\n");
        boardingPass.append("║ Flight: ").append(String.format("%-47s", flight.getFlightNo())).append("║\n");
        boardingPass.append("║ Seat: ").append(String.format("%-49s", booking.getSeatNo())).append("║\n");
        boardingPass.append("║ Route: ").append(String.format("%-48s", flight.getOrigin() + " → " + flight.getDestination())).append("║\n");
        boardingPass.append("║ Departure: ").append(String.format("%-44s", flight.getDepartTime().format(dateFormatter))).append("║\n");
        boardingPass.append("║ Gate: ").append(String.format("%-49s", "TBD")).append("║\n");
        boardingPass.append("║ Check-in Time: ").append(String.format("%-40s", booking.getCheckInTime().format(dateFormatter))).append("║\n");
        boardingPass.append("║                                                              ║\n");
        boardingPass.append("║ Please arrive at the gate 30 minutes before departure       ║\n");
        boardingPass.append("╚══════════════════════════════════════════════════════════════╝\n");
        
        return boardingPass.toString();
    }
    
    private void exportData() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            String csvContent = "ID,Booking Reference,Passenger Name,Flight No,Seat,Status,Check-in Time,Passport Number\n";
            for (Booking booking : bookings) {
                csvContent += String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                    booking.getBookingId(),
                    booking.getBookingReference(),
                    booking.getPassengerName(),
                    "Flight " + booking.getFlightId(),
                    booking.getSeatNo(),
                    booking.isCheckedIn() ? "Checked In" : "Not Checked In",
                    booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "Not checked in",
                    "N/A"
                );
            }
            
            String filename = "passenger_bookings_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            // Simple file save implementation
            try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
                writer.write(csvContent);
            }
            FileLogger.getInstance().logInfo("Data exported to " + filename);
            JOptionPane.showMessageDialog(this, "Data exported successfully to " + filename, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            FileLogger.getInstance().logError("Error exporting data: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bulkCheckIn() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to bulk check in", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedBooking.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, "Passenger is already checked in", "Already Checked In", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to bulk check in all passengers?", "Confirm Bulk Check-In", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                progressBar.setVisible(true);
                statusLabel.setText("Bulk checking in...");
                bookingsTable.setEnabled(false);
                
                // Simple bulk check-in implementation
                List<Booking> allBookings = bookingDAO.getAllBookings();
                for (Booking booking : allBookings) {
                    if (!booking.isCheckedIn()) {
                        booking.setCheckedIn(true);
                        booking.setCheckInTime(LocalDateTime.now());
                        bookingDAO.updateBooking(booking);
                    }
                }
                
                loadBookings();
                statusLabel.setText("Bulk check-in complete!");
                progressBar.setVisible(false);
                bookingsTable.setEnabled(true);
                FileLogger.getInstance().logInfo("All bookings bulk checked in.");
                JOptionPane.showMessageDialog(this, "All bookings bulk checked in successfully!", "Bulk Check-In Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Error bulk checking in: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error bulk checking in: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStats() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            int total = bookings.size();
            int checkedIn = 0;
            int notCheckedIn = 0;

            for (Booking booking : bookings) {
                if (booking.isCheckedIn()) {
                    checkedIn++;
                } else {
                    notCheckedIn++;
                }
            }

            statsLabel.setText("Total: " + total + " | Checked In: " + checkedIn + " | Pending: " + notCheckedIn);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error updating stats: " + ex.getMessage());
            statsLabel.setText("Error loading stats");
        }
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Passenger Check-In Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1200, 700));
        
        // Add tooltips for better UX
        addTooltips();
        
        ThemeManager.styleFrame(this);
    }
    
    private void addTooltips() {
        // Button tooltips
        checkInButton.setToolTipText("Check in the selected passenger");
        generateBoardingPassButton.setToolTipText("Generate boarding pass for checked-in passenger");
        exportButton.setToolTipText("Export passenger data to CSV file");
        bulkCheckInButton.setToolTipText("Check in all pending passengers");
        
        // Search tooltips
        searchButton.setToolTipText("Search for passengers");
        clearSearchButton.setToolTipText("Clear search filters");
        refreshButton.setToolTipText("Refresh passenger list");
        
        // Table tooltip
        bookingsTable.setToolTipText("Select a passenger to perform actions");
        
        // Field tooltips
        searchField.setToolTipText("Enter search term (booking ref, flight number, passenger name, or passport)");
    }
} 