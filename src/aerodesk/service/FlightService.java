package aerodesk.service;

import aerodesk.dao.FlightDAO;
import aerodesk.model.Flight;
import aerodesk.exception.DatabaseException;
import aerodesk.util.FileLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Flight operations
 * Handles business logic for flight scheduling
 */
public class FlightService {
    private final FlightDAO flightDAO;
    
    public FlightService() {
        this.flightDAO = new FlightDAO();
    }
    
    /**
     * Creates a new flight with validation
     * @param flight The flight to create
     * @return The created flight with generated ID
     * @throws DatabaseException if database operation fails
     */
    public Flight createFlight(Flight flight) throws DatabaseException {
        validateFlight(flight);
        checkFlightNumberUniqueness(flight.getFlightNo());
        validateFlightTimes(flight);
        
        FileLogger.getInstance().logInfo("Creating flight: " + flight.getFlightNo());
        return flightDAO.createFlight(flight);
    }
    
    /**
     * Updates an existing flight with validation
     * @param flight The flight to update
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateFlight(Flight flight) throws DatabaseException {
        validateFlight(flight);
        validateFlightTimes(flight);
        
        // Check if flight number is unique (excluding current flight)
        Flight existingFlight = flightDAO.getFlightById(flight.getFlightId());
        if (existingFlight != null && !existingFlight.getFlightNo().equals(flight.getFlightNo())) {
            checkFlightNumberUniqueness(flight.getFlightNo());
        }
        
        FileLogger.getInstance().logInfo("Updating flight: " + flight.getFlightNo());
        return flightDAO.updateFlight(flight);
    }
    
    /**
     * Deletes a flight
     * @param flightId The ID of the flight to delete
     * @return true if deletion was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean deleteFlight(int flightId) throws DatabaseException {
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new DatabaseException("Flight not found with ID: " + flightId);
        }
        
        FileLogger.getInstance().logInfo("Deleting flight: " + flight.getFlightNo());
        return flightDAO.deleteFlight(flightId);
    }
    
    /**
     * Retrieves all flights
     * @return List of all flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getAllFlights() throws DatabaseException {
        return flightDAO.getAllFlights();
    }
    
    /**
     * Retrieves a flight by ID
     * @param flightId The flight ID
     * @return Flight object or null if not found
     * @throws DatabaseException if database operation fails
     */
    public Flight getFlightById(int flightId) throws DatabaseException {
        return flightDAO.getFlightById(flightId);
    }
    
    /**
     * Searches flights by flight number
     * @param flightNo The flight number to search for
     * @return List of matching flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> searchFlightsByNumber(String flightNo) throws DatabaseException {
        List<Flight> allFlights = flightDAO.getAllFlights();
        return allFlights.stream()
                .filter(flight -> flight.getFlightNo().toLowerCase().contains(flightNo.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches flights by origin
     * @param origin The origin to search for
     * @return List of matching flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> searchFlightsByOrigin(String origin) throws DatabaseException {
        List<Flight> allFlights = flightDAO.getAllFlights();
        return allFlights.stream()
                .filter(flight -> flight.getOrigin().toLowerCase().contains(origin.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches flights by destination
     * @param destination The destination to search for
     * @return List of matching flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> searchFlightsByDestination(String destination) throws DatabaseException {
        List<Flight> allFlights = flightDAO.getAllFlights();
        return allFlights.stream()
                .filter(flight -> flight.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets flights within a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of flights within the date range
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getFlightsInDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        List<Flight> allFlights = flightDAO.getAllFlights();
        return allFlights.stream()
                .filter(flight -> !flight.getDepartTime().isBefore(startDate) && !flight.getDepartTime().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets flights by status
     * @param status The flight status to filter by
     * @return List of flights with the specified status
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getFlightsByStatus(Flight.FlightStatus status) throws DatabaseException {
        List<Flight> allFlights = flightDAO.getAllFlights();
        return allFlights.stream()
                .filter(flight -> flight.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Updates flight status
     * @param flightId The flight ID
     * @param newStatus The new status
     * @return true if update was successful
     * @throws DatabaseException if database operation fails
     */
    public boolean updateFlightStatus(int flightId, Flight.FlightStatus newStatus) throws DatabaseException {
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new DatabaseException("Flight not found with ID: " + flightId);
        }
        
        flight.setStatus(newStatus);
        FileLogger.getInstance().logInfo("Updated flight " + flight.getFlightNo() + " status to: " + newStatus);
        return flightDAO.updateFlight(flight);
    }
    
    /**
     * Validates flight data
     * @param flight The flight to validate
     * @throws DatabaseException if validation fails
     */
    private void validateFlight(Flight flight) throws DatabaseException {
        if (flight.getFlightNo() == null || flight.getFlightNo().trim().isEmpty()) {
            throw new DatabaseException("Flight number is required");
        }
        
        if (flight.getOrigin() == null || flight.getOrigin().trim().isEmpty()) {
            throw new DatabaseException("Origin is required");
        }
        
        if (flight.getDestination() == null || flight.getDestination().trim().isEmpty()) {
            throw new DatabaseException("Destination is required");
        }
        
        if (flight.getDepartTime() == null) {
            throw new DatabaseException("Departure time is required");
        }
        
        if (flight.getArriveTime() == null) {
            throw new DatabaseException("Arrival time is required");
        }
        
        if (flight.getAircraftType() == null || flight.getAircraftType().trim().isEmpty()) {
            throw new DatabaseException("Aircraft type is required");
        }
        
        if (flight.getStatus() == null) {
            throw new DatabaseException("Flight status is required");
        }
    }
    
    /**
     * Validates flight times
     * @param flight The flight to validate
     * @throws DatabaseException if validation fails
     */
    private void validateFlightTimes(Flight flight) throws DatabaseException {
        if (flight.getDepartTime().isAfter(flight.getArriveTime())) {
            throw new DatabaseException("Departure time cannot be after arrival time");
        }
        
        if (flight.getDepartTime().isBefore(LocalDateTime.now())) {
            throw new DatabaseException("Departure time cannot be in the past");
        }
    }
    
    /**
     * Checks if flight number is unique
     * @param flightNo The flight number to check
     * @throws DatabaseException if flight number already exists
     */
    private void checkFlightNumberUniqueness(String flightNo) throws DatabaseException {
        try {
            List<Flight> existingFlights = searchFlightsByNumber(flightNo);
            for (Flight existingFlight : existingFlights) {
                if (existingFlight.getFlightNo().equalsIgnoreCase(flightNo)) {
                    throw new DatabaseException("Flight number " + flightNo + " already exists");
                }
            }
        } catch (DatabaseException e) {
            throw e;
        }
    }
    
    /**
     * Gets upcoming flights (departing within next 24 hours)
     * @return List of upcoming flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getUpcomingFlights() throws DatabaseException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);
        
        return getFlightsInDateRange(now, tomorrow);
    }
    
    /**
     * Gets delayed flights
     * @return List of delayed flights
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getDelayedFlights() throws DatabaseException {
        return getFlightsByStatus(Flight.FlightStatus.DELAYED);
    }
    
    /**
     * Gets flights departing today
     * @return List of flights departing today
     * @throws DatabaseException if database operation fails
     */
    public List<Flight> getFlightsDepartingToday() throws DatabaseException {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime tomorrow = today.plusDays(1);
        
        return getFlightsInDateRange(today, tomorrow);
    }
} 