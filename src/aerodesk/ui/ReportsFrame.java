package aerodesk.ui;

import aerodesk.model.Flight;
import aerodesk.model.Booking;
import aerodesk.model.Baggage;
import aerodesk.model.GateAssignment;
import aerodesk.dao.FlightDAO;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.BaggageDAO;
import aerodesk.dao.GateDAO;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reports & Logs UI for AeroDesk Pro
 * Provides comprehensive system reporting and log management
 */
public class ReportsFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable flightsReportTable;
    private DefaultTableModel flightsReportModel;
    private JTable bookingsReportTable;
    private DefaultTableModel bookingsReportModel;
    private JTable baggageReportTable;
    private DefaultTableModel baggageReportModel;
    private JTable gatesReportTable;
    private DefaultTableModel gatesReportModel;
    private JTextArea logsArea;
    private JTextArea systemStatsArea;
    
    private JButton exportButton;
    private JButton refreshButton;
    private JButton clearLogsButton;
    private JButton saveLogsButton;
    
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;
    private BaggageDAO baggageDAO;
    private GateDAO gateDAO;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ReportsFrame() {
        this.flightDAO = new FlightDAO();
        this.bookingDAO = new BookingDAO();
        this.baggageDAO = new BaggageDAO();
        this.gateDAO = new GateDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadAllReports();
        loadSystemLogs();
        updateSystemStats();
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Flights Report Table
        String[] flightColumns = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Status", "Aircraft"};
        flightsReportModel = new DefaultTableModel(flightColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsReportTable = new JTable(flightsReportModel);
        flightsReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Bookings Report Table
        String[] bookingColumns = {"Booking ID", "Passenger", "Flight No", "Seat", "Checked In", "Check-in Time"};
        bookingsReportModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsReportTable = new JTable(bookingsReportModel);
        bookingsReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Baggage Report Table
        String[] baggageColumns = {"Tag Number", "Passenger", "Weight", "Type", "Status", "Created"};
        baggageReportModel = new DefaultTableModel(baggageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baggageReportTable = new JTable(baggageReportModel);
        baggageReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Gates Report Table
        String[] gateColumns = {"Gate", "Flight No", "Departure", "Arrival", "Status"};
        gatesReportModel = new DefaultTableModel(gateColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gatesReportTable = new JTable(gatesReportModel);
        gatesReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Logs Area
        logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logsArea.setBackground(new Color(248, 248, 248));
        
        // System Stats Area
        systemStatsArea = new JTextArea();
        systemStatsArea.setEditable(false);
        systemStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        systemStatsArea.setBackground(new Color(255, 250, 240));
        
        // Buttons
        exportButton = new JButton("Export Report");
        refreshButton = new JButton("Refresh Data");
        clearLogsButton = new JButton("Clear Logs");
        saveLogsButton = new JButton("Save Logs");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Reports & Logs Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        // Create tabs
        createFlightsTab();
        createBookingsTab();
        createBaggageTab();
        createGatesTab();
        createLogsTab();
        createStatsTab();
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearLogsButton);
        buttonPanel.add(saveLogsButton);
        
        // Add components to frame
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void createFlightsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Flights Report"));
        
        JScrollPane scrollPane = new JScrollPane(flightsReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Flights", panel);
    }
    
    private void createBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bookings Report"));
        
        JScrollPane scrollPane = new JScrollPane(bookingsReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Bookings", panel);
    }
    
    private void createBaggageTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Baggage Report"));
        
        JScrollPane scrollPane = new JScrollPane(baggageReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Baggage", panel);
    }
    
    private void createGatesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gate Assignments Report"));
        
        JScrollPane scrollPane = new JScrollPane(gatesReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Gates", panel);
    }
    
    private void createLogsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Logs"));
        
        JScrollPane scrollPane = new JScrollPane(logsArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Logs", panel);
    }
    
    private void createStatsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Statistics"));
        
        JScrollPane scrollPane = new JScrollPane(systemStatsArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Statistics", panel);
    }
    
    private void setupEventHandlers() {
        exportButton.addActionListener(e -> handleExport());
        refreshButton.addActionListener(e -> handleRefresh());
        clearLogsButton.addActionListener(e -> handleClearLogs());
        saveLogsButton.addActionListener(e -> handleSaveLogs());
    }
    
    private void handleExport() {
        int selectedTab = tabbedPane.getSelectedIndex();
        String reportType = tabbedPane.getTitleAt(selectedTab);
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export " + reportType + " Report");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(reportType.toLowerCase() + "_report_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            exportToCSV(file, selectedTab);
        }
    }
    
    private void exportToCSV(File file, int tabIndex) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            DefaultTableModel model = null;
            
            switch (tabIndex) {
                case 0: // Flights
                    model = flightsReportModel;
                    break;
                case 1: // Bookings
                    model = bookingsReportModel;
                    break;
                case 2: // Baggage
                    model = baggageReportModel;
                    break;
                case 3: // Gates
                    model = gatesReportModel;
                    break;
            }
            
            if (model != null) {
                // Write headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
                
                // Write data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        writer.print(value != null ? value.toString() : "");
                        if (col < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
            }
            
            FileLogger.getInstance().logInfo("Exported report to: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, 
                "Report exported successfully to:\n" + file.getAbsolutePath(), 
                "Export Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            FileLogger.getInstance().logError("Failed to export report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to export report: " + ex.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRefresh() {
        loadAllReports();
        loadSystemLogs();
        updateSystemStats();
        FileLogger.getInstance().logInfo("Reports data refreshed");
        JOptionPane.showMessageDialog(this, 
            "All reports refreshed successfully.", 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleClearLogs() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear the logs display?\nThis will not delete the log file.", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            logsArea.setText("");
            FileLogger.getInstance().logInfo("Logs display cleared by user");
        }
    }
    
    private void handleSaveLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Logs");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("aerodesk_logs_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(logsArea.getText());
                FileLogger.getInstance().logInfo("Logs saved to: " + file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Logs saved successfully to:\n" + file.getAbsolutePath(), 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                FileLogger.getInstance().logError("Failed to save logs: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Failed to save logs: " + ex.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadAllReports() {
        loadFlightsReport();
        loadBookingsReport();
        loadBaggageReport();
        loadGatesReport();
    }
    
    private void loadFlightsReport() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            flightsReportModel.setRowCount(0);
            
            for (Flight flight : flights) {
                Object[] row = {
                    flight.getFlightNo(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartTime() != null ? flight.getDepartTime().format(dateFormatter) : "N/A",
                    flight.getArriveTime() != null ? flight.getArriveTime().format(dateFormatter) : "N/A",
                    flight.getStatus(),
                    flight.getAircraftType()
                };
                flightsReportModel.addRow(row);
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load flights report: " + ex.getMessage());
        }
    }
    
    private void loadBookingsReport() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            bookingsReportModel.setRowCount(0);
            
            for (Booking booking : bookings) {
                try {
                    Flight flight = flightDAO.getFlightById(booking.getFlightId());
                    String flightNo = flight != null ? flight.getFlightNo() : "N/A";
                    
                    Object[] row = {
                        booking.getBookingId(),
                        booking.getPassengerName(),
                        flightNo,
                        booking.getSeatNo() != null ? booking.getSeatNo() : "Not Assigned",
                        booking.isCheckedIn() ? "Yes" : "No",
                        booking.getCheckInTime() != null ? booking.getCheckInTime().format(dateFormatter) : "N/A"
                    };
                    bookingsReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading flight for booking: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load bookings report: " + ex.getMessage());
        }
    }
    
    private void loadBaggageReport() {
        try {
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            baggageReportModel.setRowCount(0);
            
            for (Baggage baggage : baggageList) {
                try {
                    Booking booking = bookingDAO.getBookingById(baggage.getBookingId());
                    String passengerName = booking != null ? booking.getPassengerName() : "Unknown";
                    
                    Object[] row = {
                        baggage.getTagNumber(),
                        passengerName,
                        String.format("%.1f kg", baggage.getWeightKg()),
                        baggage.getBaggageType(),
                        baggage.getStatus(),
                        baggage.getCreatedAt() != null ? baggage.getCreatedAt().format(dateFormatter) : "N/A"
                    };
                    baggageReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading booking for baggage: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load baggage report: " + ex.getMessage());
        }
    }
    
    private void loadGatesReport() {
        try {
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            gatesReportModel.setRowCount(0);
            
            for (GateAssignment assignment : assignments) {
                try {
                    Flight flight = flightDAO.getFlightById(assignment.getFlightId());
                    String flightNo = flight != null ? flight.getFlightNo() : "Unknown";
                    
                    Object[] row = {
                        "Gate " + assignment.getGateId(),
                        flightNo,
                        assignment.getAssignedFrom() != null ? assignment.getAssignedFrom().format(dateFormatter) : "N/A",
                        assignment.getAssignedTo() != null ? assignment.getAssignedTo().format(dateFormatter) : "N/A",
                        "Active"
                    };
                    gatesReportModel.addRow(row);
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error loading flight for gate assignment: " + ex.getMessage());
                }
            }
            
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to load gates report: " + ex.getMessage());
        }
    }
    
    private void loadSystemLogs() {
        try {
            // Read the log file
            File logFile = new File("aerodesk.log");
            if (logFile.exists()) {
                StringBuilder logContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logContent.append(line).append("\n");
                    }
                }
                logsArea.setText(logContent.toString());
                
                // Scroll to bottom to show latest logs
                logsArea.setCaretPosition(logsArea.getDocument().getLength());
            } else {
                logsArea.setText("No log file found.");
            }
        } catch (IOException ex) {
            logsArea.setText("Error reading log file: " + ex.getMessage());
            FileLogger.getInstance().logError("Failed to read log file: " + ex.getMessage());
        }
    }
    
    private void updateSystemStats() {
        StringBuilder stats = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        
        stats.append("AeroDesk Pro System Statistics\n");
        stats.append("==============================\n\n");
        stats.append("Generated: ").append(now.format(dateFormatter)).append("\n\n");
        
        try {
            // Flight statistics
            List<Flight> flights = flightDAO.getAllFlights();
            long scheduledFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.SCHEDULED).count();
            long onTimeFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.ON_TIME).count();
            long delayedFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.DELAYED).count();
            long departedFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.DEPARTED).count();
            long cancelledFlights = flights.stream().filter(f -> f.getStatus() == Flight.FlightStatus.CANCELLED).count();
            
            stats.append("FLIGHT STATISTICS:\n");
            stats.append("-----------------\n");
            stats.append("Total Flights: ").append(flights.size()).append("\n");
            stats.append("Scheduled: ").append(scheduledFlights).append("\n");
            stats.append("On Time: ").append(onTimeFlights).append("\n");
            stats.append("Delayed: ").append(delayedFlights).append("\n");
            stats.append("Departed: ").append(departedFlights).append("\n");
            stats.append("Cancelled: ").append(cancelledFlights).append("\n\n");
            
            // Booking statistics
            List<Booking> bookings = bookingDAO.getAllBookings();
            long checkedInBookings = bookings.stream().filter(Booking::isCheckedIn).count();
            
            stats.append("BOOKING STATISTICS:\n");
            stats.append("-------------------\n");
            stats.append("Total Bookings: ").append(bookings.size()).append("\n");
            stats.append("Checked In: ").append(checkedInBookings).append("\n");
            stats.append("Not Checked In: ").append(bookings.size() - checkedInBookings).append("\n");
            stats.append("Check-in Rate: ").append(String.format("%.1f%%", 
                bookings.size() > 0 ? (double) checkedInBookings / bookings.size() * 100 : 0)).append("\n\n");
            
            // Baggage statistics
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            long deliveredBaggage = baggageList.stream()
                .filter(b -> b.getStatus().equals("DELIVERED")).count();
            
            stats.append("BAGGAGE STATISTICS:\n");
            stats.append("-------------------\n");
            stats.append("Total Baggage: ").append(baggageList.size()).append("\n");
            stats.append("Delivered: ").append(deliveredBaggage).append("\n");
            stats.append("In Transit: ").append(baggageList.size() - deliveredBaggage).append("\n");
            stats.append("Delivery Rate: ").append(String.format("%.1f%%", 
                baggageList.size() > 0 ? (double) deliveredBaggage / baggageList.size() * 100 : 0)).append("\n\n");
            
            // Gate statistics
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            stats.append("GATE STATISTICS:\n");
            stats.append("----------------\n");
            stats.append("Total Assignments: ").append(assignments.size()).append("\n");
            stats.append("Active Gates: ").append(assignments.stream()
                .filter(a -> a.getAssignedTo() != null && a.getAssignedTo().isAfter(now)).count()).append("\n\n");
            
        } catch (DatabaseException ex) {
            stats.append("ERROR: ").append(ex.getMessage()).append("\n");
            FileLogger.getInstance().logError("Failed to generate system stats: " + ex.getMessage());
        }
        
        systemStatsArea.setText(stats.toString());
    }
    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Reports & Logs");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
    }
} 