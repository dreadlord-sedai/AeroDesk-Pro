package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Gate model class for AeroDesk Pro
 * Represents gate information
 */
public class Gate {
    private int gateId;
    private String gateName;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Gate() {}
    
    // Constructor with parameters
    public Gate(String gateName) {
        this.gateName = gateName;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getGateId() {
        return gateId;
    }
    
    public void setGateId(int gateId) {
        this.gateId = gateId;
    }
    
    public String getGateName() {
        return gateName;
    }
    
    public void setGateName(String gateName) {
        this.gateName = gateName;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return String.format("Gate{gateName='%s', active=%s}", gateName, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Gate gate = (Gate) obj;
        return gateId == gate.gateId && 
               gateName.equals(gate.gateName);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(gateId, gateName);
    }
} 