package aerodesk.exception;

/**
 * Custom exception for gate scheduling conflicts in AeroDesk Pro
 */
public class GateConflictException extends Exception {
    
    public GateConflictException(String message) {
        super(message);
    }
    
    public GateConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GateConflictException(Throwable cause) {
        super(cause);
    }
} 