package aerodesk.ui;

import aerodesk.model.Booking;
import aerodesk.model.Flight;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import aerodesk.util.ThemeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Passenger Check-In UI for AeroDesk Pro
 * Handles passenger check-in and boarding pass generation
 */
public class CheckInFrame extends JFrame {
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JButton searchButton;
    private JButton refreshButton;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton checkInButton;
    private JButton generateBoardingPassButton;
    
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private Booking selectedBooking;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
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
        searchField = new JTextField(20);
        searchTypeComboBox = new JComboBox<>(new String[]{"Booking Reference", "Flight Number", "Passenger Name"});
        searchButton = new JButton("🔍 Search");
        refreshButton = new JButton("🔄 Refresh");
        
        // Apply modern styling
        ThemeManager.styleTextField(searchField);
        ThemeManager.styleComboBox(searchTypeComboBox);
        ThemeManager.styleButton(searchButton, ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        ThemeManager.styleButton(refreshButton, ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Table
        String[] columnNames = {"ID", "Booking Ref", "Passenger", "Flight No", "Seat", "Checked In", "Check-in Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.styleTable(bookingsTable);
        
        // Action buttons
        checkInButton = new JButton("✅ Check In");
        generateBoardingPassButton = new JButton("🎫 Generate Boarding Pass");
        
        ThemeManager.styleButton(checkInButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(generateBoardingPassButton, ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with gradient
        JPanel headerPanel = ThemeManager.createGradientPanel();
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("👤 Passenger Check-In Management");
        titleLabel.setForeground(ThemeManager.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = ThemeManager.createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        searchPanel.add(ThemeManager.createBodyLabel("🔍 Search by:"));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(ThemeManager.createBodyLabel("🔎 Search term:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        return searchPanel;
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
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        buttonPanel.add(checkInButton);
        buttonPanel.add(generateBoardingPassButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        searchButton.addActionListener(e -> searchBookings());
        refreshButton.addActionListener(e -> loadBookings());
        checkInButton.addActionListener(e -> checkInPassenger());
        generateBoardingPassButton.addActionListener(e -> generateBoardingPass());
        
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
        
        // Enter key in search field
        searchField.addActionListener(e -> searchBookings());
    }
    
    private void searchBookings() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        
        if (searchTerm.isEmpty()) {
            loadBookings();
            return;
        }
        
        try {
            List<Booking> bookings = new ArrayList<>();
            
            switch (searchType) {
                case "Booking Reference":
                    Booking booking = bookingDAO.getBookingByReference(searchTerm);
                    if (booking != null) {
                        bookings.add(booking);
                    }
                    break;
                case "Flight Number":
                    // Search by flight number - would need to implement this
                    bookings = bookingDAO.getAllBookings();
                    break;
                case "Passenger Name":
                    // Search by passenger name - would need to implement this
                    bookings = bookingDAO.getAllBookings();
                    break;
            }
            
            updateTable(bookings);
            FileLogger.getInstance().logInfo("Searched bookings: " + searchType + " = " + searchTerm);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error searching bookings: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error searching bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
                booking.isCheckedIn() ? "Yes" : "No",
                booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "Not checked in"
            };
            tableModel.addRow(row);
        }
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
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Passenger Check-In");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        ThemeManager.styleFrame(this);
    }
} 