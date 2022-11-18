package com.stormeye.producer.config;

import static java.util.Map.entry;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import com.casper.sdk.model.event.Event;
import com.stormeye.producer.json.CsprEventSerializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configure any beans needed
 * Configurations are split out of the main SpringBoot class
 * to enable individual service testing
 */
@Configuration
public class AppConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.client-id}")
    private String clientId;
    private final ServiceProperties properties;
    private final static int PRODUCER_BYTES = 268435456;

    public AppConfig(@Qualifier("ServiceProperties") final ServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public KafkaProducer<Integer, Event<?>> kafkaProducer(){
        return new KafkaProducer<>(producerConfigs());
    }

    @Bean
    public List<NewTopic> newTopics() {

        return properties.getTopics()
                .stream()
                .map(topic -> TopicBuilder.name(topic.getTopic())
                        .partitions(topic.getPartitions())
                        .replicas(topic.getReplicas())
                        .config(TopicConfig.COMPRESSION_TYPE_CONFIG, topic.getCompression())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Object> producerConfigs() {

        return Map.ofEntries(
                entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                entry(ProducerConfig.CLIENT_ID_CONFIG, clientId),
                entry("buffer.memory", PRODUCER_BYTES),
                entry("max.request.size", PRODUCER_BYTES),
                entry(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 90000),
                entry("acks", "all"),
                entry("enable.idempotence", "true"),
                entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CsprEventSerializer.class)
        );
    }
}
