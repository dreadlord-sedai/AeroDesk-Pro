package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Gate model class for AeroDesk Pro
 * Represents gate information
 */
public class Gate {
    
    public enum GateStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }
    private int gateId;
    private String gateName;
    private String terminal;
    private GateStatus status;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Gate() {}
    
    // Constructor with parameters
    public Gate(String gateName) {
        this.gateName = gateName;
        this.status = GateStatus.AVAILABLE;
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
    
    public String getTerminal() {
        return terminal;
    }
    
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    
    public GateStatus getStatus() {
        return status;
    }
    
    public void setStatus(GateStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return String.format("Gate{gateName='%s', terminal='%s', status=%s}", gateName, terminal, status);
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