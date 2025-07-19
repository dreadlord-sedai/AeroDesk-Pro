package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Booking model class for AeroDesk Pro
 * Represents passenger booking information
 */
public class Booking {
    private int bookingId;
    private int flightId;
    private String passengerName;
    private String passportNo;
    private String seatNo;
    private boolean checkedIn;
    private LocalDateTime checkInTime;
    private String bookingReference;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Booking() {}
    
    // Constructor with parameters
    public Booking(int flightId, String passengerName, String passportNo, String seatNo, String bookingReference) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passportNo = passportNo;
        this.seatNo = seatNo;
        this.bookingReference = bookingReference;
        this.checkedIn = false;
    }
    
    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getFlightId() {
        return flightId;
    }
    
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
    
    public String getPassengerName() {
        return passengerName;
    }
    
    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
    
    public String getPassportNo() {
        return passportNo;
    }
    
    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }
    
    public String getSeatNo() {
        return seatNo;
    }
    
    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }
    
    public boolean isCheckedIn() {
        return checkedIn;
    }
    
    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public String getBookingReference() {
        return bookingReference;
    }
    
    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return String.format("Booking{bookingRef='%s', passenger='%s', flightId=%d, checkedIn=%s}", 
                           bookingReference, passengerName, flightId, checkedIn);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId && 
               bookingReference.equals(booking.bookingReference);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(bookingId, bookingReference);
    }
} 