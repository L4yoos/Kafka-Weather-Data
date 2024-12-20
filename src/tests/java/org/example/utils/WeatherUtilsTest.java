package utils;

import org.example.utils.WeatherUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherUtilsTest {

    private final String mockWeatherData = """
        {
            "location": {
                "name": "Warszawa"
            },
            "current": {
                "temperature": 20,
                "humidity": 50,
                "wind_speed": 10,
                "visibility": 8,
                "uv_index": 3,
                "pressure": 1012,
                "weather_descriptions": ["Clear sky"]
            }
        }
        """;

    @Test
    void testExtractCity() {
        String city = WeatherUtils.extractCity(mockWeatherData);
        assertEquals("Warszawa", city);
    }

    @Test
    void testExtractTemperature() {
        int temperature = WeatherUtils.extractTemperature(mockWeatherData);
        assertEquals(20, temperature);
    }
}
