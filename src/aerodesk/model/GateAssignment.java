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
    private LocalDateTime assignedFrom;
    private LocalDateTime assignedTo;
    private LocalDateTime createdAt;
    
    // Default constructor
    public GateAssignment() {}
    
    // Constructor with parameters
    public GateAssignment(int flightId, int gateId, LocalDateTime assignedFrom, LocalDateTime assignedTo) {
        this.flightId = flightId;
        this.gateId = gateId;
        this.assignedFrom = assignedFrom;
        this.assignedTo = assignedTo;
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
    
    public LocalDateTime getAssignedFrom() {
        return assignedFrom;
    }
    
    public void setAssignedFrom(LocalDateTime assignedFrom) {
        this.assignedFrom = assignedFrom;
    }
    
    public LocalDateTime getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(LocalDateTime assignedTo) {
        this.assignedTo = assignedTo;
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
        return !(this.assignedTo.isBefore(other.assignedFrom) || 
                this.assignedFrom.isAfter(other.assignedTo));
    }
    
    /**
     * Checks if this assignment is currently active
     * @return true if the assignment is currently active
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(assignedFrom) && !now.isAfter(assignedTo);
    }
    
    /**
     * Gets the duration of the assignment in minutes
     * @return Duration in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(assignedFrom, assignedTo).toMinutes();
    }
    
    @Override
    public String toString() {
        return String.format("GateAssignment{flightId=%d, gateId=%d, from=%s, to=%s}", 
                           flightId, gateId, assignedFrom, assignedTo);
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