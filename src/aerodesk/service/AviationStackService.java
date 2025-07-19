package aerodesk.service;

import aerodesk.util.ApiIntegrator;
import aerodesk.util.FileLogger;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for Aviation Stack API integration
 * Handles flight data processing and provides business logic for flight information
 */
public class AviationStackService {
    
    /**
     * Flight information data class
     */
    public static class FlightInfo {
        private String flightNumber;
        private String airline;
        private String departureAirport;
        private String arrivalAirport;
        private LocalDateTime scheduledDeparture;
        private LocalDateTime scheduledArrival;
        private LocalDateTime estimatedDeparture;
        private LocalDateTime estimatedArrival;
        private String status;
        private String gate;
        private String terminal;
        private double latitude;
        private double longitude;
        private int altitude;
        private double speed;
        private boolean isLive;
        
        // Getters and setters
        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
        
        public String getAirline() { return airline; }
        public void setAirline(String airline) { this.airline = airline; }
        
        public String getDepartureAirport() { return departureAirport; }
        public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }
        
        public String getArrivalAirport() { return arrivalAirport; }
        public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }
        
        public LocalDateTime getScheduledDeparture() { return scheduledDeparture; }
        public void setScheduledDeparture(LocalDateTime scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }
        
        public LocalDateTime getScheduledArrival() { return scheduledArrival; }
        public void setScheduledArrival(LocalDateTime scheduledArrival) { this.scheduledArrival = scheduledArrival; }
        
        public LocalDateTime getEstimatedDeparture() { return estimatedDeparture; }
        public void setEstimatedDeparture(LocalDateTime estimatedDeparture) { this.estimatedDeparture = estimatedDeparture; }
        
        public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
        public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getGate() { return gate; }
        public void setGate(String gate) { this.gate = gate; }
        
        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public int getAltitude() { return altitude; }
        public void setAltitude(int altitude) { this.altitude = altitude; }
        
        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
        
        public boolean isLive() { return isLive; }
        public void setLive(boolean live) { isLive = live; }
        
        @Override
        public String toString() {
            return String.format("Flight %s (%s) - %s to %s - Status: %s", 
                flightNumber, airline, departureAirport, arrivalAirport, status);
        }
    }
    
    /**
     * Airport information data class
     */
    public static class AirportInfo {
        private String name;
        private String iataCode;
        private String icaoCode;
        private double latitude;
        private double longitude;
        private String timezone;
        private String country;
        private String website;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIataCode() { return iataCode; }
        public void setIataCode(String iataCode) { this.iataCode = iataCode; }
        
        public String getIcaoCode() { return icaoCode; }
        public void setIcaoCode(String icaoCode) { this.icaoCode = icaoCode; }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        @Override
        public String toString() {
            return String.format("%s (%s) - %s", name, iataCode, country);
        }
    }
    
    /**
     * Gets flight information for a specific flight number
     * @param flightNumber The flight number to search for
     * @return FlightInfo object with flight details
     */
    public FlightInfo getFlightInfo(String flightNumber) {
        try {
            String jsonData = ApiIntegrator.getFlightData(flightNumber);
            return parseFlightData(jsonData, flightNumber);
        } catch (IOException e) {
            FileLogger.getInstance().logError("Error fetching flight info for " + flightNumber + ": " + e.getMessage());
            return createMockFlightInfo(flightNumber);
        }
    }
    
    /**
     * Gets live flight tracking information
     * @param flightNumber The flight number to track
     * @return FlightInfo object with live tracking data
     */
    public FlightInfo getLiveFlightTracking(String flightNumber) {
        try {
            String jsonData = ApiIntegrator.getLiveFlightTracking(flightNumber);
            FlightInfo flightInfo = parseFlightData(jsonData, flightNumber);
            flightInfo.setLive(true);
            return flightInfo;
        } catch (IOException e) {
            FileLogger.getInstance().logError("Error fetching live tracking for " + flightNumber + ": " + e.getMessage());
            return createMockFlightInfo(flightNumber);
        }
    }
    
    /**
     * Gets airport information
     * @param airportCode The airport IATA code
     * @return AirportInfo object with airport details
     */
    public AirportInfo getAirportInfo(String airportCode) {
        try {
            String jsonData = ApiIntegrator.getAirportData(airportCode);
            return parseAirportData(jsonData, airportCode);
        } catch (IOException e) {
            FileLogger.getInstance().logError("Error fetching airport info for " + airportCode + ": " + e.getMessage());
            return createMockAirportInfo(airportCode);
        }
    }
    
    /**
     * Searches for flights by route
     * @param departureAirport Departure airport code
     * @param arrivalAirport Arrival airport code
     * @return List of FlightInfo objects
     */
    public List<FlightInfo> searchFlightsByRoute(String departureAirport, String arrivalAirport) {
        List<FlightInfo> flights = new ArrayList<>();
        
        // For demo purposes, create mock flights for the route
        // In a real implementation, this would call Aviation Stack's route search API
        flights.add(createMockFlightInfo("AA101"));
        flights.add(createMockFlightInfo("UA202"));
        flights.add(createMockFlightInfo("DL303"));
        
        FileLogger.getInstance().logInfo("Searched flights from " + departureAirport + " to " + arrivalAirport + ": " + flights.size() + " flights found");
        return flights;
    }
    
    /**
     * Gets flight status summary
     * @param flightNumber The flight number
     * @return Status summary string
     */
    public String getFlightStatusSummary(String flightNumber) {
        FlightInfo flight = getFlightInfo(flightNumber);
        
        if (flight == null) {
            return "Flight information not available";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Flight: ").append(flight.getFlightNumber()).append("\n");
        summary.append("Airline: ").append(flight.getAirline()).append("\n");
        summary.append("Route: ").append(flight.getDepartureAirport()).append(" → ").append(flight.getArrivalAirport()).append("\n");
        summary.append("Status: ").append(flight.getStatus()).append("\n");
        
        if (flight.getGate() != null) {
            summary.append("Gate: ").append(flight.getGate()).append("\n");
        }
        
        if (flight.getScheduledDeparture() != null) {
            summary.append("Scheduled Departure: ").append(flight.getScheduledDeparture()).append("\n");
        }
        
        if (flight.getEstimatedDeparture() != null) {
            summary.append("Estimated Departure: ").append(flight.getEstimatedDeparture()).append("\n");
        }
        
        if (flight.isLive()) {
            summary.append("Live Tracking: Available\n");
            summary.append("Location: ").append(flight.getLatitude()).append(", ").append(flight.getLongitude()).append("\n");
            summary.append("Altitude: ").append(flight.getAltitude()).append(" ft\n");
            summary.append("Speed: ").append(flight.getSpeed()).append(" km/h\n");
        }
        
        return summary.toString();
    }
    
    /**
     * Parses flight data from JSON response
     * @param jsonData The JSON response from Aviation Stack
     * @param flightNumber The flight number
     * @return Parsed FlightInfo object
     */
    private FlightInfo parseFlightData(String jsonData, String flightNumber) {
        // Simple JSON parsing (in a production environment, use a proper JSON library)
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(flightNumber);
        
        // Extract basic information from JSON
        if (jsonData.contains("\"flight_status\"")) {
            String status = extractJsonValue(jsonData, "flight_status");
            flightInfo.setStatus(status != null ? status : "unknown");
        }
        
        if (jsonData.contains("\"airline\"")) {
            String airline = extractJsonValue(jsonData, "name");
            flightInfo.setAirline(airline != null ? airline : "Unknown Airline");
        }
        
        // Extract departure information
        if (jsonData.contains("\"departure\"")) {
            String depAirport = extractJsonValue(jsonData, "airport");
            if (depAirport != null) {
                flightInfo.setDepartureAirport(depAirport);
            }
            
            String gate = extractJsonValue(jsonData, "gate");
            if (gate != null) {
                flightInfo.setGate(gate);
            }
        }
        
        // Extract arrival information
        if (jsonData.contains("\"arrival\"")) {
            String arrAirport = extractJsonValue(jsonData, "airport");
            if (arrAirport != null) {
                flightInfo.setArrivalAirport(arrAirport);
            }
        }
        
        // Extract live tracking data
        if (jsonData.contains("\"live\"")) {
            try {
                String latStr = extractJsonValue(jsonData, "latitude");
                String lonStr = extractJsonValue(jsonData, "longitude");
                String altStr = extractJsonValue(jsonData, "altitude");
                String speedStr = extractJsonValue(jsonData, "speed_horizontal");
                
                if (latStr != null) flightInfo.setLatitude(Double.parseDouble(latStr));
                if (lonStr != null) flightInfo.setLongitude(Double.parseDouble(lonStr));
                if (altStr != null) flightInfo.setAltitude(Integer.parseInt(altStr));
                if (speedStr != null) flightInfo.setSpeed(Double.parseDouble(speedStr));
            } catch (NumberFormatException e) {
                FileLogger.getInstance().logError("Error parsing live tracking coordinates: " + e.getMessage());
            }
        }
        
        return flightInfo;
    }
    
    /**
     * Parses airport data from JSON response
     * @param jsonData The JSON response from Aviation Stack
     * @param airportCode The airport code
     * @return Parsed AirportInfo object
     */
    private AirportInfo parseAirportData(String jsonData, String airportCode) {
        AirportInfo airportInfo = new AirportInfo();
        airportInfo.setIataCode(airportCode);
        
        // Extract basic information from JSON
        String name = extractJsonValue(jsonData, "airport_name");
        if (name != null) {
            airportInfo.setName(name);
        }
        
        String country = extractJsonValue(jsonData, "country_name");
        if (country != null) {
            airportInfo.setCountry(country);
        }
        
        String timezone = extractJsonValue(jsonData, "timezone");
        if (timezone != null) {
            airportInfo.setTimezone(timezone);
        }
        
        String website = extractJsonValue(jsonData, "website");
        if (website != null) {
            airportInfo.setWebsite(website);
        }
        
        // Extract coordinates
        try {
            String latStr = extractJsonValue(jsonData, "latitude");
            String lonStr = extractJsonValue(jsonData, "longitude");
            
            if (latStr != null) airportInfo.setLatitude(Double.parseDouble(latStr));
            if (lonStr != null) airportInfo.setLongitude(Double.parseDouble(lonStr));
        } catch (NumberFormatException e) {
            FileLogger.getInstance().logError("Error parsing airport coordinates: " + e.getMessage());
        }
        
        return airportInfo;
    }
    
    /**
     * Extracts a value from JSON string by key
     * @param json The JSON string
     * @param key The key to search for
     * @return The value or null if not found
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        if (endIndex == -1) {
            endIndex = json.indexOf("]", startIndex);
        }
        
        if (endIndex == -1) return null;
        
        String value = json.substring(startIndex, endIndex).trim();
        // Remove quotes if present
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        
        return value;
    }
    
    /**
     * Creates mock flight information for demonstration
     * @param flightNumber The flight number
     * @return Mock FlightInfo object
     */
    private FlightInfo createMockFlightInfo(String flightNumber) {
        FlightInfo flight = new FlightInfo();
        flight.setFlightNumber(flightNumber);
        flight.setAirline("American Airlines");
        flight.setDepartureAirport("JFK");
        flight.setArrivalAirport("LAX");
        flight.setStatus("scheduled");
        flight.setGate("A1");
        flight.setTerminal("1");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(5));
        flight.setLatitude(40.6413);
        flight.setLongitude(-73.7781);
        flight.setAltitude(35000);
        flight.setSpeed(850);
        flight.setLive(false);
        
        return flight;
    }
    
    /**
     * Creates mock airport information for demonstration
     * @param airportCode The airport code
     * @return Mock AirportInfo object
     */
    private AirportInfo createMockAirportInfo(String airportCode) {
        AirportInfo airport = new AirportInfo();
        airport.setName("John F. Kennedy International Airport");
        airport.setIataCode(airportCode);
        airport.setIcaoCode(airportCode);
        airport.setLatitude(40.6413);
        airport.setLongitude(-73.7781);
        airport.setTimezone("America/New_York");
        airport.setCountry("United States");
        airport.setWebsite("https://www.jfkairport.com");
        
        return airport;
    }
    
    /**
     * Checks if Aviation Stack API is available
     * @return true if available, false otherwise
     */
    public boolean isApiAvailable() {
        try {
            ApiIntegrator integrator = new ApiIntegrator();
            return integrator.isAviationStackAvailable();
        } catch (Exception e) {
            return false;
        }
    }
} 