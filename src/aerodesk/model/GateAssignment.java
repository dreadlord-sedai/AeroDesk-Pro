package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Gate Assignment model class for AeroDesk Pro
 * Represents flight-gate scheduling information
 */
public class GateAssignment {
    private int assignmentId;
    private int flightId;
    private int gateId;
    private LocalDateTime assignmentTime;
    private LocalDateTime departureTime;
    private LocalDateTime createdAt;
    
    // Default constructor
    public GateAssignment() {}
    
    // Constructor with parameters
    public GateAssignment(int flightId, int gateId, LocalDateTime assignmentTime, LocalDateTime departureTime) {
        this.flightId = flightId;
        this.gateId = gateId;
        this.assignmentTime = assignmentTime;
        this.departureTime = departureTime;
    }
    
    // Getters and Setters
    public int getAssignmentId() {
        return assignmentId;
    }
    
    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }
    
    public int getFlightId() {
        return flightId;
    }
    
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
    
    public int getGateId() {
        return gateId;
    }
    
    public void setGateId(int gateId) {
        this.gateId = gateId;
    }
    
    public LocalDateTime getAssignmentTime() {
        return assignmentTime;
    }
    
    public void setAssignmentTime(LocalDateTime assignmentTime) {
        this.assignmentTime = assignmentTime;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Checks if this assignment conflicts with another assignment
     * @param other The other assignment to check against
     * @return true if there's a conflict
     */
    public boolean conflictsWith(GateAssignment other) {
        if (this.gateId != other.gateId) {
            return false; // Different gates, no conflict
        }
        
        // Check for time overlap
        return !(this.departureTime.isBefore(other.assignmentTime) || 
                this.assignmentTime.isAfter(other.departureTime));
    }
    
    /**
     * Checks if this assignment is currently active
     * @return true if the assignment is currently active
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(assignmentTime) && !now.isAfter(departureTime);
    }
    
    /**
     * Gets the duration of the assignment in minutes
     * @return Duration in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(assignmentTime, departureTime).toMinutes();
    }
    
    @Override
    public String toString() {
        return String.format("GateAssignment{flightId=%d, gateId=%d, assignmentTime=%s, departureTime=%s}", 
                           flightId, gateId, assignmentTime, departureTime);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GateAssignment that = (GateAssignment) obj;
        return assignmentId == that.assignmentId;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(assignmentId);
    }
} 