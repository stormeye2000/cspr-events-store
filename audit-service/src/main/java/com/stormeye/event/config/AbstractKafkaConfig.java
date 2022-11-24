package com.stormeye.event.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Kafka Spring Configuration Class.
 *
 * @author ian@meywood.com
 */
public abstract class AbstractKafkaConfig {

    private final String bootstrapAddress;
    private final String clientId;
    private final String groupId;

    protected AbstractKafkaConfig(final String bootstrapAddress, final String clientId, final String groupId) {
        this.bootstrapAddress = bootstrapAddress;
        this.clientId = clientId;
        this.groupId = groupId;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        //noinspection SpringConfigurationProxyMethods
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}