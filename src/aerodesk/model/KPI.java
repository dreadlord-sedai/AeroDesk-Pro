package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Key Performance Indicator (KPI) model for dashboard metrics
 * Represents various operational metrics for airport management
 */
public class KPI {
    private String name;
    private String value;
    private String unit;
    private String description;
    private KPIStatus status;
    private LocalDateTime lastUpdated;
    private double percentageChange;
    private String trend;
    
    public enum KPIStatus {
        EXCELLENT("🟢"),
        GOOD("🟡"),
        WARNING("🟠"),
        CRITICAL("🔴"),
        NEUTRAL("⚪");
        
        private final String icon;
        
        KPIStatus(String icon) {
            this.icon = icon;
        }
        
        public String getIcon() {
            return icon;
        }
    }
    
    public KPI() {
        this.lastUpdated = LocalDateTime.now();
        this.status = KPIStatus.NEUTRAL;
    }
    
    public KPI(String name, String value, String unit, String description) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.description = description;
        this.lastUpdated = LocalDateTime.now();
        this.status = KPIStatus.NEUTRAL;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public KPIStatus getStatus() {
        return status;
    }
    
    public void setStatus(KPIStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public double getPercentageChange() {
        return percentageChange;
    }
    
    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }
    
    public String getTrend() {
        return trend;
    }
    
    public void setTrend(String trend) {
        this.trend = trend;
    }
    
    /**
     * Get formatted value with unit
     * @return Value with unit
     */
    public String getFormattedValue() {
        if (unit != null && !unit.isEmpty()) {
            return value + " " + unit;
        }
        return value;
    }
    
    /**
     * Get status icon
     * @return Status icon string
     */
    public String getStatusIcon() {
        return status.getIcon();
    }
    
    /**
     * Get formatted percentage change
     * @return Percentage change with sign
     */
    public String getFormattedPercentageChange() {
        if (percentageChange > 0) {
            return "+" + String.format("%.1f%%", percentageChange);
        } else if (percentageChange < 0) {
            return String.format("%.1f%%", percentageChange);
        }
        return "0.0%";
    }
    
    /**
     * Get trend indicator
     * @return Trend arrow
     */
    public String getTrendIndicator() {
        if (percentageChange > 0) {
            return "↗️";
        } else if (percentageChange < 0) {
            return "↘️";
        }
        return "→";
    }
    
    /**
     * Get display summary
     * @return Complete KPI summary for display
     */
    public String getDisplaySummary() {
        return String.format("%s %s: %s", getStatusIcon(), name, getFormattedValue());
    }
    
    @Override
    public String toString() {
        return String.format("KPI[%s]: %s %s (Status: %s)", 
            name, value, unit, status.name());
    }
} 