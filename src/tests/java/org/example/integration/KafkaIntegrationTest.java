package integration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaIntegrationTest {

    private static KafkaContainer kafkaContainer;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    static void startKafka() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));
        kafkaContainer.start();
    }

    @AfterAll
    static void stopKafka() {
        kafkaContainer.stop();
    }

    @BeforeEach
    void setUp() {
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        consumerProps.put("group.id", "test-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("auto.offset.reset", "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
    }

    @Test
    void testKafkaProducerConsumerIntegration() {
        String topic = "weather-test";
        String key = "Warszawa";
        String value = "{\"temperature\": 20}";

        producer.send(new ProducerRecord<>(topic, key, value));
        producer.flush();

        consumer.subscribe(Collections.singletonList(topic));
        ConsumerRecord<String, String> record = consumer.poll(Duration.ofMillis(1000)).iterator().next();

        assertEquals(key, record.key());
        assertEquals(value, record.value());
    }
}
