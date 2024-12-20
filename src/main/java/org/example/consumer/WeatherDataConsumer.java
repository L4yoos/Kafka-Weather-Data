package org.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.example.utils.WeatherUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * WeatherDataConsumer subscribes to Kafka topics to consume and process weather data.
 */
public class WeatherDataConsumer {

    /**
     * Main method that initializes the Kafka consumer, subscribes to topics, and processes messages.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = createKafkaConsumer();
        if (consumer == null) return;

        List<String> topics = Arrays.asList(
                "weather-data-krakow",
                "weather-data-warszawa",
                "weather-data-wroclaw"
        );

        consumer.subscribe(topics);
        System.out.println("Subscribed to topics: " + topics);

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(WeatherDataConsumer::processWeatherData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    /**
     * Creates and configures a Kafka consumer.
     *
     * @return A configured KafkaConsumer instance or null if an error occurs.
     */
    private static KafkaConsumer<String, String> createKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "weather-consumer-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try {
            return new KafkaConsumer<>(props);
        } catch (Exception e) {
            System.err.println("Failed to create Kafka consumer.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Processes a single weather data record from Kafka.
     *
     * @param record The Kafka record containing weather data.
     */
    private static void processWeatherData(ConsumerRecord<String, String> record) {
        try {
            String weatherData = record.value();

            String cityName = WeatherUtils.extractCity(weatherData);
            int temperature = WeatherUtils.extractTemperature(weatherData);
            int humidity = WeatherUtils.extractHumidity(weatherData);
            int windSpeed = WeatherUtils.extractWindSpeed(weatherData);
            int visibility = WeatherUtils.extractVisibility(weatherData);
            int uvIndex = WeatherUtils.extractUVIndex(weatherData);
            int pressure = WeatherUtils.extractPressure(weatherData);
            String weatherDescription = WeatherUtils.extractWeatherDescription(weatherData);

            String formattedMessage = WeatherUtils.formatWeatherMessage(
                    cityName, temperature, humidity, weatherDescription, windSpeed, visibility, uvIndex, pressure
            );

            System.out.println("Processed weather data: " + formattedMessage);

            if (WeatherUtils.isExtremeWeather(temperature, windSpeed, humidity)) {
                System.out.printf("ALERT: Extreme weather detected in %s!%n", cityName);
            }
        } catch (Exception e) {
            System.err.printf("Error processing data from topic: %s | Key: %s%n", record.topic(), record.key());
            e.printStackTrace();
        }
    }
}
