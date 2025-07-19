package aerodesk.service;

import aerodesk.util.FileLogger;
import aerodesk.util.ConfigManager;
import aerodesk.model.Weather;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Weather Service for real-time weather data integration
 * Integrates with OpenWeatherMap API for airport weather information
 */
public class WeatherService {
    
    private static WeatherService instance;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
    private final Map<String, Weather> weatherCache;
    private final Map<String, LocalDateTime> cacheTimestamps;
    private static final int CACHE_DURATION_MINUTES = 10;
    
    private WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = ConfigManager.getInstance().getProperty("openweathermap.api.key", "demo_key");
        this.weatherCache = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
    }
    
    public static WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }
        return instance;
    }
    
    /**
     * Get weather data for a specific airport by coordinates
     * @param latitude Airport latitude
     * @param longitude Airport longitude
     * @param airportCode Airport IATA code for caching
     * @return Weather object with current conditions
     */
    public CompletableFuture<Weather> getWeatherData(double latitude, double longitude, String airportCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check cache first
                if (isCacheValid(airportCode)) {
                    FileLogger.getInstance().logInfo("Weather data retrieved from cache for " + airportCode);
                    return weatherCache.get(airportCode);
                }
                
                // Fetch from API
                String url = String.format("%s?lat=%.4f&lon=%.4f&appid=%s&units=metric", 
                    baseUrl, latitude, longitude, apiKey);
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    Weather weather = parseWeatherResponse(response.body(), airportCode);
                    cacheWeatherData(airportCode, weather);
                    FileLogger.getInstance().logInfo("Weather data fetched for " + airportCode + ": " + weather.getDescription());
                    return weather;
                } else {
                    FileLogger.getInstance().logError("Weather API error for " + airportCode + ": " + response.statusCode());
                    return createMockWeatherData(airportCode);
                }
                
            } catch (Exception e) {
                FileLogger.getInstance().logError("Error fetching weather for " + airportCode + ": " + e.getMessage());
                return createMockWeatherData(airportCode);
            }
        });
    }
    
    /**
     * Get weather data for major airports
     * @return Map of airport codes to weather data
     */
    public CompletableFuture<Map<String, Weather>> getMajorAirportsWeather() {
        Map<String, double[]> airports = new HashMap<>();
        airports.put("JFK", new double[]{40.6413, -73.7781});
        airports.put("LAX", new double[]{33.9416, -118.4085});
        airports.put("LHR", new double[]{51.4700, -0.4543});
        airports.put("CDG", new double[]{49.0097, 2.5479});
        airports.put("NRT", new double[]{35.7720, 140.3929});
        airports.put("SIN", new double[]{1.3644, 103.9915});
        airports.put("DXB", new double[]{25.2532, 55.3657});
        airports.put("HKG", new double[]{22.3080, 113.9185});
        
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Weather> results = new HashMap<>();
            
            airports.forEach((code, coords) -> {
                try {
                    Weather weather = getWeatherData(coords[0], coords[1], code).get();
                    results.put(code, weather);
                } catch (Exception e) {
                    FileLogger.getInstance().logError("Error fetching weather for " + code + ": " + e.getMessage());
                    results.put(code, createMockWeatherData(code));
                }
            });
            
            return results;
        });
    }
    
    private Weather parseWeatherResponse(String jsonResponse, String airportCode) {
        try {
            // Simple JSON parsing (in production, use a proper JSON library)
            Weather weather = new Weather();
            weather.setAirportCode(airportCode);
            weather.setTimestamp(LocalDateTime.now());
            
            // Extract temperature
            int tempIndex = jsonResponse.indexOf("\"temp\":");
            if (tempIndex != -1) {
                int start = jsonResponse.indexOf(":", tempIndex) + 1;
                int end = jsonResponse.indexOf(",", start);
                if (end == -1) end = jsonResponse.indexOf("}", start);
                String tempStr = jsonResponse.substring(start, end).trim();
                weather.setTemperature(Double.parseDouble(tempStr));
            }
            
            // Extract description
            int descIndex = jsonResponse.indexOf("\"description\":");
            if (descIndex != -1) {
                int start = jsonResponse.indexOf("\"", descIndex + 15) + 1;
                int end = jsonResponse.indexOf("\"", start);
                weather.setDescription(jsonResponse.substring(start, end));
            }
            
            // Extract humidity
            int humidityIndex = jsonResponse.indexOf("\"humidity\":");
            if (humidityIndex != -1) {
                int start = jsonResponse.indexOf(":", humidityIndex) + 1;
                int end = jsonResponse.indexOf(",", start);
                if (end == -1) end = jsonResponse.indexOf("}", start);
                String humidityStr = jsonResponse.substring(start, end).trim();
                weather.setHumidity(Integer.parseInt(humidityStr));
            }
            
            // Extract wind speed
            int windIndex = jsonResponse.indexOf("\"speed\":");
            if (windIndex != -1) {
                int start = jsonResponse.indexOf(":", windIndex) + 1;
                int end = jsonResponse.indexOf(",", start);
                if (end == -1) end = jsonResponse.indexOf("}", start);
                String windStr = jsonResponse.substring(start, end).trim();
                weather.setWindSpeed(Double.parseDouble(windStr));
            }
            
            // Set weather icon based on description
            weather.setWeatherIcon(getWeatherIcon(weather.getDescription()));
            
            return weather;
            
        } catch (Exception e) {
            FileLogger.getInstance().logError("Error parsing weather response: " + e.getMessage());
            return createMockWeatherData(airportCode);
        }
    }
    
    private String getWeatherIcon(String description) {
        if (description == null) return "🌤️";
        
        String desc = description.toLowerCase();
        if (desc.contains("clear")) return "☀️";
        if (desc.contains("cloud")) return "☁️";
        if (desc.contains("rain")) return "🌧️";
        if (desc.contains("snow")) return "❄️";
        if (desc.contains("thunder")) return "⛈️";
        if (desc.contains("fog") || desc.contains("mist")) return "🌫️";
        if (desc.contains("wind")) return "💨";
        
        return "🌤️";
    }
    
    private Weather createMockWeatherData(String airportCode) {
        Weather weather = new Weather();
        weather.setAirportCode(airportCode);
        weather.setTimestamp(LocalDateTime.now());
        weather.setTemperature(22.0 + (airportCode.hashCode() % 20));
        weather.setHumidity(60 + (airportCode.hashCode() % 30));
        weather.setWindSpeed(5.0 + (airportCode.hashCode() % 15));
        weather.setDescription("Partly Cloudy");
        weather.setWeatherIcon("🌤️");
        return weather;
    }
    
    private boolean isCacheValid(String airportCode) {
        LocalDateTime timestamp = cacheTimestamps.get(airportCode);
        if (timestamp == null) return false;
        
        return LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES).isBefore(timestamp);
    }
    
    private void cacheWeatherData(String airportCode, Weather weather) {
        weatherCache.put(airportCode, weather);
        cacheTimestamps.put(airportCode, LocalDateTime.now());
    }
    
    /**
     * Clear weather cache
     */
    public void clearCache() {
        weatherCache.clear();
        cacheTimestamps.clear();
        FileLogger.getInstance().logInfo("Weather cache cleared");
    }
    
    /**
     * Get cache statistics
     * @return Cache information
     */
    public String getCacheStats() {
        return String.format("Weather Cache: %d entries, Last update: %s", 
            weatherCache.size(), 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
} 