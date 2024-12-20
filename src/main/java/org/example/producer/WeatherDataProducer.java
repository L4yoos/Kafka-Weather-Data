package org.example.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * WeatherDataProducer is responsible for fetching weather data for specified cities from an API
 * and sending the data to Kafka topics.
 */
public class WeatherDataProducer {

    /**
     * Main method that orchestrates the execution of the weather data producer.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Properties appProps = loadAppProperties();
        if (appProps == null) return;

        String apiKey = appProps.getProperty("weather.api.key");
        String country = appProps.getProperty("weather.country", "Poland");
        String cities = appProps.getProperty("weather.cities");

        if (!validateConfig(apiKey, cities)) return;

        List<String> cityList = Arrays.asList(cities.split(","));

        KafkaProducer<String, String> producer = createKafkaProducer();
        if (producer == null) return;

        runWeatherDataProducer(producer, cityList, apiKey, country);
    }

    /**
     * Loads application properties from the `app.properties` file in the resources directory.
     *
     * @return A Properties object containing application settings or null if an error occurs.
     */
    private static Properties loadAppProperties() {
        Properties appProps = new Properties();
        try (InputStream inputStream = WeatherDataProducer.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("app.properties file not found in resources directory.");
            }
            appProps.load(inputStream);
        } catch (Exception e) {
            System.err.println("Failed to load application properties.");
            e.printStackTrace();
            return null;
        }
        return appProps;
    }

    /**
     * Validates that the API key and cities configuration are correctly set.
     *
     * @param apiKey API key for the weather API.
     * @param cities Comma-separated list of city names.
     * @return True if the configuration is valid, otherwise false.
     */
    private static boolean validateConfig(String apiKey, String cities) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is missing in app.properties.");
            return false;
        }

        if (cities == null || cities.isEmpty()) {
            System.err.println("No cities defined in app.properties.");
            return false;
        }
        return true;
    }

    /**
     * Creates and configures a Kafka producer.
     *
     * @return A configured KafkaProducer instance or null if an error occurs.
     */
    private static KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try {
            return new KafkaProducer<>(props);
        } catch (Exception e) {
            System.err.println("Failed to create Kafka producer.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Runs the weather data producer, fetching weather data for each city and sending it to Kafka.
     *
     * @param producer Kafka producer instance.
     * @param cityList List of cities for which to fetch weather data.
     * @param apiKey API key for the weather API.
     * @param country Country associated with the cities.
     */
    private static void runWeatherDataProducer(KafkaProducer<String, String> producer, List<String> cityList, String apiKey, String country) {
        try {
            while (true) {
                for (String city : cityList) {
                    String topic = generateTopicName(city);
                    String apiUrl = buildWeatherApiUrl(apiKey, city.trim(), country);

                    try {
                        String weatherData = fetchWeatherData(apiUrl);
                        sendToKafka(producer, topic, city, weatherData);
                    } catch (Exception e) {
                        System.err.printf("Failed to fetch or send data for city: %s%n", city);
                        e.printStackTrace();
                    }
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Producer interrupted");
        } finally {
            producer.close();
        }
    }

    /**
     * Generates a Kafka topic name for a given city.
     *
     * @param city The city name.
     * @return The topic name formatted as "weather-data-{city}".
     */
    private static String generateTopicName(String city) {
        return "weather-data-" + city.toLowerCase().replaceAll("\\s+", "-");
    }

    /**
     * Builds the weather API URL for a specific city and country.
     *
     * @param apiKey API key for the weather API.
     * @param city The city name.
     * @param country The country name.
     * @return A formatted API URL string.
     */
    private static String buildWeatherApiUrl(String apiKey, String city, String country) {
        return String.format("https://api.weatherstack.com/current?access_key=%s&query=%s,%s", apiKey, city, country);
    }

    /**
     * Fetches weather data from the given API URL.
     *
     * @param apiUrl The weather API URL.
     * @return The weather data in JSON format.
     * @throws Exception if an error occurs while fetching data.
     */
    private static String fetchWeatherData(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            throw new RuntimeException("Failed to fetch data from API: HTTP code " + responseCode);
        }
    }

    /**
     * Sends weather data to a Kafka topic.
     *
     * @param producer Kafka producer instance.
     * @param topic The Kafka topic name.
     * @param city The city name (used as the message key).
     * @param weatherData The weather data to send.
     */
    private static void sendToKafka(KafkaProducer<String, String> producer, String topic, String city, String weatherData) {
        producer.send(new ProducerRecord<>(topic, city, weatherData), (metadata, exception) -> {
            if (exception != null) {
                System.err.printf("Error sending data to Kafka topic %s for city %s: %s%n", topic, city, exception.getMessage());
            } else {
                System.out.printf("Weather data for %s sent to Kafka topic %s at partition %d, offset %d%n", city, topic, metadata.partition(), metadata.offset());
            }
        });
    }
}
