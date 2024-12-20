package org.example.utils;

import org.json.JSONObject;

/**
 * Utility class for extracting and formatting weather data from JSON responses.
 */
public class WeatherUtils {

    /**
     * Extracts the city name from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The city name as a string.
     */
    public static String extractCity(String weatherData) {
        return extractFromJson(weatherData, "location", "name");
    }

    /**
     * Extracts the temperature from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The temperature as an integer (in Celsius).
     */
    public static int extractTemperature(String weatherData) {
        return extractIntFromJson(weatherData, "current", "temperature");
    }

    /**
     * Extracts the humidity percentage from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The humidity as an integer (percentage).
     */
    public static int extractHumidity(String weatherData) {
        return extractIntFromJson(weatherData, "current", "humidity");
    }

    /**
     * Extracts the wind speed from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The wind speed as an integer (in km/h).
     */
    public static int extractWindSpeed(String weatherData) {
        return extractIntFromJson(weatherData, "current", "wind_speed");
    }

    /**
     * Extracts the visibility distance from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The visibility as an integer (in kilometers).
     */
    public static int extractVisibility(String weatherData) {
        return extractIntFromJson(weatherData, "current", "visibility");
    }

    /**
     * Extracts the UV index from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The UV index as an integer.
     */
    public static int extractUVIndex(String weatherData) {
        return extractIntFromJson(weatherData, "current", "uv_index");
    }

    /**
     * Extracts the atmospheric pressure from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return The pressure as an integer (in hPa).
     */
    public static int extractPressure(String weatherData) {
        return extractIntFromJson(weatherData, "current", "pressure");
    }

    /**
     * Extracts the first weather description from the weather data JSON.
     *
     * @param weatherData JSON string containing weather data.
     * @return A string describing the current weather condition.
     */
    public static String extractWeatherDescription(String weatherData) {
        return extractFromJsonArray(weatherData, "current", "weather_descriptions", 0);
    }

    /**
     * Determines if the weather conditions are considered extreme.
     *
     * @param temperature The temperature in Celsius.
     * @param windSpeed The wind speed in km/h.
     * @param humidity The humidity percentage.
     * @return True if conditions are extreme, otherwise false.
     */
    public static boolean isExtremeWeather(int temperature, int windSpeed, int humidity) {
        return temperature < 0 || temperature > 35 || windSpeed > 50 || humidity < 20;
    }

    /**
     * Formats a comprehensive weather message with detailed information.
     *
     * @param city The city name.
     * @param temperature The temperature in Celsius.
     * @param humidity The humidity percentage.
     * @param description A short weather description.
     * @param windSpeed The wind speed in km/h.
     * @param visibility The visibility distance in kilometers.
     * @param uvIndex The UV index.
     * @param pressure The atmospheric pressure in hPa.
     * @return A formatted weather message as a string.
     */
    public static String formatWeatherMessage(String city, int temperature, int humidity, String description, int windSpeed, int visibility, int uvIndex, int pressure) {
        return String.format(
                "Weather in %s: %d°C, %d%% humidity, %s, Wind: %d km/h, Visibility: %d km, UV Index: %d, Pressure: %d hPa.",
                city, temperature, humidity, description, windSpeed, visibility, uvIndex, pressure
        );
    }

    /**
     * Extracts a string value from a nested JSON object.
     *
     * @param weatherData JSON string containing weather data.
     * @param objectKey The key for the parent object.
     * @param fieldKey The key for the field within the object.
     * @return The extracted string value.
     */
    private static String extractFromJson(String weatherData, String objectKey, String fieldKey) {
        JSONObject json = new JSONObject(weatherData);
        return json.getJSONObject(objectKey).getString(fieldKey);
    }

    /**
     * Extracts an integer value from a nested JSON object.
     *
     * @param weatherData JSON string containing weather data.
     * @param objectKey The key for the parent object.
     * @param fieldKey The key for the field within the object.
     * @return The extracted integer value.
     */
    private static int extractIntFromJson(String weatherData, String objectKey, String fieldKey) {
        JSONObject json = new JSONObject(weatherData);
        return json.getJSONObject(objectKey).getInt(fieldKey);
    }

    /**
     * Extracts a string value from a nested JSON array.
     *
     * @param weatherData JSON string containing weather data.
     * @param objectKey The key for the parent object.
     * @param arrayKey The key for the array within the object.
     * @param index The index of the element in the array.
     * @return The extracted string value.
     */
    private static String extractFromJsonArray(String weatherData, String objectKey, String arrayKey, int index) {
        JSONObject json = new JSONObject(weatherData);
        return json.getJSONObject(objectKey).getJSONArray(arrayKey).getString(index);
    }
}
