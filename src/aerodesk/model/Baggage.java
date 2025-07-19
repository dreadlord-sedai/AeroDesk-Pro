package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Baggage model class for AeroDesk Pro
 * Represents baggage information and tracking
 */
public class Baggage {
    private int baggageId;
    private int bookingId;
    private double weightKg;
    private BaggageType baggageType;
    private String tagNumber;
    private BaggageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum BaggageType {
        CHECKED, CARRY_ON
    }
    
    public enum BaggageStatus {
        CHECKED_IN, LOADED, DELIVERED, LOST
    }
    
    // Default constructor
    public Baggage() {}
    
    // Constructor with parameters
    public Baggage(int bookingId, double weightKg, BaggageType baggageType, String tagNumber) {
        this.bookingId = bookingId;
        this.weightKg = weightKg;
        this.baggageType = baggageType;
        this.tagNumber = tagNumber;
        this.status = BaggageStatus.LOADED;
    }
    
    // Getters and Setters
    public int getBaggageId() {
        return baggageId;
    }
    
    public void setBaggageId(int baggageId) {
        this.baggageId = baggageId;
    }
    
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public double getWeightKg() {
        return weightKg;
    }
    
    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }
    
    public BaggageType getBaggageType() {
        return baggageType;
    }
    
    public void setBaggageType(BaggageType baggageType) {
        this.baggageType = baggageType;
    }
    
    public String getTagNumber() {
        return tagNumber;
    }
    
    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }
    
    public BaggageStatus getStatus() {
        return status;
    }
    
    public void setStatus(BaggageStatus status) {
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
        return String.format("Baggage{tagNumber='%s', weight=%.1fkg, type=%s, status=%s}", 
                           tagNumber, weightKg, baggageType, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Baggage baggage = (Baggage) obj;
        return baggageId == baggage.baggageId && 
               tagNumber.equals(baggage.tagNumber);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(baggageId, tagNumber);
    }
} 