package com.stormeye.event.store.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.casper.sdk.model.event.EventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;

/**
 * Consumes Events to Store
 *
 * @author ian@meywood.com
 */
@Service
public class EventsConsumer {

    private final Logger logger = LoggerFactory.getLogger(EventsConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StorageFactory storageFactory;

    public EventsConsumer(final StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
    }

    @KafkaListener(topics = {"main", "deploys", "sigs"})
    public void consumeWithHeaders(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload String event) {

        try {

            var eventInfo = objectMapper.readValue(event, EventInfo.class);
            StorageService<EventData> storageService = storageFactory.getStorageService(eventInfo.getData().getClass());

            if (storageService != null) {
                storageService.store(eventInfo);
            }

        } catch (Exception e) {
            logger.error("Error in topic {} event {}", topic, event, e);
        }
    }
}
