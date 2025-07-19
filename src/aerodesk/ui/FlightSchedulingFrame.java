package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.util.IconManager;
import aerodesk.dao.FlightDAO;
import aerodesk.model.Flight;
import aerodesk.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Flight Scheduling UI for AeroDesk Pro
 * Handles flight creation, editing, deletion, and viewing with advanced features
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
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> statusFilterComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    
    // Action buttons
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton importButton;
    
    // Status and progress
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    private FlightDAO flightDAO;
    private Flight selectedFlight;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Validation patterns
    private static final Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}\\d{3,4}$");
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    
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
        
        // Search components
        searchField = new JTextField(20);
        searchTypeComboBox = new JComboBox<>(new String[]{"Flight No", "Origin", "Destination", "Aircraft Type"});
        statusFilterComboBox = new JComboBox<>(new String[]{"All Statuses", "SCHEDULED", "ON_TIME", "DELAYED", "DEPARTED", "CANCELLED"});
        searchButton = new JButton("🔍 Search");
        clearSearchButton = new JButton("🧹 Clear Search");
        
        // Action buttons
        createButton = new JButton("✈️ Create Flight");
        updateButton = new JButton("🔄 Update Flight");
        deleteButton = new JButton("🗑️ Delete Flight");
        clearButton = new JButton("🧹 Clear Form");
        refreshButton = new JButton("🔄 Refresh");
        exportButton = new JButton("📤 Export");
        importButton = new JButton("📥 Import");
        
        // Status components
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        
        // Apply modern styling
        applyStyling();
        
        // Table setup
        setupTable();
    }
    
    private void applyStyling() {
        // Enhanced button styling
        styleEnhancedButton(createButton, "Create Flight", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(updateButton, "Update Flight", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(deleteButton, "Delete Flight", ThemeManager.ERROR_RED, ThemeManager.WHITE);
        styleEnhancedButton(clearButton, "Clear Form", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        styleEnhancedButton(refreshButton, "Refresh", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(exportButton, "Export CSV", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(importButton, "Import CSV", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        
        // Search button styling
        styleEnhancedButton(searchButton, "Search", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(clearSearchButton, "Clear", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        
        // Enhanced field styling
        styleEnhancedTextField(flightNoField, "Enter flight number (e.g., AA101)");
        styleEnhancedTextField(originField, "Enter origin airport code (e.g., JFK)");
        styleEnhancedTextField(destinationField, "Enter destination airport code (e.g., LAX)");
        styleEnhancedTextField(departTimeField, "Enter departure time (yyyy-MM-dd HH:mm)");
        styleEnhancedTextField(arriveTimeField, "Enter arrival time (yyyy-MM-dd HH:mm)");
        styleEnhancedTextField(aircraftTypeField, "Enter aircraft type (e.g., Boeing 737)");
        styleEnhancedTextField(searchField, "Enter search term...");
        
        // Enhanced combo box styling
        styleEnhancedComboBox(statusComboBox);
        styleEnhancedComboBox(searchTypeComboBox);
        styleEnhancedComboBox(statusFilterComboBox);
        
        // Status label styling
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
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
        String[] columnNames = {"Flight No", "Origin", "Destination", "Departure", "Arrival", "Aircraft", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        flightsTable.setRowSorter(tableSorter);
        ThemeManager.styleTable(flightsTable);
        
        // Set column widths
        flightsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Flight No
        flightsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Origin
        flightsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Destination
        flightsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Departure
        flightsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Arrival
        flightsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Aircraft
        flightsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Header panel with enhanced gradient
        JPanel headerPanel = createEnhancedHeader();
        
        // Main content panel with better organization
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBackground(ThemeManager.LIGHT_GRAY);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel with improved design
        JPanel searchPanel = createEnhancedSearchPanel();
        
        // Form and table split panel
        JSplitPane splitPane = createSplitPane();
        
        // Status and button panel
        JPanel bottomPanel = createEnhancedBottomPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        mainContentPanel.add(searchPanel, BorderLayout.NORTH);
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        // Create gradient background
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Add subtle pattern overlay
        headerPanel.setLayout(new BorderLayout());
        
        // Main title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Flight Scheduling Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.WHITE);
        
        titlePanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage flight schedules, routes, and aircraft assignments");
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
        JLabel searchTitle = new JLabel("Search & Filter Flights");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        
        controlsPanel.add(searchFieldPanel);
        controlsPanel.add(typePanel);
        controlsPanel.add(statusPanel);
        controlsPanel.add(buttonsPanel);
        
        searchPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private JSplitPane createSplitPane() {
        // Enhanced form panel
        JPanel formPanel = createEnhancedFormPanel();
        
        // Enhanced table panel
        JPanel tablePanel = createEnhancedTablePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        
        return splitPane;
    }
    
    private JPanel createEnhancedFormPanel() {
        JPanel formPanel = ThemeManager.createCardPanel();
        formPanel.setLayout(new BorderLayout(0, 0));
        formPanel.setPreferredSize(new Dimension(500, 600));
        
        // Form header
        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setBackground(ThemeManager.PRIMARY_BLUE);
        formHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel formTitle = new JLabel("Flight Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(ThemeManager.WHITE);
        formHeader.add(formTitle, BorderLayout.CENTER);
        
        formPanel.add(formHeader, BorderLayout.NORTH);
        
        // Form content with scroll pane
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(ThemeManager.WHITE);
        formContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add form fields with enhanced styling
        formContent.add(createEnhancedFormField("Flight Number", flightNoField, "e.g., AA101, UA202"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Origin Airport", originField, "e.g., JFK, LAX"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Destination Airport", destinationField, "e.g., LAX, ORD"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Departure Time", departTimeField, "yyyy-MM-dd HH:mm"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Arrival Time", arriveTimeField, "yyyy-MM-dd HH:mm"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Aircraft Type", aircraftTypeField, "e.g., Boeing 737, Airbus A320"));
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(createEnhancedFormField("Status", statusComboBox, ""));
        
        // Add some extra space at the bottom
        formContent.add(Box.createVerticalStrut(20));
        
        // Wrap in scroll pane to handle overflow
        JScrollPane formScrollPane = new JScrollPane(formContent);
        formScrollPane.setBorder(null);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
        formPanel.add(formScrollPane, BorderLayout.CENTER);
        
        return formPanel;
    }
    
    private JPanel createEnhancedFormField(String labelText, JComponent component, String placeholder) {
        JPanel fieldPanel = new JPanel(new BorderLayout(8, 5));
        fieldPanel.setBackground(ThemeManager.WHITE);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fieldPanel.setPreferredSize(new Dimension(0, 70));
        
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeManager.DARK_GRAY);
        fieldPanel.add(label, BorderLayout.NORTH);
        
        // Component with enhanced styling
        if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setPreferredSize(new Dimension(0, 32));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            
            // Add placeholder text if available
            if (!placeholder.isEmpty()) {
                textField.setToolTipText(placeholder);
            }
        } else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setPreferredSize(new Dimension(0, 32));
            comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        
        fieldPanel.add(component, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    private JPanel createEnhancedTablePanel() {
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout(0, 0));
        
        // Table header
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(ThemeManager.SECONDARY_BLUE);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel tableTitle = new JLabel("Flight Schedule");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(ThemeManager.WHITE);
        tableHeader.add(tableTitle, BorderLayout.CENTER);
        
        // Row count label
        JLabel rowCountLabel = new JLabel("0 flights");
        rowCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rowCountLabel.setForeground(new Color(200, 200, 200));
        tableHeader.add(rowCountLabel, BorderLayout.EAST);
        
        tablePanel.add(tableHeader, BorderLayout.NORTH);
        
        // Enhanced table
        flightsTable.setRowHeight(35);
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightsTable.setGridColor(ThemeManager.LIGHT_GRAY);
        flightsTable.setShowGrid(true);
        flightsTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced table header
        flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        flightsTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        flightsTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        flightsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
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
        
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        primaryActions.add(createButton);
        primaryActions.add(updateButton);
        primaryActions.add(deleteButton);
        
        // Secondary actions group
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        secondaryActions.setOpaque(false);
        secondaryActions.add(clearButton);
        secondaryActions.add(refreshButton);
        
        // Data actions group
        JPanel dataActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        dataActions.setOpaque(false);
        dataActions.add(exportButton);
        dataActions.add(importButton);
        
        // Add separators
        JSeparator separator1 = new JSeparator(JSeparator.VERTICAL);
        separator1.setPreferredSize(new Dimension(1, 30));
        separator1.setBackground(ThemeManager.LIGHT_GRAY);
        
        JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);
        separator2.setPreferredSize(new Dimension(1, 30));
        separator2.setBackground(ThemeManager.LIGHT_GRAY);
        
        buttonPanel.add(primaryActions);
        buttonPanel.add(separator1);
        buttonPanel.add(secondaryActions);
        buttonPanel.add(separator2);
        buttonPanel.add(dataActions);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        // Action buttons
        createButton.addActionListener(e -> createFlight());
        updateButton.addActionListener(e -> updateFlight());
        deleteButton.addActionListener(e -> deleteFlight());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> loadFlights());
        exportButton.addActionListener(e -> exportFlights());
        importButton.addActionListener(e -> importFlights());
        
        // Search buttons
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        
        // Search field enter key
        searchField.addActionListener(e -> performSearch());
        
        // Status filter change
        statusFilterComboBox.addActionListener(e -> applyStatusFilter());
        
        // Table selection listener with enhanced highlighting
        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = flightsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadFlightToForm(selectedRow);
                    highlightSelectedRow(selectedRow);
                }
            }
        });
        
        // Real-time search with debouncing
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private javax.swing.Timer timer = new javax.swing.Timer(300, e -> performSearch());
            
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                timer.restart();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                timer.restart();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                timer.restart();
            }
        });
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Double-click to edit
        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = flightsTable.getSelectedRow();
                    if (row >= 0) {
                        loadFlightToForm(row);
                        updateButton.requestFocus();
                    }
                }
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        // Register keyboard shortcuts
        KeyStroke ctrlN = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlD = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlR = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlE = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlI = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        KeyStroke escape = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        
        getRootPane().registerKeyboardAction(e -> createFlight(), "Create", ctrlN, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> updateFlight(), "Update", ctrlS, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> deleteFlight(), "Delete", ctrlD, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> loadFlights(), "Refresh", ctrlR, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> exportFlights(), "Export", ctrlE, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> importFlights(), "Import", ctrlI, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> clearForm(), "Clear", escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void highlightSelectedRow(int row) {
        // Update status to show selected flight info
        if (row >= 0 && row < tableModel.getRowCount()) {
            String flightNo = (String) tableModel.getValueAt(row, 0);
            String origin = (String) tableModel.getValueAt(row, 1);
            String destination = (String) tableModel.getValueAt(row, 2);
            updateStatus("Selected: " + flightNo + " (" + origin + " → " + destination + ")", ThemeManager.PRIMARY_BLUE);
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
    

    
    private void configureWindow() {
        setTitle("AeroDesk Pro - Flight Scheduling Management");
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
        createButton.setToolTipText("Create a new flight (Ctrl+N)");
        updateButton.setToolTipText("Update selected flight (Ctrl+S)");
        deleteButton.setToolTipText("Delete selected flight (Ctrl+D)");
        clearButton.setToolTipText("Clear form fields (Esc)");
        refreshButton.setToolTipText("Refresh flight list (Ctrl+R)");
        exportButton.setToolTipText("Export flights to CSV (Ctrl+E)");
        importButton.setToolTipText("Import flights from CSV (Ctrl+I)");
        
        // Search tooltips
        searchButton.setToolTipText("Search flights");
        clearSearchButton.setToolTipText("Clear search filters");
        
        // Table tooltip
        flightsTable.setToolTipText("Double-click a row to edit the flight");
        
        // Form field tooltips
        flightNoField.setToolTipText("Enter flight number in format: AA101, UA202, etc.");
        originField.setToolTipText("Enter 3-letter airport code: JFK, LAX, ORD, etc.");
        destinationField.setToolTipText("Enter 3-letter airport code: JFK, LAX, ORD, etc.");
        departTimeField.setToolTipText("Enter departure time in format: 2024-01-15 14:30");
        arriveTimeField.setToolTipText("Enter arrival time in format: 2024-01-15 17:45");
        aircraftTypeField.setToolTipText("Enter aircraft type: Boeing 737, Airbus A320, etc.");
    }
    
    // ========== SEARCH AND FILTER METHODS ==========
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        
        if (searchTerm.isEmpty()) {
            tableSorter.setRowFilter(null);
            updateStatus("Search cleared", ThemeManager.SUCCESS_GREEN);
            return;
        }
        
        try {
            int columnIndex = getColumnIndexForSearchType(searchType);
            if (columnIndex >= 0) {
                RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + searchTerm, columnIndex);
                tableSorter.setRowFilter(filter);
                updateStatus("Search results: " + flightsTable.getRowCount() + " flights found", ThemeManager.SUCCESS_GREEN);
            }
        } catch (Exception ex) {
            FileLogger.getInstance().logError("Search error: " + ex.getMessage());
            updateStatus("Search error", ThemeManager.ERROR_RED);
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        tableSorter.setRowFilter(null);
        updateStatus("Search cleared", ThemeManager.SUCCESS_GREEN);
    }
    
    private void applyStatusFilter() {
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        
        if ("All Statuses".equals(selectedStatus)) {
            tableSorter.setRowFilter(null);
            updateStatus("Status filter cleared", ThemeManager.SUCCESS_GREEN);
            return;
        }
        
        try {
            RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(selectedStatus, 6); // Status column
            tableSorter.setRowFilter(filter);
            updateStatus("Filtered by status: " + selectedStatus, ThemeManager.SUCCESS_GREEN);
        } catch (Exception ex) {
            FileLogger.getInstance().logError("Status filter error: " + ex.getMessage());
            updateStatus("Filter error", ThemeManager.ERROR_RED);
        }
    }
    
    private int getColumnIndexForSearchType(String searchType) {
        switch (searchType) {
            case "Flight No": return 0;
            case "Origin": return 1;
            case "Destination": return 2;
            case "Aircraft Type": return 5;
            default: return -1;
        }
    }
    
    // ========== EXPORT/IMPORT METHODS ==========
    
    private void exportFlights() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Flights");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                showProgress("Exporting flights...");
                exportToCSV(fileChooser.getSelectedFile());
                hideProgress();
                updateStatus("Flights exported successfully", ThemeManager.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this, "Flights exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                hideProgress();
                FileLogger.getInstance().logError("Export error: " + ex.getMessage());
                updateStatus("Export failed", ThemeManager.ERROR_RED);
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importFlights() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Flights");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                showProgress("Importing flights...");
                importFromCSV(fileChooser.getSelectedFile());
                hideProgress();
                loadFlights();
                updateStatus("Flights imported successfully", ThemeManager.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this, "Flights imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                hideProgress();
                FileLogger.getInstance().logError("Import error: " + ex.getMessage());
                updateStatus("Import failed", ThemeManager.ERROR_RED);
                JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportToCSV(java.io.File file) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Write header
            writer.println("Flight No,Origin,Destination,Departure,Arrival,Aircraft,Status");
            
            // Write data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    if (j > 0) line.append(",");
                    line.append("\"").append(tableModel.getValueAt(i, j)).append("\"");
                }
                writer.println(line.toString());
            }
        }
    }
    
    private void importFromCSV(java.io.File file) throws Exception {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] values = parseCSVLine(line);
                if (values.length >= 7) {
                    try {
                        Flight flight = new Flight(
                            values[0], // flightNo
                            values[1], // origin
                            values[2], // destination
                            LocalDateTime.parse(values[3], dateFormatter), // departTime
                            LocalDateTime.parse(values[4], dateFormatter), // arriveTime
                            values[5]  // aircraftType
                        );
                        flight.setStatus(Flight.FlightStatus.valueOf(values[6]));
                        flightDAO.createFlight(flight);
                    } catch (Exception ex) {
                        FileLogger.getInstance().logWarning("Skipping invalid flight data: " + line);
                    }
                }
            }
        }
    }
    
    private String[] parseCSVLine(String line) {
        java.util.List<String> result = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        
        return result.toArray(new String[0]);
    }
    
    // ========== VALIDATION METHODS ==========
    
    private boolean validateFlightNumber(String flightNo) {
        if (!FLIGHT_NUMBER_PATTERN.matcher(flightNo).matches()) {
            JOptionPane.showMessageDialog(this, 
                "Invalid flight number format. Use format: AA101, UA202, etc.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean validateAirportCode(String airportCode) {
        if (!AIRPORT_CODE_PATTERN.matcher(airportCode).matches()) {
            JOptionPane.showMessageDialog(this, 
                "Invalid airport code. Use 3-letter format: JFK, LAX, etc.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean validateDateTime(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, dateFormatter);
            return true;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid date/time format. Use: yyyy-MM-dd HH:mm", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ========== UTILITY METHODS ==========
    
    private void showProgress(String message) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        updateStatus(message, ThemeManager.PRIMARY_BLUE);
    }
    
    private void hideProgress() {
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
    }
    
    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        FileLogger.getInstance().logInfo("Flight Scheduling: " + message);
    }
    
    // ========== ENHANCED CRUD METHODS ==========
    
    private void createFlight() {
        try {
            Flight flight = getFlightFromForm();
            if (flight != null) {
                showProgress("Creating flight...");
                
                // Check for flight conflicts
                if (checkFlightConflicts(flight)) {
                    hideProgress();
                    return;
                }
                
                flightDAO.createFlight(flight);
                hideProgress();
                FileLogger.getInstance().logInfo("Flight created: " + flight.getFlightNo());
                updateStatus("Flight created successfully", ThemeManager.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this, "Flight created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            }
        } catch (DatabaseException ex) {
            hideProgress();
            FileLogger.getInstance().logError("Error creating flight: " + ex.getMessage());
            updateStatus("Create failed", ThemeManager.ERROR_RED);
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
                showProgress("Updating flight...");
                
                flight.setFlightId(selectedFlight.getFlightId());
                flightDAO.updateFlight(flight);
                hideProgress();
                FileLogger.getInstance().logInfo("Flight updated: " + flight.getFlightNo());
                updateStatus("Flight updated successfully", ThemeManager.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this, "Flight updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            }
        } catch (DatabaseException ex) {
            hideProgress();
            FileLogger.getInstance().logError("Error updating flight: " + ex.getMessage());
            updateStatus("Update failed", ThemeManager.ERROR_RED);
            JOptionPane.showMessageDialog(this, "Error updating flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteFlight() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete flight " + selectedFlight.getFlightNo() + "?\nThis action cannot be undone.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                showProgress("Deleting flight...");
                flightDAO.deleteFlight(selectedFlight.getFlightId());
                hideProgress();
                FileLogger.getInstance().logInfo("Flight deleted: " + selectedFlight.getFlightNo());
                updateStatus("Flight deleted successfully", ThemeManager.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this, "Flight deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
                clearForm();
            } catch (DatabaseException ex) {
                hideProgress();
                FileLogger.getInstance().logError("Error deleting flight: " + ex.getMessage());
                updateStatus("Delete failed", ThemeManager.ERROR_RED);
                JOptionPane.showMessageDialog(this, "Error deleting flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean checkFlightConflicts(Flight newFlight) {
        try {
            List<Flight> existingFlights = flightDAO.getAllFlights();
            
            for (Flight existing : existingFlights) {
                // Check for same flight number
                if (existing.getFlightNo().equals(newFlight.getFlightNo())) {
                    JOptionPane.showMessageDialog(this, 
                        "Flight number " + newFlight.getFlightNo() + " already exists.", 
                        "Flight Conflict", 
                        JOptionPane.WARNING_MESSAGE);
                    return true;
                }
                
                // Check for overlapping times (simplified check)
                if (existing.getOrigin().equals(newFlight.getOrigin()) && 
                    existing.getDestination().equals(newFlight.getDestination()) &&
                    existing.getDepartTime().equals(newFlight.getDepartTime())) {
                    JOptionPane.showMessageDialog(this, 
                        "A flight with the same route and departure time already exists.", 
                        "Flight Conflict", 
                        JOptionPane.WARNING_MESSAGE);
                    return true;
                }
            }
            return false;
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Error checking flight conflicts: " + ex.getMessage());
            return false;
        }
    }
    
    private Flight getFlightFromForm() {
        String flightNo = flightNoField.getText().trim();
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String departTimeStr = departTimeField.getText().trim();
        String arriveTimeStr = arriveTimeField.getText().trim();
        String aircraftType = aircraftTypeField.getText().trim();
        Flight.FlightStatus status = (Flight.FlightStatus) statusComboBox.getSelectedItem();
        
        // Enhanced validation
        if (flightNo.isEmpty() || origin.isEmpty() || destination.isEmpty() || 
            departTimeStr.isEmpty() || arriveTimeStr.isEmpty() || aircraftType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        if (!validateFlightNumber(flightNo)) return null;
        if (!validateAirportCode(origin)) return null;
        if (!validateAirportCode(destination)) return null;
        if (!validateDateTime(departTimeStr)) return null;
        if (!validateDateTime(arriveTimeStr)) return null;
        
        try {
            LocalDateTime departTime = LocalDateTime.parse(departTimeStr, dateFormatter);
            LocalDateTime arriveTime = LocalDateTime.parse(arriveTimeStr, dateFormatter);
            
            if (arriveTime.isBefore(departTime)) {
                JOptionPane.showMessageDialog(this, "Arrival time must be after departure time", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            if (departTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Departure time cannot be in the past", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void loadFlights() {
        try {
            showProgress("Loading flights...");
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
            
            hideProgress();
            FileLogger.getInstance().logInfo("Loaded " + flights.size() + " flights");
            updateStatus("Loaded " + flights.size() + " flights", ThemeManager.SUCCESS_GREEN);
        } catch (DatabaseException ex) {
            hideProgress();
            FileLogger.getInstance().logError("Error loading flights: " + ex.getMessage());
            updateStatus("Load failed", ThemeManager.ERROR_RED);
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 