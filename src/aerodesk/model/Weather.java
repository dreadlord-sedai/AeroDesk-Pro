package aerodesk.model;

import java.time.LocalDateTime;

/**
 * Weather data model for airport weather information
 * Contains current weather conditions for airports
 */
public class Weather {
    private String airportCode;
    private LocalDateTime timestamp;
    private double temperature;
    private int humidity;
    private double windSpeed;
    private String description;
    private String weatherIcon;
    
    public Weather() {
        this.timestamp = LocalDateTime.now();
    }
    
    public Weather(String airportCode, double temperature, int humidity, double windSpeed, String description) {
        this.airportCode = airportCode;
        this.timestamp = LocalDateTime.now();
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.weatherIcon = "🌤️";
    }
    
    // Getters and Setters
    public String getAirportCode() {
        return airportCode;
    }
    
    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public int getHumidity() {
        return humidity;
    }
    
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    
    public double getWindSpeed() {
        return windSpeed;
    }
    
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getWeatherIcon() {
        return weatherIcon;
    }
    
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
    
    @Override
    public String toString() {
        return String.format("Weather[%s]: %s, %.1f°C, %d%% humidity, %.1f m/s wind", 
            airportCode, description, temperature, humidity, windSpeed);
    }
    
    /**
     * Get formatted temperature string
     * @return Temperature with degree symbol
     */
    public String getFormattedTemperature() {
        return String.format("%.1f°C", temperature);
    }
    
    /**
     * Get formatted wind speed string
     * @return Wind speed with units
     */
    public String getFormattedWindSpeed() {
        return String.format("%.1f m/s", windSpeed);
    }
    
    /**
     * Get formatted humidity string
     * @return Humidity with percentage
     */
    public String getFormattedHumidity() {
        return String.format("%d%%", humidity);
    }
    
    /**
     * Get weather summary for display
     * @return Short weather summary
     */
    public String getWeatherSummary() {
        return String.format("%s %s", weatherIcon, description);
    }
} 