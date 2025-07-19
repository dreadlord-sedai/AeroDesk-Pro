package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Flight model class for AeroDesk Pro
 * Represents flight information in the system
 */
public class Flight {
    private int flightId;
    private String flightNo;
    private String origin;
    private String destination;
    private LocalDateTime departTime;
    private LocalDateTime arriveTime;
    private String aircraftType;
    private FlightStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum FlightStatus {
        SCHEDULED, ON_TIME, DELAYED, DEPARTED, CANCELLED
    }
    
    // Default constructor
    public Flight() {}
    
    // Constructor with parameters
    public Flight(String flightNo, String origin, String destination, 
                  LocalDateTime departTime, LocalDateTime arriveTime, String aircraftType) {
        this.flightNo = flightNo;
        this.origin = origin;
        this.destination = destination;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.aircraftType = aircraftType;
        this.status = FlightStatus.SCHEDULED;
    }
    
    // Getters and Setters
    public int getFlightId() {
        return flightId;
    }
    
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
    
    public String getFlightNo() {
        return flightNo;
    }
    
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDateTime getDepartTime() {
        return departTime;
    }
    
    public void setDepartTime(LocalDateTime departTime) {
        this.departTime = departTime;
    }
    
    public LocalDateTime getArriveTime() {
        return arriveTime;
    }
    
    public void setArriveTime(LocalDateTime arriveTime) {
        this.arriveTime = arriveTime;
    }
    
    public String getAircraftType() {
        return aircraftType;
    }
    
    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }
    
    public FlightStatus getStatus() {
        return status;
    }
    
    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return String.format("Flight{flightNo='%s', origin='%s', destination='%s', status=%s}", 
                           flightNo, origin, destination, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flight flight = (Flight) obj;
        return flightId == flight.flightId && 
               flightNo.equals(flight.flightNo);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(flightId, flightNo);
    }
} 