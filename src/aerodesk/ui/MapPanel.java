package aerodesk.ui;

import aerodesk.model.Flight;
import aerodesk.model.Weather;
import aerodesk.service.AviationStackService;
import aerodesk.service.WeatherService;
import aerodesk.util.ThemeManager;
import aerodesk.util.FileLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Map Panel for displaying interactive airport map with flight status and weather overlay
 * Provides visual representation of flights and weather conditions
 */
public class MapPanel extends JPanel {
    
    private final AviationStackService aviationService;
    private final WeatherService weatherService;
    private JPanel mapCanvas;
    private JLabel statusLabel;
    private JTextArea flightInfoArea;
    private final ScheduledExecutorService updateScheduler;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private List<Flight> currentFlights;
    private Map<String, Weather> currentWeather;
    private Flight selectedFlight;
    
    // Airport coordinates (simplified for demo)
    private final Map<String, Point> airportCoordinates = Map.of(
        "JFK", new Point(100, 80),
        "LAX", new Point(50, 120),
        "LHR", new Point(150, 60),
        "CDG", new Point(160, 70),
        "NRT", new Point(250, 90),
        "SIN", new Point(220, 140),
        "DXB", new Point(180, 110),
        "HKG", new Point(240, 120)
    );
    
    public MapPanel() {
        this.aviationService = new AviationStackService();
        this.weatherService = WeatherService.getInstance();
        this.updateScheduler = Executors.newScheduledThreadPool(1);
        
        initializeComponents();
        setupLayout();
        startAutoUpdate();
        
        FileLogger.getInstance().logInfo("Map panel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        setBackground(ThemeManager.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Map canvas
        mapCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapCanvas.setBackground(ThemeManager.LIGHT_GRAY);
        mapCanvas.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_GRAY, 2));
        mapCanvas.setPreferredSize(new Dimension(600, 400));
        mapCanvas.addMouseListener(new MapMouseListener());
        
        // Status label
        statusLabel = new JLabel("Loading flight data...");
        statusLabel.setFont(ThemeManager.SUBHEADER_FONT);
        statusLabel.setForeground(ThemeManager.DARK_GRAY);
        
        // Flight info area
        flightInfoArea = new JTextArea();
        flightInfoArea.setEditable(false);
        flightInfoArea.setFont(ThemeManager.BODY_FONT);
        flightInfoArea.setBackground(ThemeManager.WHITE);
        flightInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        flightInfoArea.setLineWrap(true);
        flightInfoArea.setWrapStyleWord(true);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(ThemeManager.WHITE);
        
        // Map panel
        JPanel mapContainer = new JPanel(new BorderLayout(10, 10));
        mapContainer.setBackground(ThemeManager.WHITE);
        mapContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_GRAY),
            "Interactive Flight Map",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            ThemeManager.SUBHEADER_FONT,
            ThemeManager.DARK_GRAY
        ));
        
        mapContainer.add(statusLabel, BorderLayout.NORTH);
        mapContainer.add(mapCanvas, BorderLayout.CENTER);
        
        // Flight info panel
        JPanel infoPanel = createInfoPanel();
        
        contentPanel.add(mapContainer, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.EAST);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Live Flight Status & Weather Map");
        titleLabel.setFont(ThemeManager.TITLE_FONT);
        titleLabel.setForeground(ThemeManager.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        ThemeManager.styleButton(refreshBtn, ThemeManager.WHITE, ThemeManager.PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> refreshData());
        
        buttonPanel.add(refreshBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create info panel
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(ThemeManager.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_GRAY),
            "Flight Information",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            ThemeManager.SUBHEADER_FONT,
            ThemeManager.DARK_GRAY
        ));
        infoPanel.setPreferredSize(new Dimension(300, 400));
        
        // Legend
        JPanel legendPanel = createLegendPanel();
        
        // Flight info area
        JScrollPane scrollPane = new JScrollPane(flightInfoArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        infoPanel.add(legendPanel, BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    /**
     * Create legend panel
     */
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        legendPanel.setBackground(ThemeManager.LIGHT_GRAY);
        legendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        legendPanel.add(createLegendItem("🟢 On Time", ThemeManager.SUCCESS_GREEN));
        legendPanel.add(createLegendItem("🟡 Delayed", ThemeManager.WARNING_AMBER));
        legendPanel.add(createLegendItem("🔴 Cancelled", ThemeManager.ERROR_RED));
        legendPanel.add(createLegendItem("🌤️ Weather", ThemeManager.PRIMARY_BLUE));
        
        return legendPanel;
    }
    
    /**
     * Create legend item
     */
    private JLabel createLegendItem(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.BODY_FONT);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Draw the map
     */
    private void drawMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        g2d.setColor(ThemeManager.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw airports
        drawAirports(g2d);
        
        // Draw weather
        drawWeather(g2d);
        
        // Draw flights
        drawFlights(g2d);
    }
    
    /**
     * Draw airports on the map
     */
    private void drawAirports(Graphics2D g2d) {
        g2d.setFont(ThemeManager.BODY_FONT);
        
        for (Map.Entry<String, Point> entry : airportCoordinates.entrySet()) {
            String code = entry.getKey();
            Point pos = entry.getValue();
            
            // Draw airport marker
            g2d.setColor(ThemeManager.DARK_GRAY);
            g2d.fillOval(pos.x - 5, pos.y - 5, 10, 10);
            g2d.setColor(ThemeManager.WHITE);
            g2d.drawOval(pos.x - 5, pos.y - 5, 10, 10);
            
            // Draw airport code
            g2d.setColor(ThemeManager.DARK_GRAY);
            g2d.drawString(code, pos.x + 8, pos.y + 4);
        }
    }
    
    /**
     * Draw weather information
     */
    private void drawWeather(Graphics2D g2d) {
        if (currentWeather == null) return;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        for (Map.Entry<String, Weather> entry : currentWeather.entrySet()) {
            String airportCode = entry.getKey();
            Weather weather = entry.getValue();
            Point pos = airportCoordinates.get(airportCode);
            
            if (pos != null) {
                // Draw weather icon and temperature
                g2d.setColor(ThemeManager.PRIMARY_BLUE);
                g2d.drawString(weather.getWeatherIcon() + " " + weather.getFormattedTemperature(), 
                    pos.x - 20, pos.y - 15);
            }
        }
    }
    
    /**
     * Draw flights on the map
     */
    private void drawFlights(Graphics2D g2d) {
        if (currentFlights == null) return;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        
        for (Flight flight : currentFlights) {
            String departure = flight.getOrigin();
            String arrival = flight.getDestination();
            
            Point depPos = airportCoordinates.get(departure);
            Point arrPos = airportCoordinates.get(arrival);
            
            if (depPos != null && arrPos != null) {
                // Draw flight path
                Color flightColor = getFlightStatusColor(flight.getStatus().toString());
                g2d.setColor(flightColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(depPos.x, depPos.y, arrPos.x, arrPos.y);
                
                // Draw flight marker
                Point midPoint = new Point((depPos.x + arrPos.x) / 2, (depPos.y + arrPos.y) / 2);
                g2d.fillOval(midPoint.x - 3, midPoint.y - 3, 6, 6);
                
                // Draw flight number
                g2d.setColor(ThemeManager.DARK_GRAY);
                g2d.drawString(flight.getFlightNo(), midPoint.x + 5, midPoint.y - 5);
            }
        }
    }
    
    /**
     * Get color for flight status
     */
    private Color getFlightStatusColor(String status) {
        if (status == null) return ThemeManager.DARK_GRAY;
        
        switch (status.toUpperCase()) {
            case "ON_TIME":
            case "SCHEDULED":
                return ThemeManager.SUCCESS_GREEN;
            case "DELAYED":
                return ThemeManager.WARNING_AMBER;
            case "CANCELLED":
                return ThemeManager.ERROR_RED;
            default:
                return ThemeManager.DARK_GRAY;
        }
    }
    
    /**
     * Mouse listener for map interactions
     */
    private class MapMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickPos = e.getPoint();
            
            // Check if clicked on a flight
            if (currentFlights != null) {
                for (Flight flight : currentFlights) {
                    if (isFlightClicked(flight, clickPos)) {
                        selectedFlight = flight;
                        updateFlightInfo();
                        return;
                    }
                }
            }
            
            // Check if clicked on an airport
            for (Map.Entry<String, Point> entry : airportCoordinates.entrySet()) {
                String code = entry.getKey();
                Point pos = entry.getValue();
                
                if (Math.abs(clickPos.x - pos.x) < 10 && Math.abs(clickPos.y - pos.y) < 10) {
                    showAirportInfo(code);
                    return;
                }
            }
        }
    }
    
    /**
     * Check if a flight was clicked
     */
    private boolean isFlightClicked(Flight flight, Point clickPos) {
        String departure = flight.getOrigin();
        String arrival = flight.getDestination();
        
        Point depPos = airportCoordinates.get(departure);
        Point arrPos = airportCoordinates.get(arrival);
        
        if (depPos != null && arrPos != null) {
            Point midPoint = new Point((depPos.x + arrPos.x) / 2, (depPos.y + arrPos.y) / 2);
            return Math.abs(clickPos.x - midPoint.x) < 10 && Math.abs(clickPos.y - midPoint.y) < 10;
        }
        
        return false;
    }
    
    /**
     * Update flight information display
     */
    private void updateFlightInfo() {
        if (selectedFlight == null) {
            flightInfoArea.setText("Click on a flight or airport for information");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("=== Flight Information ===\n\n");
        info.append("Flight: ").append(selectedFlight.getFlightNo()).append("\n");
        info.append("Route: ").append(selectedFlight.getOrigin()).append(" → ").append(selectedFlight.getDestination()).append("\n");
        info.append("Status: ").append(selectedFlight.getStatus()).append("\n");
        info.append("Departure: ").append(selectedFlight.getDepartTime()).append("\n");
        info.append("Arrival: ").append(selectedFlight.getArriveTime()).append("\n");
        info.append("Aircraft: ").append(selectedFlight.getAircraftType()).append("\n");
        
        flightInfoArea.setText(info.toString());
    }
    
    /**
     * Show airport information
     */
    private void showAirportInfo(String airportCode) {
        StringBuilder info = new StringBuilder();
        info.append("=== Airport Information ===\n\n");
        info.append("Airport: ").append(airportCode).append("\n");
        
        if (currentWeather != null && currentWeather.containsKey(airportCode)) {
            Weather weather = currentWeather.get(airportCode);
            info.append("Weather: ").append(weather.getWeatherSummary()).append("\n");
            info.append("Temperature: ").append(weather.getFormattedTemperature()).append("\n");
            info.append("Humidity: ").append(weather.getFormattedHumidity()).append("\n");
            info.append("Wind: ").append(weather.getFormattedWindSpeed()).append("\n");
        }
        
        flightInfoArea.setText(info.toString());
    }
    
    /**
     * Start automatic updates
     */
    private void startAutoUpdate() {
        updateScheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::refreshData);
        }, 0, 60, TimeUnit.SECONDS);
    }
    
    /**
     * Refresh map data
     */
    public void refreshData() {
        try {
            statusLabel.setText("Updating flight data...");
            
            // Update flights - convert FlightInfo to Flight
            List<AviationStackService.FlightInfo> flightInfos = aviationService.getFlightsByAirport("JFK", "departure");
            currentFlights = flightInfos.stream()
                .map(this::convertFlightInfoToFlight)
                .collect(Collectors.toList());
            
            // Update weather
            currentWeather = weatherService.getMajorAirportsWeather().get();
            
            // Update display
            mapCanvas.repaint();
            statusLabel.setText("Last Update: " + LocalDateTime.now().format(timeFormatter));
            
            FileLogger.getInstance().logInfo("Map data refreshed");
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error refreshing map data: " + e.getMessage());
            statusLabel.setText("Error updating data");
        }
    }
    
    /**
     * Convert FlightInfo to Flight
     */
    private Flight convertFlightInfoToFlight(AviationStackService.FlightInfo flightInfo) {
        Flight flight = new Flight();
        flight.setFlightNo(flightInfo.getFlightNumber());
        flight.setOrigin(flightInfo.getDepartureAirport());
        flight.setDestination(flightInfo.getArrivalAirport());
        flight.setDepartTime(flightInfo.getScheduledDeparture());
        flight.setArriveTime(flightInfo.getScheduledArrival());
        flight.setAircraftType(flightInfo.getAircraftType());
        
        // Convert status string to FlightStatus enum
        String status = flightInfo.getStatus();
        if (status != null) {
            try {
                flight.setStatus(Flight.FlightStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                flight.setStatus(Flight.FlightStatus.SCHEDULED);
            }
        } else {
            flight.setStatus(Flight.FlightStatus.SCHEDULED);
        }
        
        return flight;
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (updateScheduler != null && !updateScheduler.isShutdown()) {
            updateScheduler.shutdown();
            try {
                if (!updateScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    updateScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                updateScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        FileLogger.getInstance().logInfo("Map panel cleanup completed");
    }
} 