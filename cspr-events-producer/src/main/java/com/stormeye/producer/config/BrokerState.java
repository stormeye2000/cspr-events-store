package com.stormeye.producer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.casper.sdk.model.event.Event;

/**
 * Performs a simple query on the kafka broker
 * If there's no connection will throw an exception
 */
@Component
public class BrokerState {

    private final Logger logger = LoggerFactory.getLogger(BrokerState.class.getName());
    private final KafkaProducer<Integer, Event<?>> kafkaProducer;

    public BrokerState(final KafkaProducer<Integer, Event<?>> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public boolean isAvailable() {
        try {
            return !kafkaProducer.metrics().isEmpty();
        } catch (TimeoutException e) {
            logger.error(e.getMessage());
            return false;
        }

    }



}
