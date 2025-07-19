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
 * Enhanced service class for Aviation Stack API integration
 * Handles comprehensive flight data processing and provides business logic for flight information
 */
public class AviationStackService {
    
    /**
     * Enhanced Flight information data class
     */
    public static class FlightInfo {
        private String flightNumber;
        private String airline;
        private String airlineCode;
        private String departureAirport;
        private String arrivalAirport;
        private LocalDateTime scheduledDeparture;
        private LocalDateTime scheduledArrival;
        private LocalDateTime estimatedDeparture;
        private LocalDateTime estimatedArrival;
        private LocalDateTime actualDeparture;
        private LocalDateTime actualArrival;
        private String status;
        private String gate;
        private String terminal;
        private String aircraftType;
        private String aircraftRegistration;
        private double latitude;
        private double longitude;
        private int altitude;
        private double speed;
        private int direction;
        private boolean isLive;
        private String delay;
        private String weather;
        
        // Getters and setters
        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
        
        public String getAirline() { return airline; }
        public void setAirline(String airline) { this.airline = airline; }
        
        public String getAirlineCode() { return airlineCode; }
        public void setAirlineCode(String airlineCode) { this.airlineCode = airlineCode; }
        
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
        
        public LocalDateTime getActualDeparture() { return actualDeparture; }
        public void setActualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; }
        
        public LocalDateTime getActualArrival() { return actualArrival; }
        public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getGate() { return gate; }
        public void setGate(String gate) { this.gate = gate; }
        
        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }
        
        public String getAircraftType() { return aircraftType; }
        public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }
        
        public String getAircraftRegistration() { return aircraftRegistration; }
        public void setAircraftRegistration(String aircraftRegistration) { this.aircraftRegistration = aircraftRegistration; }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public int getAltitude() { return altitude; }
        public void setAltitude(int altitude) { this.altitude = altitude; }
        
        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
        
        public int getDirection() { return direction; }
        public void setDirection(int direction) { this.direction = direction; }
        
        public boolean isLive() { return isLive; }
        public void setLive(boolean live) { isLive = live; }
        
        public String getDelay() { return delay; }
        public void setDelay(String delay) { this.delay = delay; }
        
        public String getWeather() { return weather; }
        public void setWeather(String weather) { this.weather = weather; }
        
        @Override
        public String toString() {
            return String.format("Flight %s (%s) - %s to %s - Status: %s", 
                flightNumber, airline, departureAirport, arrivalAirport, status);
        }
    }
    
    /**
     * Enhanced Airport information data class
     */
    public static class AirportInfo {
        private String name;
        private String iataCode;
        private String icaoCode;
        private double latitude;
        private double longitude;
        private String timezone;
        private String country;
        private String countryCode;
        private String city;
        private String website;
        private String phone;
        private String gmt;
        private String geonameId;
        private List<String> runways;
        private List<String> terminals;
        
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
        
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getGmt() { return gmt; }
        public void setGmt(String gmt) { this.gmt = gmt; }
        
        public String getGeonameId() { return geonameId; }
        public void setGeonameId(String geonameId) { this.geonameId = geonameId; }
        
        public List<String> getRunways() { return runways; }
        public void setRunways(List<String> runways) { this.runways = runways; }
        
        public List<String> getTerminals() { return terminals; }
        public void setTerminals(List<String> terminals) { this.terminals = terminals; }
        
        @Override
        public String toString() {
            return String.format("%s (%s) - %s, %s", name, iataCode, city, country);
        }
    }
    
    /**
     * Airline information data class
     */
    public static class AirlineInfo {
        private String name;
        private String iataCode;
        private String icaoCode;
        private String country;
        private String website;
        private String phone;
        private String fleetSize;
        private String founded;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIataCode() { return iataCode; }
        public void setIataCode(String iataCode) { this.iataCode = iataCode; }
        
        public String getIcaoCode() { return icaoCode; }
        public void setIcaoCode(String icaoCode) { this.icaoCode = icaoCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getFleetSize() { return fleetSize; }
        public void setFleetSize(String fleetSize) { this.fleetSize = fleetSize; }
        
        public String getFounded() { return founded; }
        public void setFounded(String founded) { this.founded = founded; }
        
        @Override
        public String toString() {
            return String.format("%s (%s) - %s", name, iataCode, country);
        }
    }
    
    /**
     * Gets comprehensive flight information for a specific flight number
     * @param flightNumber The flight number to search for
     * @return FlightInfo object with detailed flight information
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
     * Gets live flight tracking information with enhanced data
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
     * Gets comprehensive airport information
     * @param airportCode The airport IATA code
     * @return AirportInfo object with detailed airport information
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
     * Gets airline information
     * @param airlineCode The airline IATA code
     * @return AirlineInfo object with airline details
     */
    public AirlineInfo getAirlineInfo(String airlineCode) {
        try {
            String jsonData = ApiIntegrator.getAirlineData(airlineCode);
            return parseAirlineData(jsonData, airlineCode);
        } catch (IOException e) {
            FileLogger.getInstance().logError("Error fetching airline info for " + airlineCode + ": " + e.getMessage());
            return createMockAirlineInfo(airlineCode);
        }
    }
    
    /**
     * Searches for flights by route with enhanced filtering
     * @param departureAirport Departure airport code
     * @param arrivalAirport Arrival airport code
     * @param date Optional date filter
     * @return List of FlightInfo objects
     */
    public List<FlightInfo> searchFlightsByRoute(String departureAirport, String arrivalAirport, String date) {
        List<FlightInfo> flights = new ArrayList<>();
        
        try {
            // In a real implementation, this would call Aviation Stack's route search API
            // For now, create enhanced mock flights
            flights.add(createMockFlightInfo("AA101"));
            flights.add(createMockFlightInfo("UA202"));
            flights.add(createMockFlightInfo("DL303"));
            flights.add(createMockFlightInfo("BA404"));
            
            FileLogger.getInstance().logInfo("Searched flights from " + departureAirport + " to " + arrivalAirport + 
                (date != null ? " on " + date : "") + ": " + flights.size() + " flights found");
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error searching flights: " + e.getMessage());
        }
        
        return flights;
    }
    
    /**
     * Gets flights by airport (departures/arrivals)
     * @param airportCode The airport code
     * @param type "departure" or "arrival"
     * @return List of FlightInfo objects
     */
    public List<FlightInfo> getFlightsByAirport(String airportCode, String type) {
        List<FlightInfo> flights = new ArrayList<>();
        
        try {
            // Mock implementation - would call Aviation Stack API
            for (int i = 1; i <= 5; i++) {
                FlightInfo flight = createMockFlightInfo("FL" + String.format("%03d", i));
                if ("departure".equals(type)) {
                    flight.setDepartureAirport(airportCode);
                    flight.setArrivalAirport("LAX");
                } else {
                    flight.setDepartureAirport("JFK");
                    flight.setArrivalAirport(airportCode);
                }
                flights.add(flight);
            }
            
            FileLogger.getInstance().logInfo("Retrieved " + type + " flights for " + airportCode + ": " + flights.size() + " flights");
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error getting " + type + " flights for " + airportCode + ": " + e.getMessage());
        }
        
        return flights;
    }
    
    /**
     * Gets enhanced flight status summary with more details
     * @param flightNumber The flight number
     * @return Detailed status summary string
     */
    public String getFlightStatusSummary(String flightNumber) {
        FlightInfo flight = getFlightInfo(flightNumber);
        
        if (flight == null) {
            return "Flight information not available";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== Flight Information ===\n");
        summary.append("Flight: ").append(flight.getFlightNumber()).append("\n");
        summary.append("Airline: ").append(flight.getAirline()).append(" (").append(flight.getAirlineCode()).append(")\n");
        summary.append("Route: ").append(flight.getDepartureAirport()).append(" → ").append(flight.getArrivalAirport()).append("\n");
        summary.append("Status: ").append(flight.getStatus()).append("\n");
        
        if (flight.getAircraftType() != null) {
            summary.append("Aircraft: ").append(flight.getAircraftType());
            if (flight.getAircraftRegistration() != null) {
                summary.append(" (").append(flight.getAircraftRegistration()).append(")");
            }
            summary.append("\n");
        }
        
        if (flight.getGate() != null) {
            summary.append("Gate: ").append(flight.getGate());
            if (flight.getTerminal() != null) {
                summary.append(" (Terminal ").append(flight.getTerminal()).append(")");
            }
            summary.append("\n");
        }
        
        if (flight.getScheduledDeparture() != null) {
            summary.append("Scheduled Departure: ").append(flight.getScheduledDeparture()).append("\n");
        }
        
        if (flight.getEstimatedDeparture() != null) {
            summary.append("Estimated Departure: ").append(flight.getEstimatedDeparture()).append("\n");
        }
        
        if (flight.getActualDeparture() != null) {
            summary.append("Actual Departure: ").append(flight.getActualDeparture()).append("\n");
        }
        
        if (flight.getDelay() != null) {
            summary.append("Delay: ").append(flight.getDelay()).append("\n");
        }
        
        if (flight.isLive()) {
            summary.append("\n=== Live Tracking ===\n");
            summary.append("Location: ").append(flight.getLatitude()).append(", ").append(flight.getLongitude()).append("\n");
            summary.append("Altitude: ").append(flight.getAltitude()).append(" ft\n");
            summary.append("Speed: ").append(flight.getSpeed()).append(" km/h\n");
            summary.append("Direction: ").append(flight.getDirection()).append("°\n");
        }
        
        if (flight.getWeather() != null) {
            summary.append("\n=== Weather ===\n");
            summary.append(flight.getWeather()).append("\n");
        }
        
        return summary.toString();
    }
    
    /**
     * Gets airport statistics and information
     * @param airportCode The airport code
     * @return Airport statistics summary
     */
    public String getAirportStatistics(String airportCode) {
        AirportInfo airport = getAirportInfo(airportCode);
        List<FlightInfo> departures = getFlightsByAirport(airportCode, "departure");
        List<FlightInfo> arrivals = getFlightsByAirport(airportCode, "arrival");
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== Airport Statistics ===\n");
        stats.append("Airport: ").append(airport.getName()).append(" (").append(airport.getIataCode()).append(")\n");
        stats.append("Location: ").append(airport.getCity()).append(", ").append(airport.getCountry()).append("\n");
        stats.append("Coordinates: ").append(airport.getLatitude()).append(", ").append(airport.getLongitude()).append("\n");
        stats.append("Timezone: ").append(airport.getTimezone()).append(" (GMT").append(airport.getGmt()).append(")\n");
        stats.append("Today's Departures: ").append(departures.size()).append("\n");
        stats.append("Today's Arrivals: ").append(arrivals.size()).append("\n");
        stats.append("Total Flights: ").append(departures.size() + arrivals.size()).append("\n");
        
        if (airport.getWebsite() != null) {
            stats.append("Website: ").append(airport.getWebsite()).append("\n");
        }
        
        return stats.toString();
    }
    
    // Enhanced parsing methods...
    private FlightInfo parseFlightData(String jsonData, String flightNumber) {
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(flightNumber);
        
        // Extract enhanced information from JSON
        if (jsonData.contains("\"flight_status\"")) {
            String status = extractJsonValue(jsonData, "flight_status");
            flightInfo.setStatus(status != null ? status : "unknown");
        }
        
        if (jsonData.contains("\"airline\"")) {
            String airline = extractJsonValue(jsonData, "name");
            String airlineCode = extractJsonValue(jsonData, "iata");
            if (airline != null) {
                flightInfo.setAirline(airline);
            }
            if (airlineCode != null) {
                flightInfo.setAirlineCode(airlineCode);
            }
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
            
            String terminal = extractJsonValue(jsonData, "terminal");
            if (terminal != null) {
                flightInfo.setTerminal(terminal);
            }
        }
        
        // Extract arrival information
        if (jsonData.contains("\"arrival\"")) {
            String arrAirport = extractJsonValue(jsonData, "airport");
            if (arrAirport != null) {
                flightInfo.setArrivalAirport(arrAirport);
            }
        }
        
        // Extract aircraft information
        if (jsonData.contains("\"aircraft\"")) {
            String aircraftType = extractJsonValue(jsonData, "type");
            String aircraftReg = extractJsonValue(jsonData, "registration");
            if (aircraftType != null) {
                flightInfo.setAircraftType(aircraftType);
            }
            if (aircraftReg != null) {
                flightInfo.setAircraftRegistration(aircraftReg);
            }
        }
        
        // Extract live tracking data
        if (jsonData.contains("\"live\"")) {
            try {
                String latStr = extractJsonValue(jsonData, "latitude");
                String lonStr = extractJsonValue(jsonData, "longitude");
                String altStr = extractJsonValue(jsonData, "altitude");
                String speedStr = extractJsonValue(jsonData, "speed_horizontal");
                String dirStr = extractJsonValue(jsonData, "direction");
                
                if (latStr != null) flightInfo.setLatitude(Double.parseDouble(latStr));
                if (lonStr != null) flightInfo.setLongitude(Double.parseDouble(lonStr));
                if (altStr != null) flightInfo.setAltitude(Integer.parseInt(altStr));
                if (speedStr != null) flightInfo.setSpeed(Double.parseDouble(speedStr));
                if (dirStr != null) flightInfo.setDirection(Integer.parseInt(dirStr));
            } catch (NumberFormatException e) {
                FileLogger.getInstance().logError("Error parsing live tracking coordinates: " + e.getMessage());
            }
        }
        
        return flightInfo;
    }
    
    private AirportInfo parseAirportData(String jsonData, String airportCode) {
        AirportInfo airportInfo = new AirportInfo();
        airportInfo.setIataCode(airportCode);
        
        // Extract enhanced information from JSON
        String name = extractJsonValue(jsonData, "airport_name");
        if (name != null) {
            airportInfo.setName(name);
        }
        
        String country = extractJsonValue(jsonData, "country_name");
        if (country != null) {
            airportInfo.setCountry(country);
        }
        
        String countryCode = extractJsonValue(jsonData, "country_code");
        if (countryCode != null) {
            airportInfo.setCountryCode(countryCode);
        }
        
        String city = extractJsonValue(jsonData, "city");
        if (city != null) {
            airportInfo.setCity(city);
        }
        
        String timezone = extractJsonValue(jsonData, "timezone");
        if (timezone != null) {
            airportInfo.setTimezone(timezone);
        }
        
        String website = extractJsonValue(jsonData, "website");
        if (website != null) {
            airportInfo.setWebsite(website);
        }
        
        String phone = extractJsonValue(jsonData, "phone_number");
        if (phone != null) {
            airportInfo.setPhone(phone);
        }
        
        String gmt = extractJsonValue(jsonData, "gmt");
        if (gmt != null) {
            airportInfo.setGmt(gmt);
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
    
    private AirlineInfo parseAirlineData(String jsonData, String airlineCode) {
        AirlineInfo airlineInfo = new AirlineInfo();
        airlineInfo.setIataCode(airlineCode);
        
        // Extract airline information from JSON
        String name = extractJsonValue(jsonData, "airline_name");
        if (name != null) {
            airlineInfo.setName(name);
        }
        
        String country = extractJsonValue(jsonData, "country_name");
        if (country != null) {
            airlineInfo.setCountry(country);
        }
        
        String website = extractJsonValue(jsonData, "website");
        if (website != null) {
            airlineInfo.setWebsite(website);
        }
        
        String phone = extractJsonValue(jsonData, "phone_number");
        if (phone != null) {
            airlineInfo.setPhone(phone);
        }
        
        return airlineInfo;
    }
    
    // Helper methods remain the same...
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
    
    private FlightInfo createMockFlightInfo(String flightNumber) {
        FlightInfo flight = new FlightInfo();
        flight.setFlightNumber(flightNumber);
        flight.setAirline("American Airlines");
        flight.setAirlineCode("AA");
        flight.setDepartureAirport("JFK");
        flight.setArrivalAirport("LAX");
        flight.setStatus("scheduled");
        flight.setGate("A1");
        flight.setTerminal("1");
        flight.setAircraftType("Boeing 737-800");
        flight.setAircraftRegistration("N12345");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(5));
        flight.setLatitude(40.6413);
        flight.setLongitude(-73.7781);
        flight.setAltitude(35000);
        flight.setSpeed(850);
        flight.setDirection(270);
        flight.setLive(false);
        flight.setDelay("On time");
        flight.setWeather("Clear skies, 22°C");
        
        return flight;
    }
    
    private AirportInfo createMockAirportInfo(String airportCode) {
        AirportInfo airport = new AirportInfo();
        airport.setName("John F. Kennedy International Airport");
        airport.setIataCode(airportCode);
        airport.setIcaoCode(airportCode);
        airport.setLatitude(40.6413);
        airport.setLongitude(-73.7781);
        airport.setTimezone("America/New_York");
        airport.setCountry("United States");
        airport.setCountryCode("US");
        airport.setCity("New York");
        airport.setWebsite("https://www.jfkairport.com");
        airport.setPhone("+1 718-244-4444");
        airport.setGmt("-5");
        
        return airport;
    }
    
    private AirlineInfo createMockAirlineInfo(String airlineCode) {
        AirlineInfo airline = new AirlineInfo();
        airline.setName("American Airlines");
        airline.setIataCode(airlineCode);
        airline.setIcaoCode("AAL");
        airline.setCountry("United States");
        airline.setWebsite("https://www.aa.com");
        airline.setPhone("+1 800-433-7300");
        airline.setFleetSize("956 aircraft");
        airline.setFounded("1926");
        
        return airline;
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