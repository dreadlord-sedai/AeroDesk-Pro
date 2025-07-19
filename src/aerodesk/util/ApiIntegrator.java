package aerodesk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * API integration utility for AeroDesk Pro
 * Handles HTTP API calls for external data (weather, flight status, Aviation Stack)
 */
public class ApiIntegrator {
    private static final int TIMEOUT = 15000; // 15 seconds for Aviation Stack
    private static final String USER_AGENT = "AeroDesk-Pro/1.0";
    
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
     * Fetches real-time flight data from Aviation Stack
     * @param flightNumber The flight number (e.g., "AA101")
     * @return Flight data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getFlightData(String flightNumber) throws IOException {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.getProperty("aviationstack.api.key");
        String baseUrl = config.getProperty("aviationstack.api.url");
        
        if (apiKey == null || apiKey.isEmpty() || "your_aviationstack_api_key_here".equals(apiKey)) {
            // Return mock data if no API key is configured
            return getMockFlightData(flightNumber);
        }
        
        String urlString = String.format("%s/flights?access_key=%s&flight_iata=%s", baseUrl, apiKey, flightNumber);
        
        try {
            String response = makeHttpRequest(urlString);
            FileLogger.getInstance().logInfo("Aviation Stack API: Retrieved flight data for " + flightNumber);
            return response;
        } catch (IOException e) {
            FileLogger.getInstance().logError("Aviation Stack API error for flight " + flightNumber + ": " + e.getMessage());
            // Fallback to mock data
            return getMockFlightData(flightNumber);
        }
    }
    
    /**
     * Fetches airport information from Aviation Stack
     * @param airportCode The airport IATA code (e.g., "JFK", "LAX")
     * @return Airport data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getAirportData(String airportCode) throws IOException {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.getProperty("aviationstack.api.key");
        String baseUrl = config.getProperty("aviationstack.api.url");
        
        if (apiKey == null || apiKey.isEmpty() || "your_aviationstack_api_key_here".equals(apiKey)) {
            return getMockAirportData(airportCode);
        }
        
        String urlString = String.format("%s/airports?access_key=%s&iata_code=%s", baseUrl, apiKey, airportCode);
        
        try {
            String response = makeHttpRequest(urlString);
            FileLogger.getInstance().logInfo("Aviation Stack API: Retrieved airport data for " + airportCode);
            return response;
        } catch (IOException e) {
            FileLogger.getInstance().logError("Aviation Stack API error for airport " + airportCode + ": " + e.getMessage());
            return getMockAirportData(airportCode);
        }
    }
    
    /**
     * Fetches live flight tracking data
     * @param flightNumber The flight number
     * @return Live flight tracking data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getLiveFlightTracking(String flightNumber) throws IOException {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.getProperty("aviationstack.api.key");
        String baseUrl = config.getProperty("aviationstack.api.url");
        
        if (apiKey == null || apiKey.isEmpty() || "your_aviationstack_api_key_here".equals(apiKey)) {
            return getMockLiveTrackingData(flightNumber);
        }
        
        String urlString = String.format("%s/flights?access_key=%s&flight_iata=%s&live=1", baseUrl, apiKey, flightNumber);
        
        try {
            String response = makeHttpRequest(urlString);
            FileLogger.getInstance().logInfo("Aviation Stack API: Retrieved live tracking for " + flightNumber);
            return response;
        } catch (IOException e) {
            FileLogger.getInstance().logError("Aviation Stack API error for live tracking " + flightNumber + ": " + e.getMessage());
            return getMockLiveTrackingData(flightNumber);
        }
    }
    
    /**
     * Fetches flight status data (legacy method for backward compatibility)
     * @param flightNumber The flight number
     * @return Flight status data as JSON string
     * @throws IOException if the API call fails
     */
    public static String getFlightStatus(String flightNumber) throws IOException {
        return getFlightData(flightNumber);
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
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept", "application/json");
            
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
                FileLogger.getInstance().logError("API request failed with code: " + responseCode + " for URL: " + urlString);
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
            }""", city, LocalDateTime.now());
    }
    
    /**
     * Returns mock flight data for demonstration purposes
     * @param flightNumber The flight number
     * @return Mock flight data as JSON string
     */
    private static String getMockFlightData(String flightNumber) {
        String[] statuses = {"scheduled", "active", "landed", "cancelled"};
        String randomStatus = statuses[(int) (Math.random() * statuses.length)];
        
        return String.format("""
            {
                "success": true,
                "data": [{
                    "flight": {
                        "number": "%s",
                        "iata": "%s",
                        "icao": "%s"
                    },
                    "departure": {
                        "airport": "John F. Kennedy International Airport",
                        "iata": "JFK",
                        "scheduled": "2024-01-15T10:30:00+00:00",
                        "estimated": "2024-01-15T10:35:00+00:00",
                        "actual": null,
                        "gate": "A1",
                        "terminal": "1"
                    },
                    "arrival": {
                        "airport": "Los Angeles International Airport",
                        "iata": "LAX",
                        "scheduled": "2024-01-15T13:15:00+00:00",
                        "estimated": "2024-01-15T13:20:00+00:00",
                        "actual": null,
                        "gate": "B5",
                        "terminal": "2"
                    },
                    "airline": {
                        "name": "American Airlines",
                        "iata": "AA",
                        "icao": "AAL"
                    },
                    "flight_status": "%s",
                    "live": {
                        "updated": "%s",
                        "latitude": 40.6413,
                        "longitude": -73.7781,
                        "altitude": 35000,
                        "direction": 270,
                        "speed_horizontal": 850,
                        "speed_vertical": 0,
                        "is_ground": false
                    }
                }]
            }""", flightNumber, flightNumber, flightNumber, randomStatus, LocalDateTime.now());
    }
    
    /**
     * Returns mock airport data for demonstration purposes
     * @param airportCode The airport code
     * @return Mock airport data as JSON string
     */
    private static String getMockAirportData(String airportCode) {
        Map<String, String> airports = new HashMap<>();
        airports.put("JFK", "John F. Kennedy International Airport");
        airports.put("LAX", "Los Angeles International Airport");
        airports.put("ORD", "O'Hare International Airport");
        airports.put("ATL", "Hartsfield-Jackson Atlanta International Airport");
        airports.put("DFW", "Dallas/Fort Worth International Airport");
        
        String airportName = airports.getOrDefault(airportCode, "Unknown Airport");
        
        return String.format("""
            {
                "success": true,
                "data": [{
                    "airport_name": "%s",
                    "iata_code": "%s",
                    "icao_code": "%s",
                    "latitude": "40.6413",
                    "longitude": "-73.7781",
                    "geoname_id": "5128581",
                    "timezone": "America/New_York",
                    "gmt": "-5",
                    "phone_number": "+1 718-244-4444",
                    "country_name": "United States",
                    "country_code": "US",
                    "website": "https://www.jfkairport.com"
                }]
            }""", airportName, airportCode, airportCode);
    }
    
    /**
     * Returns mock live tracking data for demonstration purposes
     * @param flightNumber The flight number
     * @return Mock live tracking data as JSON string
     */
    private static String getMockLiveTrackingData(String flightNumber) {
        return String.format("""
            {
                "success": true,
                "data": [{
                    "flight": {
                        "number": "%s",
                        "iata": "%s"
                    },
                    "live": {
                        "updated": "%s",
                        "latitude": 40.6413,
                        "longitude": -73.7781,
                        "altitude": 35000,
                        "direction": 270,
                        "speed_horizontal": 850,
                        "speed_vertical": 0,
                        "is_ground": false
                    }
                }]
            }""", flightNumber, flightNumber, LocalDateTime.now());
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
     * Checks if the Aviation Stack API is available
     * @return true if API is available, false otherwise
     */
    public boolean isAviationStackAvailable() {
        try {
            ConfigManager config = ConfigManager.getInstance();
            String apiKey = config.getProperty("aviationstack.api.key");
            return apiKey != null && !apiKey.isEmpty() && !"your_aviationstack_api_key_here".equals(apiKey);
        } catch (Exception e) {
            return false;
        }
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