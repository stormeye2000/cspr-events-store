package com.stormeye.event.store.audit.consumer.config;

import com.stormeye.kafka.AbstractKafkaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka Spring Configuration Class.
 *
 * @author ian@meywood.com
 */
@EnableKafka
@Configuration
public class KafkaConfig extends AbstractKafkaConfig {

    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") final String bootstrapAddress,
                       @Value("${spring.kafka.consumer.client-id:consumer-audit}") final String clientId,
                       @Value("${spring.kafka.consumer.group-id:consumer-audit}") final String groupId) {
        super(bootstrapAddress, clientId, groupId);
    }

}