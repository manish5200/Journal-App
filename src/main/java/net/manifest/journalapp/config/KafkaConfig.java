package net.manifest.journalapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfig {

    /**
     * Configures the Kafka listener container factory with manual acknowledgment.
     * This tells Spring Kafka how to manage our consumers.
     *
     * @param consumerFactory The default consumer factory provided by Spring Boot.
     * @return A configured listener factory.
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // --- THIS IS THE KEY ---
        // AckMode.RECORD tells the listener to commit the offset after each message
        // is successfully processed by the @KafkaListener method. If the method
        // throws an exception, the offset is NOT committed, and the message will be re-delivered.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }
}
