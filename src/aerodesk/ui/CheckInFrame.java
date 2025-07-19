package aerodesk.ui;

import aerodesk.model.Booking;
import aerodesk.model.Flight;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Passenger Check-In UI for AeroDesk Pro
 * Handles passenger check-in operations
 */
public class CheckInFrame extends JFrame {
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JButton searchButton;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton checkInButton;
    private JButton refreshButton;
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
        loadAllBookings();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        searchTypeComboBox = new JComboBox<>(new String[]{"Booking Reference", "Flight Number", "Passenger Name"});
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");
        
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
        
        // Action buttons
        checkInButton = new JButton("Check In");
        generateBoardingPassButton = new JButton("Generate Boarding Pass");
        
        // Set preferred sizes
        searchField.setPreferredSize(new Dimension(200, 25));
        searchTypeComboBox.setPreferredSize(new Dimension(150, 25));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Passenger Check-In Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Bookings"));
        
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(new JLabel("Search term:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Bookings"));
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(checkInButton);
        buttonPanel.add(generateBoardingPassButton);
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        searchButton.addActionListener(e -> handleSearch());
        refreshButton.addActionListener(e -> loadAllBookings());
        checkInButton.addActionListener(e -> handleCheckIn());
        generateBoardingPassButton.addActionListener(e -> handleGenerateBoardingPass());
        
        // Table selection handler
        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
        
        // Enter key in search field
        searchField.addActionListener(e -> handleSearch());
    }
    
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllBookings();
            return;
        }
        
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        
        try {
            List<Booking> results = null;
            
            switch (searchType) {
                case "Booking Reference":
                    Booking booking = bookingDAO.getBookingByReference(searchTerm);
                    if (booking != null) {
                        results = List.of(booking);
                    }
                    break;
                case "Flight Number":
                    results = searchBookingsByFlightNumber(searchTerm);
                    break;
                case "Passenger Name":
                    results = searchBookingsByPassengerName(searchTerm);
                    break;
            }
            
            if (results != null) {
                displayBookings(results);
            } else {
                displayBookings(List.of());
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Search failed: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Search failed: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<Booking> searchBookingsByFlightNumber(String flightNumber) throws DatabaseException {
        List<Flight> flights = flightDAO.getAllFlights();
        List<Booking> results = new java.util.ArrayList<>();
        
        for (Flight flight : flights) {
            if (flight.getFlightNo().toLowerCase().contains(flightNumber.toLowerCase())) {
                List<Booking> flightBookings = bookingDAO.getBookingsByFlightId(flight.getFlightId());
                results.addAll(flightBookings);
            }
        }
        
        return results;
    }
    
    private List<Booking> searchBookingsByPassengerName(String passengerName) throws DatabaseException {
        List<Booking> allBookings = bookingDAO.getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getPassengerName().toLowerCase().contains(passengerName.toLowerCase()))
                .toList();
    }
    
    private void handleCheckIn() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to check in", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedBooking.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, 
                "Passenger is already checked in", 
                "Already Checked In", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show seat selection dialog
        String seatNo = showSeatSelectionDialog();
        if (seatNo != null) {
            try {
                boolean checkedIn = bookingDAO.checkInPassenger(selectedBooking.getBookingId(), seatNo);
                
                if (checkedIn) {
                    FileLogger.getInstance().logInfo("Checked in passenger: " + selectedBooking.getPassengerName());
                    JOptionPane.showMessageDialog(this, 
                        "Passenger checked in successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadAllBookings();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to check in passenger", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Check-in failed: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Check-in failed: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String showSeatSelectionDialog() {
        // Simple seat selection - in a real app, you'd show available seats
        String seatNo = JOptionPane.showInputDialog(this, 
            "Enter seat number for " + selectedBooking.getPassengerName() + ":", 
            "Seat Assignment", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (seatNo != null && !seatNo.trim().isEmpty()) {
            try {
                // Check if seat is available
                boolean available = bookingDAO.isSeatAvailable(selectedBooking.getFlightId(), seatNo.trim());
                if (!available) {
                    JOptionPane.showMessageDialog(this, 
                        "Seat " + seatNo.trim() + " is not available", 
                        "Seat Unavailable", 
                        JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return seatNo.trim();
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error checking seat availability: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        
        return null;
    }
    
    private void handleGenerateBoardingPass() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to generate boarding pass", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!selectedBooking.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, 
                "Passenger must be checked in to generate boarding pass", 
                "Not Checked In", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            generateBoardingPass(selectedBooking);
        } catch (Exception ex) {
            FileLogger.getInstance().logError("Failed to generate boarding pass: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to generate boarding pass: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateBoardingPass(Booking booking) throws DatabaseException {
        Flight flight = flightDAO.getFlightById(booking.getFlightId());
        if (flight == null) {
            throw new DatabaseException("Flight not found");
        }
        
        // Generate boarding pass content
        StringBuilder boardingPass = new StringBuilder();
        boardingPass.append("==========================================\n");
        boardingPass.append("           BOARDING PASS\n");
        boardingPass.append("==========================================\n");
        boardingPass.append("Passenger: ").append(booking.getPassengerName()).append("\n");
        boardingPass.append("Flight: ").append(flight.getFlightNo()).append("\n");
        boardingPass.append("From: ").append(flight.getOrigin()).append("\n");
        boardingPass.append("To: ").append(flight.getDestination()).append("\n");
        boardingPass.append("Date: ").append(flight.getDepartTime().format(dateFormatter)).append("\n");
        boardingPass.append("Seat: ").append(booking.getSeatNo()).append("\n");
        boardingPass.append("Gate: TBD\n");
        boardingPass.append("==========================================\n");
        
        // Show boarding pass in dialog
        JTextArea textArea = new JTextArea(boardingPass.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Boarding Pass - " + booking.getPassengerName(), 
            JOptionPane.INFORMATION_MESSAGE);
        
        FileLogger.getInstance().logInfo("Generated boarding pass for: " + booking.getPassengerName());
    }
    
    private void handleTableSelection() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedBooking = getBookingFromTableRow(selectedRow);
            checkInButton.setEnabled(!selectedBooking.isCheckedIn());
            generateBoardingPassButton.setEnabled(selectedBooking.isCheckedIn());
        } else {
            selectedBooking = null;
            checkInButton.setEnabled(false);
            generateBoardingPassButton.setEnabled(false);
        }
    }
    
    private Booking getBookingFromTableRow(int row) {
        // This is a simplified approach - in a real app, you'd store the actual Booking objects
        Booking booking = new Booking();
        booking.setBookingId(Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
        booking.setBookingReference(tableModel.getValueAt(row, 1).toString());
        booking.setPassengerName(tableModel.getValueAt(row, 2).toString());
        booking.setSeatNo(tableModel.getValueAt(row, 4).toString());
        booking.setCheckedIn(Boolean.parseBoolean(tableModel.getValueAt(row, 5).toString()));
        return booking;
    }
    
    private void loadAllBookings() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            displayBookings(bookings);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load bookings: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load bookings: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayBookings(List<Booking> bookings) {
        tableModel.setRowCount(0);
        
        for (Booking booking : bookings) {
            try {
                Flight flight = flightDAO.getFlightById(booking.getFlightId());
                String flightNo = flight != null ? flight.getFlightNo() : "N/A";
                
                Object[] row = {
                    booking.getBookingId(),
                    booking.getBookingReference(),
                    booking.getPassengerName(),
                    flightNo,
                    booking.getSeatNo() != null ? booking.getSeatNo() : "Not Assigned",
                    booking.isCheckedIn() ? "Yes" : "No",
                    booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "N/A"
                };
                tableModel.addRow(row);
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Error loading flight for booking: " + ex.getMessage());
            }
        }
        
        FileLogger.getInstance().logInfo("Displayed " + bookings.size() + " bookings");
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Passenger Check-In");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set initial button states
        checkInButton.setEnabled(false);
        generateBoardingPassButton.setEnabled(false);
    }
} 