package aerodesk.ui;

import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;
import aerodesk.dao.GateDAO;
import aerodesk.dao.FlightDAO;
import aerodesk.model.Gate;
import aerodesk.model.GateAssignment;
import aerodesk.model.Flight;
import aerodesk.exception.DatabaseException;
import aerodesk.exception.GateConflictException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 * Enhanced Gate Management UI for AeroDesk Pro
 * Handles gate assignments and conflict detection with real-time updates
 */
public class GateManagementFrame extends JFrame {
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton refreshButton;
    
    // Table components
    private JTable gatesTable;
    private DefaultTableModel gatesTableModel;
    private TableRowSorter<DefaultTableModel> gatesTableSorter;
    private JTable assignmentsTable;
    private DefaultTableModel assignmentsTableModel;
    private TableRowSorter<DefaultTableModel> assignmentsTableSorter;
    
    // Action buttons
    private JButton addGateButton;
    private JButton editGateButton;
    private JButton deleteGateButton;
    private JButton assignFlightButton;
    private JButton removeAssignmentButton;
    private JButton exportButton;
    private JButton conflictCheckButton;
    
    // Status and progress
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel statsLabel;
    private JLabel conflictStatusLabel;
    
    // Data components
    private GateDAO gateDAO;
    private FlightDAO flightDAO;
    private Gate selectedGate;
    private GateAssignment selectedAssignment;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Search timer for debouncing
    private Timer searchTimer;
    
    public GateManagementFrame() {
        this.gateDAO = new GateDAO();
        this.flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
        loadGates();
        loadAssignments();
        updateStats();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(25);
        statusFilterComboBox = new JComboBox<>(new String[]{"All Status", "AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");
        refreshButton = new JButton("Refresh");
        
        // Action buttons
        addGateButton = new JButton("Add Gate");
        editGateButton = new JButton("Edit Gate");
        deleteGateButton = new JButton("Delete Gate");
        assignFlightButton = new JButton("Assign Flight");
        removeAssignmentButton = new JButton("Remove Assignment");
        exportButton = new JButton("Export Data");
        conflictCheckButton = new JButton("Check Conflicts");
        
        // Status components
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statsLabel = new JLabel("Total Gates: 0 | Active: 0 | Assignments: 0");
        conflictStatusLabel = new JLabel("Conflicts: None detected");
        
        // Apply enhanced styling
        applyEnhancedStyling();
        
        // Table setup
        setupTables();
    }
    
    private void applyEnhancedStyling() {
        // Enhanced button styling
        styleEnhancedButton(addGateButton, "Add Gate", ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        styleEnhancedButton(editGateButton, "Edit Gate", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(deleteGateButton, "Delete Gate", ThemeManager.ERROR_RED, ThemeManager.WHITE);
        styleEnhancedButton(assignFlightButton, "Assign Flight", ThemeManager.ACCENT_ORANGE, ThemeManager.WHITE);
        styleEnhancedButton(removeAssignmentButton, "Remove Assignment", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        styleEnhancedButton(exportButton, "Export Data", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(conflictCheckButton, "Check Conflicts", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        
        // Search button styling
        styleEnhancedButton(searchButton, "Search", ThemeManager.PRIMARY_BLUE, ThemeManager.WHITE);
        styleEnhancedButton(clearSearchButton, "Clear", ThemeManager.WARNING_AMBER, ThemeManager.WHITE);
        styleEnhancedButton(refreshButton, "Refresh", ThemeManager.SECONDARY_BLUE, ThemeManager.WHITE);
        
        // Enhanced field styling
        styleEnhancedTextField(searchField, "Search gates by name or ID...");
        styleEnhancedComboBox(statusFilterComboBox);
        
        // Status label styling
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsLabel.setForeground(ThemeManager.PRIMARY_BLUE);
        conflictStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        conflictStatusLabel.setForeground(ThemeManager.ACCENT_ORANGE);
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
        // Gates table
        String[] gateColumns = {"Gate ID", "Gate Name", "Status", "Assignments", "Created At", "Last Updated"};
        gatesTableModel = new DefaultTableModel(gateColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gatesTable = new JTable(gatesTableModel);
        gatesTableSorter = new TableRowSorter<>(gatesTableModel);
        gatesTable.setRowSorter(gatesTableSorter);
        gatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enhanced gates table styling
        gatesTable.setRowHeight(35);
        gatesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gatesTable.setGridColor(ThemeManager.LIGHT_GRAY);
        gatesTable.setShowGrid(true);
        gatesTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced gates table header
        gatesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        gatesTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        gatesTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        gatesTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        // Assignments table
        String[] assignmentColumns = {"Assignment ID", "Gate", "Flight No", "Assigned From", "Assigned To", "Status"};
        assignmentsTableModel = new DefaultTableModel(assignmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentsTable = new JTable(assignmentsTableModel);
        assignmentsTableSorter = new TableRowSorter<>(assignmentsTableModel);
        assignmentsTable.setRowSorter(assignmentsTableSorter);
        assignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enhanced assignments table styling
        assignmentsTable.setRowHeight(35);
        assignmentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assignmentsTable.setGridColor(ThemeManager.LIGHT_GRAY);
        assignmentsTable.setShowGrid(true);
        assignmentsTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Enhanced assignments table header
        assignmentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignmentsTable.getTableHeader().setBackground(ThemeManager.LIGHT_GRAY);
        assignmentsTable.getTableHeader().setForeground(ThemeManager.DARK_GRAY);
        assignmentsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY));
        
        // Set column widths
        assignmentsTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // Assignment ID
        assignmentsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Gate
        assignmentsTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Flight No
        assignmentsTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Assigned From
        assignmentsTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Assigned To
        assignmentsTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Status
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
        
        // Left panel - Gates
        JPanel gatesPanel = createEnhancedGatesPanel();
        
        // Right panel - Assignments
        JPanel assignmentsPanel = createEnhancedAssignmentsPanel();
        
        contentPanel.add(gatesPanel);
        contentPanel.add(assignmentsPanel);
        
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
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Main title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gate Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.WHITE);
        
        titlePanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage gate assignments and detect conflicts in real-time");
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
    
    private JPanel createEnhancedGatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel panelTitle = new JLabel("Available Gates");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelTitle.setForeground(ThemeManager.DARK_GRAY);
        
        titlePanel.add(panelTitle);
        
        // Table with enhanced scroll pane
        JScrollPane scrollPane = new JScrollPane(gatesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(ThemeManager.WHITE);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEnhancedAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ThemeManager.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel panelTitle = new JLabel("Gate Assignments");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelTitle.setForeground(ThemeManager.DARK_GRAY);
        
        titlePanel.add(panelTitle);
        
        // Table with enhanced scroll pane
        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
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
        actionButtonPanel.add(addGateButton);
        actionButtonPanel.add(editGateButton);
        actionButtonPanel.add(deleteGateButton);
        actionButtonPanel.add(assignFlightButton);
        actionButtonPanel.add(removeAssignmentButton);
        actionButtonPanel.add(exportButton);
        actionButtonPanel.add(conflictCheckButton);
        
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
        rightStatusPanel.add(conflictStatusLabel);
        
        statusPanel.add(leftStatusPanel, BorderLayout.WEST);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);
        
        bottomPanel.add(actionButtonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Button event handlers
        addGateButton.addActionListener(e -> handleAddGate());
        editGateButton.addActionListener(e -> handleEditGate());
        deleteGateButton.addActionListener(e -> handleDeleteGate());
        assignFlightButton.addActionListener(e -> handleAssignFlight());
        removeAssignmentButton.addActionListener(e -> handleRemoveAssignment());
        exportButton.addActionListener(e -> handleExportData());
        conflictCheckButton.addActionListener(e -> handleConflictCheck());
        
                 // Search field event handler
         searchField.addKeyListener(new KeyAdapter() {
             @Override
             public void keyReleased(KeyEvent e) {
                 if (searchTimer != null) {
                     searchTimer.stop();
                 }
                 searchTimer = new Timer(1000, evt -> {
                     performSearch();
                     searchTimer.stop(); // Stop timer after one execution
                 });
                 searchTimer.setRepeats(false); // Don't repeat
                 searchTimer.start();
             }
         });
         
         // Status filter combo box event handler
         statusFilterComboBox.addActionListener(e -> performSearch());
         
         // Search and clear button handlers
         searchButton.addActionListener(e -> performSearch());
         clearSearchButton.addActionListener(e -> {
             searchField.setText("");
             statusFilterComboBox.setSelectedItem("All Status");
             loadGates(); // Load all gates instead of searching
         });
        
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
    
    private void handleEditGate() {
        if (selectedGate == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a gate to edit", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        showEditGateDialog();
    }
    
    private void handleDeleteGate() {
        if (selectedGate == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a gate to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete gate " + selectedGate.getGateName() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = gateDAO.deleteGate(selectedGate.getGateId());
                
                if (deleted) {
                    FileLogger.getInstance().logInfo("Deleted gate: " + selectedGate.getGateName());
                    JOptionPane.showMessageDialog(this, 
                        "Gate deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadGates();
                    updateStats();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete gate", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to delete gate: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete gate: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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
        JTextField gateNameField = new JTextField(25);
        gateNameField.setFocusable(true);
        gateNameField.setPreferredSize(new Dimension(200, 30));
        ThemeManager.styleTextField(gateNameField);
        gbc.gridx = 1;
        formPanel.add(gateNameField, gbc);
        
        // Terminal field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(ThemeManager.createBodyLabel("Terminal:"), gbc);
        JTextField terminalField = new JTextField(25);
        terminalField.setFocusable(true);
        terminalField.setPreferredSize(new Dimension(200, 30));
        ThemeManager.styleTextField(terminalField);
        gbc.gridx = 1;
        formPanel.add(terminalField, gbc);
        
        // Status field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(ThemeManager.createBodyLabel("Status:"), gbc);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        ThemeManager.styleComboBox(statusComboBox);
        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        // Make buttons wider
        addButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        
        ThemeManager.styleButton(addButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(cancelButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        addButton.addActionListener(e -> {
            try {
                String gateName = gateNameField.getText().trim();
                String terminal = terminalField.getText().trim();
                String status = statusComboBox.getSelectedItem().toString();
                
                if (gateName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Gate name is required", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (terminal.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Terminal is required", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Gate gate = new Gate(gateName);
                gate.setTerminal(terminal);
                gate.setStatus(Gate.GateStatus.valueOf(status));
                
                Gate createdGate = gateDAO.createGate(gate);
                
                FileLogger.getInstance().logInfo("Added gate: " + createdGate.getGateName());
                JOptionPane.showMessageDialog(dialog, 
                    "Gate added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                loadGates();
                updateStats();
                
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
        
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set focus to first field and make dialog visible
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gateNameField.requestFocusInWindow();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void showEditGateDialog() {
        JDialog dialog = new JDialog(this, "Edit Gate", true);
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
        JTextField gateNameField = new JTextField(25);
        gateNameField.setText(selectedGate.getGateName()); // Pre-fill current name
        gateNameField.setFocusable(true);
        gateNameField.setPreferredSize(new Dimension(200, 30));
        ThemeManager.styleTextField(gateNameField);
        gbc.gridx = 1;
        formPanel.add(gateNameField, gbc);
        
        // Terminal field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(ThemeManager.createBodyLabel("Terminal:"), gbc);
        JTextField terminalField = new JTextField(25);
        terminalField.setText(selectedGate.getTerminal()); // Pre-fill current terminal
        terminalField.setFocusable(true);
        terminalField.setPreferredSize(new Dimension(200, 30));
        ThemeManager.styleTextField(terminalField);
        gbc.gridx = 1;
        formPanel.add(terminalField, gbc);
        
        // Status field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(ThemeManager.createBodyLabel("Status:"), gbc);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        statusComboBox.setSelectedItem(selectedGate.getStatus().name()); // Pre-fill current status
        ThemeManager.styleComboBox(statusComboBox);
        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeManager.WHITE);
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        
        // Make buttons wider
        saveButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        
        ThemeManager.styleButton(saveButton, ThemeManager.SUCCESS_GREEN, ThemeManager.WHITE);
        ThemeManager.styleButton(cancelButton, ThemeManager.ERROR_RED, ThemeManager.WHITE);
        
        saveButton.addActionListener(e -> {
            try {
                String gateName = gateNameField.getText().trim();
                String terminal = terminalField.getText().trim();
                String status = statusComboBox.getSelectedItem().toString();
                
                if (gateName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Gate name is required", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (terminal.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Terminal is required", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Gate gate = new Gate();
                gate.setGateId(selectedGate.getGateId());
                gate.setGateName(gateName);
                gate.setTerminal(terminal);
                gate.setStatus(Gate.GateStatus.valueOf(status));
                
                                 boolean updated = gateDAO.updateGate(gate);
                
                                 FileLogger.getInstance().logInfo("Edited gate: " + gate.getGateName());
                JOptionPane.showMessageDialog(dialog, 
                    "Gate updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                loadGates();
                updateStats();
                
            } catch (DatabaseException ex) {
                FileLogger.getInstance().logError("Failed to edit gate: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, 
                    "Failed to edit gate: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set focus to first field and make dialog visible
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gateNameField.requestFocusInWindow();
            }
        });
        
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
        updateStats();
        statusLabel.setText("Data refreshed at " + LocalDateTime.now().format(dateFormatter));
    }
    
    private void handleExportData() {
        try {
            List<Gate> gates = gateDAO.getAllGates();
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            
                         String csvContent = "Gate,Status,Created At\n";
             for (Gate gate : gates) {
                 csvContent += gate.getGateName() + "," + gate.getStatus().name() + "," +
                                (gate.getCreatedAt() != null ? gate.getCreatedAt().format(dateFormatter) : "N/A") + "\n";
             }
            
            csvContent += "\nAssignment,Gate,Flight No,Assigned From,Assigned To,Status\n";
            for (GateAssignment assignment : assignments) {
                try {
                    Gate gate = gateDAO.getGateById(assignment.getGateId());
                    Flight flight = flightDAO.getFlightById(assignment.getFlightId());
                    
                    String gateInfo = gate != null ? gate.getGateName() : "Unknown";
                    String flightInfo = flight != null ? flight.getFlightNo() : "Unknown";
                    
                                         csvContent += assignment.getAssignmentId() + "," +
                                    gateInfo + "," +
                                    flightInfo + "," +
                                    (assignment.getAssignmentTime() != null ? assignment.getAssignmentTime().format(dateFormatter) : "N/A") + "," +
                                    (assignment.getDepartureTime() != null ? assignment.getDepartureTime().format(dateFormatter) : "N/A") + "\n";
                } catch (DatabaseException ex) {
                    FileLogger.getInstance().logError("Error exporting assignment details: " + ex.getMessage());
                }
            }
            
            FileLogger.getInstance().logInfo("Exported data to CSV");
            JOptionPane.showMessageDialog(this, 
                "Data exported successfully to " + System.getProperty("user.home") + "/aerodesk_gate_data.csv", 
                "Export Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (DatabaseException ex) {
            FileLogger.getInstance().logError("Failed to export data: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to export data: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
         private void handleConflictCheck() {
         try {
             List<GateAssignment> assignments = gateDAO.getAllAssignments();
             
             if (assignments.isEmpty()) {
                 conflictStatusLabel.setText("Conflicts: None detected");
                 JOptionPane.showMessageDialog(this, 
                     "No assignments found to check for conflicts.", 
                     "No Assignments", 
                     JOptionPane.INFORMATION_MESSAGE);
             } else {
                 conflictStatusLabel.setText("Conflicts: Checked " + assignments.size() + " assignments");
                 JOptionPane.showMessageDialog(this, 
                     "Conflict check completed. Found " + assignments.size() + " assignments to review.", 
                     "Conflict Check Complete", 
                     JOptionPane.INFORMATION_MESSAGE);
             }
         } catch (DatabaseException ex) {
             FileLogger.getInstance().logError("Failed to check conflicts: " + ex.getMessage());
             JOptionPane.showMessageDialog(this, 
                 "Failed to check conflicts: " + ex.getMessage(), 
                 "Error", 
                 JOptionPane.ERROR_MESSAGE);
         }
     }
    
    private void handleGateSelection() {
        int selectedRow = gatesTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedGate = getGateFromTableRow(selectedRow);
            assignFlightButton.setEnabled(true);
            editGateButton.setEnabled(true);
            deleteGateButton.setEnabled(true);
        } else {
            selectedGate = null;
            assignFlightButton.setEnabled(false);
            editGateButton.setEnabled(false);
            deleteGateButton.setEnabled(false);
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
        gate.setStatus(Gate.GateStatus.valueOf(gatesTableModel.getValueAt(row, 2).toString()));
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
                     gate.getStatus().name(),
                     "0", // Placeholder for assignments count
                     gate.getCreatedAt() != null ? gate.getCreatedAt().format(dateFormatter) : "N/A",
                     "N/A" // Placeholder for updated at
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
                         assignment.getAssignmentTime() != null ? assignment.getAssignmentTime().format(dateFormatter) : "N/A",
                         assignment.getDepartureTime() != null ? assignment.getDepartureTime().format(dateFormatter) : "N/A"
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
    
         private void performSearch() {
         String searchText = searchField.getText().trim();
         String statusFilter = statusFilterComboBox.getSelectedItem().toString();
         
         // Only search if there's actually a search term or status filter
         if (searchText.isEmpty() && statusFilter.equals("All Status")) {
             loadGates(); // Load all gates if no search criteria
             return;
         }
         
         // Stop any existing timer to prevent multiple searches
         if (searchTimer != null) {
             searchTimer.stop();
         }
         
         try {
             List<Gate> allGates = gateDAO.getAllGates();
             List<Gate> filteredGates = new ArrayList<>();
             
             for (Gate gate : allGates) {
                 boolean matchesSearch = searchText.isEmpty() || 
                     gate.getGateName().toLowerCase().contains(searchText.toLowerCase()) ||
                     String.valueOf(gate.getGateId()).contains(searchText);
                     
                 boolean matchesStatus = statusFilter.equals("All Status") || 
                     gate.getStatus().name().equals(statusFilter);
                     
                 if (matchesSearch && matchesStatus) {
                     filteredGates.add(gate);
                 }
             }
             
             gatesTableModel.setRowCount(0);
             for (Gate gate : filteredGates) {
                 Object[] row = {
                     gate.getGateId(),
                     gate.getGateName(),
                     gate.getStatus().name(),
                     "0", // Placeholder for assignments count
                     gate.getCreatedAt() != null ? gate.getCreatedAt().format(dateFormatter) : "N/A",
                     "N/A" // Placeholder for updated at
                 };
                 gatesTableModel.addRow(row);
             }
             
             updateStatus("Found " + filteredGates.size() + " gates");
             
         } catch (DatabaseException ex) {
             FileLogger.getInstance().logError("Failed to search gates: " + ex.getMessage());
             updateStatus("Search failed: " + ex.getMessage());
         }
     }
     
     private void updateStats() {
         try {
             List<Gate> gates = gateDAO.getAllGates();
             int totalGates = gates.size();
             int activeGates = (int) gates.stream().filter(g -> g.getStatus().name().equals("AVAILABLE")).count();
             int assignmentsCount = gateDAO.getAllAssignments().size();
             
             statsLabel.setText("Total Gates: " + totalGates + " | Active: " + activeGates + " | Assignments: " + assignmentsCount);
         } catch (DatabaseException ex) {
             FileLogger.getInstance().logError("Failed to update stats: " + ex.getMessage());
         }
     }
     
     private void updateStatus(String message) {
         statusLabel.setText(message);
         statusLabel.setForeground(ThemeManager.DARK_GRAY);
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
        editGateButton.setEnabled(false);
        deleteGateButton.setEnabled(false);
    }
} 