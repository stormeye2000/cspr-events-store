package com.stormeye.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Manually configured Kafka Producer for testing
 *
 * @author ian@meywood.com
 */
public class DummyProducer {

    private final Producer<Integer, String> producer;

    public DummyProducer(final EmbeddedKafkaBroker kafkaBroker) {
        final Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(kafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer(), new StringSerializer()).createProducer();
    }

    public Future<RecordMetadata> send(final String topic, final int key, final String message) {
        return producer.send(new ProducerRecord<>(topic, key, message));
    }
}
