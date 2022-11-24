package com.stormeye.producer.config;

import com.casper.sdk.model.event.Event;
import com.stormeye.producer.json.CsprEventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Configure any beans needed
 * Configurations are split out of the main SpringBoot class
 * to enable individual service testing
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.client-id}")
    private String clientId;
    private static final int PRODUCER_BYTES = 268435456;

    @Bean
    public KafkaProducer<Integer, Event<String>> kafkaProducer() {
        return new KafkaProducer<>(producerConfigs());
    }

    private Map<String, Object> producerConfigs() {
        return Map.ofEntries(
                entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                entry(ProducerConfig.CLIENT_ID_CONFIG, clientId),
                entry("buffer.memory", PRODUCER_BYTES),
                entry("max.request.size", PRODUCER_BYTES),
                entry(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 90000),
                entry(ProducerConfig.MAX_BLOCK_MS_CONFIG, 90000),
                entry("acks", "all"),
                entry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"),
                entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CsprEventSerializer.class)
        );
    }
}
