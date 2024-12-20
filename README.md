# Kafka Weather Data Processing Project

This project is a demonstration of how to use Apache Kafka to process weather data in real-time. It includes a producer to fetch weather data from an API, a consumer to process the data, and integration tests to ensure everything works seamlessly. The project uses Docker and Testcontainers for testing and Kafka for real-time data streaming.

---

## **Features**

- Real-time weather data fetching for multiple cities.
- Dynamic topic creation based on city names.
- Detection of extreme weather conditions.
- Comprehensive unit, integration, and load testing.

---

## **Technologies Used**

- **Programming Language**: Java 8
- **Apache Kafka**: Real-time data streaming.
- **Testcontainers**: Containerized integration testing.
- **JUnit**: Unit and integration testing.
- **Docker**: Platform for containerization.
- **Maven**: Dependency management and build tool.
- **Weather API**: WeatherStack API for fetching weather data.

---

## **Project Structure**

```
KafkaWeatherProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── org/example/
│   │   │   │   ├── producer/         # Kafka Producer
│   │   │   │   ├── consumer/         # Kafka Consumer
│   │   │   │   ├── utils/            # Weather utilities
│   │   └── resources/
│   │       └── app.properties        # Configuration
├── tests/
│   ├── java/
│   │   ├── org/example/utils/        # Unit tests
│   │   ├── org/example/integration/  # Integration tests
│   └── resources/
│       └── test.properties           # Test configuration
├── target/                            # Compiled classes and packaged JARs
├── README.md                          # Project documentation
├── pom.xml                            # Maven configuration
```

---

## **Setup Instructions**

### **Prerequisites**

- Java 8 or later
- Docker (with Docker Compose)
- Apache Maven
- Internet access (for WeatherStack API)

### **Steps**

1. Clone the repository:

   ```bash
   git clone https://github.com/your-repo/kafka-weather-project.git
   cd kafka-weather-project
   ```

2. Update the `app.properties` file with your WeatherStack API key and cities:

   ```properties
   weather.api.key=YOUR_API_KEY
   weather.cities=Krakow,Warszawa,Wroclaw
   weather.country=Poland
   ```

3. Build the project:

   ```bash
   mvn clean install
   ```

4. Run the Kafka producer:

   ```bash
   java -cp target/KafkaProject-1.0-SNAPSHOT.jar org.example.producer.WeatherDataProducer
   ```

5. Run the Kafka consumer:

   ```bash
   java -cp target/KafkaProject-1.0-SNAPSHOT.jar org.example.consumer.WeatherDataConsumer
   ```

---

## **Testing**

### **Running Tests**

To run all tests (unit and integration):

```bash
mvn test
```

### **Test Summary**

- **Unit Tests**: Validate the functionality of utility methods in `WeatherUtils`.
- **Integration Tests**: Verify end-to-end functionality using Testcontainers.

### **Load Testing**

Use tools like JMeter or Gatling to simulate high loads for Kafka topics.

---

## **Future Enhancements**

1. **Data Storage**: Save processed weather data to a database (e.g., PostgreSQL).
2. **API**: Expose a REST API for retrieving historical weather data.
3. **Visualization**: Add a frontend for displaying weather trends.
4. **Kafka Streams**: Implement real-time stream processing for aggregation and analytics.
5. **Alerts**: Send notifications (email/SMS) for extreme weather conditions.

