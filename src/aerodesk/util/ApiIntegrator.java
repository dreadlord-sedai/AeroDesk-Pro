package aerodesk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * API integration utility for AeroDesk Pro
 * Handles HTTP API calls for external data (weather, flight status)
 */
public class ApiIntegrator {
    private static final int TIMEOUT = 10000; // 10 seconds
    
    /**
     * Fetches weather data for a given city
     * @param city The city name
     * @return Weather data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getWeatherData(String city) throws IOException {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.getProperty("weather.api.key");
        String baseUrl = config.getProperty("weather.api.url");
        
        if (apiKey == null || apiKey.isEmpty() || "your_openweathermap_api_key_here".equals(apiKey)) {
            // Return mock data if no API key is configured
            return getMockWeatherData(city);
        }
        
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String urlString = String.format("%s?q=%s&appid=%s&units=metric", baseUrl, encodedCity, apiKey);
        
        return makeHttpRequest(urlString);
    }
    
    /**
     * Fetches flight status data
     * @param flightNumber The flight number
     * @return Flight status data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getFlightStatus(String flightNumber) throws IOException {
        ConfigManager config = ConfigManager.getInstance();
        String baseUrl = config.getProperty("flight.api.url");
        
        // For demo purposes, return mock data
        return getMockFlightStatus(flightNumber);
    }
    
    /**
     * Makes an HTTP GET request to the specified URL
     * @param urlString The URL to request
     * @return Response as string
     * @throws IOException if the request fails
     */
    private static String makeHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestProperty("User-Agent", "AeroDesk-Pro/1.0");
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                FileLogger.getInstance().logInfo("API request successful: " + urlString);
                return response.toString();
            } else {
                FileLogger.getInstance().logError("API request failed with code: " + responseCode);
                throw new IOException("HTTP request failed with code: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Returns mock weather data for demonstration purposes
     * @param city The city name
     * @return Mock weather data as JSON string
     */
    private static String getMockWeatherData(String city) {
        return String.format("""
            {
                "city": "%s",
                "temperature": 22.5,
                "description": "Partly cloudy",
                "humidity": 65,
                "wind_speed": 12.3,
                "timestamp": "%s"
            }""", city, java.time.LocalDateTime.now());
    }
    
    /**
     * Returns mock flight status data for demonstration purposes
     * @param flightNumber The flight number
     * @return Mock flight status data as JSON string
     */
    private static String getMockFlightStatus(String flightNumber) {
        String[] statuses = {"ON_TIME", "DELAYED", "DEPARTED", "CANCELLED"};
        String randomStatus = statuses[(int) (Math.random() * statuses.length)];
        
        return String.format("""
            {
                "flight_number": "%s",
                "status": "%s",
                "departure_time": "2024-01-15T10:30:00",
                "arrival_time": "2024-01-15T13:15:00",
                "gate": "A1",
                "last_updated": "%s"
            }""", flightNumber, randomStatus, java.time.LocalDateTime.now());
    }
    
    /**
     * Fetches weather data for the default location
     * @return Weather data as string
     * @throws IOException if the API call fails
     */
    public String getWeatherData() throws IOException {
        return getWeatherData("New York"); // Default city
    }
    
    /**
     * Checks if the API is available
     * @return true if API is available, false otherwise
     */
    public boolean isApiAvailable() {
        try {
            // Try to make a simple request to check availability
            getWeatherData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 