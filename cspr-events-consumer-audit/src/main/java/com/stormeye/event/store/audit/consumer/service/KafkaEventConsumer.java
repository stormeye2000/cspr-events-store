package com.stormeye.event.store.audit.consumer.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * The service that consumes the events from the kafka topics.
 *
 * @author ian@meywood.com
 */
@Service
public class KafkaEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final EventAuditService eventAuditService;

    public KafkaEventConsumer(final EventAuditService eventAuditService) {
        this.eventAuditService = eventAuditService;
    }

    @KafkaListener(topics = {"main", "deploys", "sigs"})
    public void consumeWithHeaders(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload String event) {
        try {
            eventAuditService.save(event);

            logger.debug("Successfully processed topic [{}]: event {}", topic, event);

        } catch (Exception e) {
            logger.error("Error in topic {} event {}", topic, event, e);
        }
    }
}