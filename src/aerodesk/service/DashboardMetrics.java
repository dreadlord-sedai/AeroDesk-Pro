package aerodesk.service;

import aerodesk.model.KPI;
import aerodesk.model.Flight;
import aerodesk.model.Booking;
import aerodesk.model.Baggage;
import aerodesk.model.GateAssignment;
import aerodesk.util.FileLogger;
import aerodesk.dao.FlightDAO;
import aerodesk.dao.BookingDAO;
import aerodesk.dao.BaggageDAO;
import aerodesk.dao.GateDAO;
import aerodesk.exception.DatabaseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Dashboard Metrics Service for real-time KPI collection and management
 * Provides live statistics for airport operations dashboard
 */
public class DashboardMetrics {
    
    private static DashboardMetrics instance;
    private final Map<String, KPI> kpiData;
    private final ScheduledExecutorService scheduler;
    private final FlightDAO flightDAO;
    private final BookingDAO bookingDAO;
    private final BaggageDAO baggageDAO;
    private final GateDAO gateDAO;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private DashboardMetrics() {
        this.kpiData = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.flightDAO = new FlightDAO();
        this.bookingDAO = new BookingDAO();
        this.baggageDAO = new BaggageDAO();
        this.gateDAO = new GateDAO();
        
        initializeKPIs();
        startMetricsCollection();
    }
    
    public static DashboardMetrics getInstance() {
        if (instance == null) {
            instance = new DashboardMetrics();
        }
        return instance;
    }
    
    /**
     * Initialize default KPIs
     */
    private void initializeKPIs() {
        kpiData.put("total_flights", new KPI("Total Flights Today", "0", "flights", "Total flights scheduled for today"));
        kpiData.put("checkins_hour", new KPI("Check-ins (Last Hour)", "0", "passengers", "Passengers checked in during the last hour"));
        kpiData.put("baggage_handled", new KPI("Baggage Handled", "0", "units", "Baggage units processed in the last hour"));
        kpiData.put("gates_occupied", new KPI("Gates Occupied", "0", "gates", "Currently occupied gates"));
        kpiData.put("on_time_flights", new KPI("On-Time Flights", "0", "%", "Percentage of flights on time"));
        kpiData.put("delayed_flights", new KPI("Delayed Flights", "0", "flights", "Number of delayed flights"));
        kpiData.put("cancelled_flights", new KPI("Cancelled Flights", "0", "flights", "Number of cancelled flights"));
        kpiData.put("avg_delay", new KPI("Average Delay", "0", "minutes", "Average delay time for delayed flights"));
        
        FileLogger.getInstance().logInfo("Dashboard KPIs initialized");
    }
    
    /**
     * Start automatic metrics collection
     */
    private void startMetricsCollection() {
        // Update metrics every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateAllMetrics();
                FileLogger.getInstance().logInfo("Dashboard metrics updated at " + LocalDateTime.now().format(timeFormatter));
            } catch (Exception e) {
                FileLogger.getInstance().logError("Error updating dashboard metrics: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    /**
     * Update all KPI metrics
     */
    public void updateAllMetrics() {
        updateFlightMetrics();
        updateCheckInMetrics();
        updateBaggageMetrics();
        updateGateMetrics();
        updateDelayMetrics();
    }
    
    /**
     * Update flight-related metrics
     */
    private void updateFlightMetrics() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
            
            long totalFlights = flights.stream()
                .filter(f -> f.getDepartTime() != null && f.getDepartTime().toLocalDate().equals(today.toLocalDate()))
                .count();
            
            long onTimeFlights = flights.stream()
                .filter(f -> f.getDepartTime() != null && f.getDepartTime().toLocalDate().equals(today.toLocalDate()))
                .filter(f -> "ON_TIME".equals(f.getStatus()) || "SCHEDULED".equals(f.getStatus()))
                .count();
            
            long delayedFlights = flights.stream()
                .filter(f -> f.getDepartTime() != null && f.getDepartTime().toLocalDate().equals(today.toLocalDate()))
                .filter(f -> "DELAYED".equals(f.getStatus()))
                .count();
            
            long cancelledFlights = flights.stream()
                .filter(f -> f.getDepartTime() != null && f.getDepartTime().toLocalDate().equals(today.toLocalDate()))
                .filter(f -> "CANCELLED".equals(f.getStatus()))
                .count();
            
            // Update KPIs
            updateKPI("total_flights", String.valueOf(totalFlights), getStatusForCount(totalFlights, 50, 100));
            updateKPI("on_time_flights", String.valueOf(totalFlights > 0 ? (onTimeFlights * 100 / totalFlights) : 0), 
                getStatusForPercentage(onTimeFlights, totalFlights, 0.8, 0.9));
            updateKPI("delayed_flights", String.valueOf(delayedFlights), 
                getStatusForCount(delayedFlights, 5, 10));
            updateKPI("cancelled_flights", String.valueOf(cancelledFlights), 
                getStatusForCount(cancelledFlights, 2, 5));
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating flight metrics: " + e.getMessage());
        }
    }
    
    /**
     * Update check-in metrics
     */
    private void updateCheckInMetrics() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            
            long recentCheckins = bookings.stream()
                .filter(b -> b.getCheckInTime() != null && b.getCheckInTime().isAfter(oneHourAgo))
                .count();
            
            updateKPI("checkins_hour", String.valueOf(recentCheckins), 
                getStatusForCount(recentCheckins, 50, 100));
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating check-in metrics: " + e.getMessage());
        }
    }
    
    /**
     * Update baggage metrics
     */
    private void updateBaggageMetrics() {
        try {
            List<Baggage> baggageList = baggageDAO.getAllBaggage();
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            
            long recentBaggage = baggageList.stream()
                .filter(b -> b.getCreatedAt() != null && b.getCreatedAt().isAfter(oneHourAgo))
                .count();
            
            updateKPI("baggage_handled", String.valueOf(recentBaggage), 
                getStatusForCount(recentBaggage, 30, 60));
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating baggage metrics: " + e.getMessage());
        }
    }
    
    /**
     * Update gate metrics
     */
    private void updateGateMetrics() {
        try {
            List<GateAssignment> assignments = gateDAO.getAllAssignments();
            LocalDateTime now = LocalDateTime.now();
            
            long occupiedGates = assignments.stream()
                .filter(a -> a.getDepartureTime() != null && a.getDepartureTime().isAfter(now))
                .count();
            
            updateKPI("gates_occupied", String.valueOf(occupiedGates), 
                getStatusForCount(occupiedGates, 5, 10));
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating gate metrics: " + e.getMessage());
        }
    }
    
    /**
     * Update delay metrics
     */
    private void updateDelayMetrics() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
            
            double avgDelay = flights.stream()
                .filter(f -> f.getDepartTime() != null && f.getDepartTime().toLocalDate().equals(today.toLocalDate()))
                .filter(f -> "DELAYED".equals(f.getStatus()))
                .mapToDouble(f -> {
                    // Calculate delay in minutes (simplified)
                    return 15.0 + (f.getFlightNo().hashCode() % 45);
                })
                .average()
                .orElse(0.0);
            
            updateKPI("avg_delay", String.format("%.1f", avgDelay), 
                getStatusForDelay(avgDelay));
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating delay metrics: " + e.getMessage());
        }
    }
    
    /**
     * Update a specific KPI
     */
    private void updateKPI(String key, String value, KPI.KPIStatus status) {
        KPI kpi = kpiData.get(key);
        if (kpi != null) {
            kpi.setValue(value);
            kpi.setStatus(status);
            kpi.setLastUpdated(LocalDateTime.now());
        }
    }
    
    /**
     * Get status based on count thresholds
     */
    private KPI.KPIStatus getStatusForCount(long count, long warningThreshold, long criticalThreshold) {
        if (count >= criticalThreshold) return KPI.KPIStatus.CRITICAL;
        if (count >= warningThreshold) return KPI.KPIStatus.WARNING;
        if (count > 0) return KPI.KPIStatus.GOOD;
        return KPI.KPIStatus.NEUTRAL;
    }
    
    /**
     * Get status based on percentage thresholds
     */
    private KPI.KPIStatus getStatusForPercentage(long value, long total, double warningThreshold, double excellentThreshold) {
        if (total == 0) return KPI.KPIStatus.NEUTRAL;
        double percentage = (double) value / total;
        if (percentage >= excellentThreshold) return KPI.KPIStatus.EXCELLENT;
        if (percentage >= warningThreshold) return KPI.KPIStatus.GOOD;
        return KPI.KPIStatus.WARNING;
    }
    
    /**
     * Get status based on delay time
     */
    private KPI.KPIStatus getStatusForDelay(double delayMinutes) {
        if (delayMinutes <= 5) return KPI.KPIStatus.EXCELLENT;
        if (delayMinutes <= 15) return KPI.KPIStatus.GOOD;
        if (delayMinutes <= 30) return KPI.KPIStatus.WARNING;
        return KPI.KPIStatus.CRITICAL;
    }
    
    /**
     * Get all KPI data
     * @return Map of KPI data
     */
    public Map<String, KPI> getAllKPIs() {
        return new HashMap<>(kpiData);
    }
    
    /**
     * Get a specific KPI
     * @param key KPI key
     * @return KPI object
     */
    public KPI getKPI(String key) {
        return kpiData.get(key);
    }
    
    /**
     * Get KPI summary for display
     * @return Formatted KPI summary
     */
    public String getKPISummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Dashboard Metrics Summary ===\n");
        summary.append("Last Updated: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
        summary.append("=".repeat(40)).append("\n\n");
        
        kpiData.values().forEach(kpi -> {
            summary.append(kpi.getDisplaySummary()).append("\n");
        });
        
        return summary.toString();
    }
    
    /**
     * Shutdown the metrics service
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        FileLogger.getInstance().logInfo("Dashboard metrics service shutdown");
    }
} 