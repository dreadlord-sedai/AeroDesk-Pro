package aerodesk.service;

import aerodesk.dao.BaggageDAO;
import aerodesk.model.Baggage;
import aerodesk.util.FileLogger;
import aerodesk.exception.DatabaseException;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Baggage simulation service for AeroDesk Pro
 * Handles background baggage status updates
 */
public class BaggageSimulator {
    private static BaggageSimulator instance;
    private final BaggageDAO baggageDAO;
    private ScheduledExecutorService executor;
    private boolean isRunning = false;
    
    private BaggageSimulator() {
        this.baggageDAO = new BaggageDAO();
    }
    
    public static BaggageSimulator getInstance() {
        if (instance == null) {
            instance = new BaggageSimulator();
        }
        return instance;
    }
    
    /**
     * Starts the baggage simulation
     */
    public void startSimulation() {
        if (isRunning) {
            return;
        }
        
        isRunning = true;
        executor = Executors.newScheduledThreadPool(1);
        
        // Schedule baggage status updates every 10 seconds
        executor.scheduleAtFixedRate(this::updateBaggageStatus, 0, 10, TimeUnit.SECONDS);
        
        FileLogger.getInstance().logInfo("Baggage simulation started");
    }
    
    /**
     * Stops the baggage simulation
     */
    public void stopSimulation() {
        if (!isRunning) {
            return;
        }
        
        isRunning = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        FileLogger.getInstance().logInfo("Baggage simulation stopped");
    }
    
    /**
     * Updates baggage status in the background
     */
    private void updateBaggageStatus() {
        try {
            List<Baggage> baggageForUpdate = baggageDAO.getBaggageForStatusUpdate();
            
            for (Baggage baggage : baggageForUpdate) {
                // Simulate realistic status progression
                Baggage.BaggageStatus newStatus = getNextStatus(baggage.getStatus());
                
                if (newStatus != baggage.getStatus()) {
                    boolean updated = baggageDAO.updateBaggageStatus(baggage.getBaggageId(), newStatus);
                    
                    if (updated) {
                        FileLogger.getInstance().logInfo("Updated baggage " + baggage.getTagNumber() + 
                                                       " status from " + baggage.getStatus() + " to " + newStatus);
                        
                        // Update UI on EDT
                        SwingUtilities.invokeLater(() -> {
                            // This will be handled by the UI refresh
                        });
                    }
                }
            }
            
        } catch (DatabaseException e) {
            FileLogger.getInstance().logError("Error updating baggage status: " + e.getMessage());
        } catch (Exception e) {
            FileLogger.getInstance().logError("Unexpected error in baggage simulation: " + e.getMessage());
        }
    }
    
    /**
     * Determines the next status for baggage progression
     * @param currentStatus The current status
     * @return The next status in the progression
     */
    private Baggage.BaggageStatus getNextStatus(Baggage.BaggageStatus currentStatus) {
        switch (currentStatus) {
            case CHECKED_IN:
                // 80% chance to move to LOADED, 20% chance to stay CHECKED_IN
                return Math.random() < 0.8 ? Baggage.BaggageStatus.LOADED : Baggage.BaggageStatus.CHECKED_IN;
            case LOADED:
                // 70% chance to move to DELIVERED, 30% chance to stay LOADED
                return Math.random() < 0.7 ? Baggage.BaggageStatus.DELIVERED : Baggage.BaggageStatus.LOADED;
            case DELIVERED:
                // Once delivered, stay delivered
                return Baggage.BaggageStatus.DELIVERED;
            case LOST:
                // Once lost, stay lost
                return Baggage.BaggageStatus.LOST;
            default:
                return Baggage.BaggageStatus.CHECKED_IN;
        }
    }
    
    /**
     * Checks if simulation is currently running
     * @return true if simulation is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Gets the current simulation interval in seconds
     * @return Simulation interval
     */
    public int getSimulationInterval() {
        return 10; // 10 seconds
    }
    
    /**
     * Sets the simulation interval
     * @param intervalSeconds Interval in seconds
     */
    public void setSimulationInterval(int intervalSeconds) {
        if (isRunning) {
            stopSimulation();
            startSimulation();
        }
    }
} 