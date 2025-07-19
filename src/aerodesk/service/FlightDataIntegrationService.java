package aerodesk.service;

import aerodesk.dao.FlightDAO;
import aerodesk.model.Flight;
import aerodesk.util.FileLogger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for integrating Aviation Stack data with the existing flight management system
 * Provides real-time data synchronization and enhanced flight information
 */
public class FlightDataIntegrationService {
    
    private AviationStackService aviationService;
    private FlightDAO flightDAO;
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;
    
    public FlightDataIntegrationService() {
        this.aviationService = new AviationStackService();
        this.flightDAO = new FlightDAO();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    /**
     * Starts the real-time data synchronization service
     */
    public void startDataSync() {
        if (isRunning) {
            FileLogger.getInstance().logInfo("Flight data sync service is already running");
            return;
        }
        
        isRunning = true;
        FileLogger.getInstance().logInfo("Starting flight data synchronization service");
        
        // Sync flight data every 5 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                syncFlightData();
            } catch (Exception e) {
                FileLogger.getInstance().logError("Error in flight data sync: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.MINUTES);
        
        // Update flight status every 2 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateFlightStatus();
            } catch (Exception e) {
                FileLogger.getInstance().logError("Error updating flight status: " + e.getMessage());
            }
        }, 1, 2, TimeUnit.MINUTES);
    }
    
    /**
     * Stops the real-time data synchronization service
     */
    public void stopDataSync() {
        if (!isRunning) {
            return;
        }
        
        isRunning = false;
        scheduler.shutdown();
        FileLogger.getInstance().logInfo("Flight data synchronization service stopped");
    }
    
    /**
     * Synchronizes flight data from Aviation Stack with local database
     */
    private void syncFlightData() {
        try {
            List<Flight> localFlights = flightDAO.getAllFlights();
            
            for (Flight localFlight : localFlights) {
                try {
                    AviationStackService.FlightInfo apiFlight = aviationService.getFlightInfo(localFlight.getFlightNo());
                    
                    // Update local flight with API data if available
                    if (apiFlight != null && apiFlight.getStatus() != null) {
                        updateLocalFlightWithApiData(localFlight, apiFlight);
                        flightDAO.updateFlight(localFlight);
                        FileLogger.getInstance().logInfo("Updated flight " + localFlight.getFlightNo() + " with API data");
                    }
                } catch (Exception e) {
                    FileLogger.getInstance().logError("Error syncing flight " + localFlight.getFlightNo() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error in flight data sync: " + e.getMessage());
        }
    }
    
    /**
     * Updates flight status using Aviation Stack data
     */
    private void updateFlightStatus() {
        try {
            List<Flight> localFlights = flightDAO.getAllFlights();
            
            for (Flight localFlight : localFlights) {
                try {
                    AviationStackService.FlightInfo apiFlight = aviationService.getLiveFlightTracking(localFlight.getFlightNo());
                    
                    if (apiFlight != null && apiFlight.isLive()) {
                        // Update flight status with live data
                        updateFlightStatusWithLiveData(localFlight, apiFlight);
                        flightDAO.updateFlight(localFlight);
                        FileLogger.getInstance().logInfo("Updated live status for flight " + localFlight.getFlightNo());
                    }
                } catch (Exception e) {
                    FileLogger.getInstance().logError("Error updating status for flight " + localFlight.getFlightNo() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error updating flight status: " + e.getMessage());
        }
    }
    
    /**
     * Updates local flight data with Aviation Stack API data
     */
    private void updateLocalFlightWithApiData(Flight localFlight, AviationStackService.FlightInfo apiFlight) {
        // Update status
        if (apiFlight.getStatus() != null) {
            localFlight.setStatus(mapApiStatusToLocal(apiFlight.getStatus()));
        }
        
        // Update departure time
        if (apiFlight.getEstimatedDeparture() != null) {
            localFlight.setDepartTime(apiFlight.getEstimatedDeparture());
        }
        
        // Update arrival time
        if (apiFlight.getEstimatedArrival() != null) {
            localFlight.setArriveTime(apiFlight.getEstimatedArrival());
        }
        
        // Update aircraft information
        if (apiFlight.getAircraftType() != null) {
            localFlight.setAircraftType(apiFlight.getAircraftType());
        }
    }
    
    /**
     * Updates flight status with live tracking data
     */
    private void updateFlightStatusWithLiveData(Flight localFlight, AviationStackService.FlightInfo apiFlight) {
        // Update with live position data
        if (apiFlight.getLatitude() != 0 && apiFlight.getLongitude() != 0) {
            // Store live coordinates (could be added to Flight model)
            String liveData = String.format("LAT:%.4f,LON:%.4f,ALT:%d,SPD:%.0f", 
                apiFlight.getLatitude(), apiFlight.getLongitude(), 
                apiFlight.getAltitude(), apiFlight.getSpeed());
            
            // Update status to indicate live tracking
            if (Flight.FlightStatus.SCHEDULED.equals(localFlight.getStatus())) {
                localFlight.setStatus(Flight.FlightStatus.DEPARTED);
            }
        }
        
        // Update status based on live data
        if (apiFlight.getStatus() != null) {
            localFlight.setStatus(mapApiStatusToLocal(apiFlight.getStatus()));
        }
    }
    
    /**
     * Maps Aviation Stack status to local flight status
     */
    private Flight.FlightStatus mapApiStatusToLocal(String apiStatus) {
        switch (apiStatus.toLowerCase()) {
            case "scheduled":
                return Flight.FlightStatus.SCHEDULED;
            case "active":
            case "en-route":
                return Flight.FlightStatus.DEPARTED;
            case "landed":
                return Flight.FlightStatus.ON_TIME;
            case "cancelled":
                return Flight.FlightStatus.CANCELLED;
            case "delayed":
                return Flight.FlightStatus.DELAYED;
            default:
                return Flight.FlightStatus.SCHEDULED;
        }
    }
    
    /**
     * Gets enhanced flight information combining local and API data
     */
    public String getEnhancedFlightInfo(String flightNumber) {
        try {
            // Get local flight data
            Flight localFlight = flightDAO.getFlightByNumber(flightNumber);
            if (localFlight == null) {
                return "Flight not found in local database";
            }
            
            // Get API flight data
            AviationStackService.FlightInfo apiFlight = aviationService.getFlightInfo(flightNumber);
            
            StringBuilder info = new StringBuilder();
            info.append("=== Enhanced Flight Information ===\n");
            info.append("Flight Number: ").append(flightNumber).append("\n");
            info.append("Local Status: ").append(localFlight.getStatus()).append("\n");
            
            if (apiFlight != null) {
                info.append("API Status: ").append(apiFlight.getStatus()).append("\n");
                info.append("Airline: ").append(apiFlight.getAirline()).append("\n");
                info.append("Route: ").append(apiFlight.getDepartureAirport()).append(" → ").append(apiFlight.getArrivalAirport()).append("\n");
                
                if (apiFlight.getAircraftType() != null) {
                    info.append("Aircraft: ").append(apiFlight.getAircraftType()).append("\n");
                }
                
                if (apiFlight.getGate() != null) {
                    info.append("Gate: ").append(apiFlight.getGate()).append("\n");
                }
                
                if (apiFlight.isLive()) {
                    info.append("\n=== Live Tracking ===\n");
                    info.append("Location: ").append(apiFlight.getLatitude()).append(", ").append(apiFlight.getLongitude()).append("\n");
                    info.append("Altitude: ").append(apiFlight.getAltitude()).append(" ft\n");
                    info.append("Speed: ").append(apiFlight.getSpeed()).append(" km/h\n");
                }
            }
            
            return info.toString();
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error getting enhanced flight info: " + e.getMessage());
            return "Error retrieving flight information: " + e.getMessage();
        }
    }
    
    /**
     * Validates flight data against Aviation Stack
     */
    public boolean validateFlightData(String flightNumber) {
        try {
            Flight localFlight = flightDAO.getFlightByNumber(flightNumber);
            if (localFlight == null) {
                return false;
            }
            
            AviationStackService.FlightInfo apiFlight = aviationService.getFlightInfo(flightNumber);
            if (apiFlight == null) {
                return false;
            }
            
            // Basic validation - check if flight exists in both systems
            return localFlight.getFlightNo().equals(apiFlight.getFlightNumber());
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error validating flight data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets flight statistics combining local and API data
     */
    public String getFlightStatistics() {
        try {
            List<Flight> localFlights = flightDAO.getAllFlights();
            int totalFlights = localFlights.size();
            int scheduledFlights = 0;
            int inFlightFlights = 0;
            int arrivedFlights = 0;
            int cancelledFlights = 0;
            
            for (Flight flight : localFlights) {
                switch (flight.getStatus()) {
                    case SCHEDULED:
                        scheduledFlights++;
                        break;
                    case DEPARTED:
                        inFlightFlights++;
                        break;
                    case ON_TIME:
                        arrivedFlights++;
                        break;
                    case CANCELLED:
                        cancelledFlights++;
                        break;
                    default:
                        break;
                }
            }
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== Flight Statistics ===\n");
            stats.append("Total Flights: ").append(totalFlights).append("\n");
            stats.append("Scheduled: ").append(scheduledFlights).append("\n");
            stats.append("In Flight: ").append(inFlightFlights).append("\n");
            stats.append("Arrived: ").append(arrivedFlights).append("\n");
            stats.append("Cancelled: ").append(cancelledFlights).append("\n");
            stats.append("API Integration: ").append(aviationService.isApiAvailable() ? "Active" : "Inactive").append("\n");
            
            return stats.toString();
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error getting flight statistics: " + e.getMessage());
            return "Error retrieving flight statistics: " + e.getMessage();
        }
    }
    
    /**
     * Checks if the integration service is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Gets the Aviation Stack service
     */
    public AviationStackService getAviationService() {
        return aviationService;
    }
} 