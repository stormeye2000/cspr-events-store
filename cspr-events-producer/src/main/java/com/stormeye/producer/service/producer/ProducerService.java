package com.stormeye.producer.service.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventType;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.exceptions.EmitterStoppedException;
import com.stormeye.producer.service.emitter.EmitterService;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Service to start the kafka producer
 * Each emitter will be tested connected via the retry template
 * Each emitter from the properties file then runs in its own thread
 */
@Service
public class ProducerService {

    private final Logger logger = LoggerFactory.getLogger(ProducerService.class.getName());

    private final ServiceProperties properties;
    private final EmitterService emitterService;
    private final KafkaProducer<Integer, Event<?>> kafkaProducer;
    private final IdStorageService idStorageService;

    public ProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties,
                           final EmitterService emitterService, final IdStorageService idStorageService, final KafkaProducer<Integer, Event<?>> kafkaProducer) {
        this.properties = properties;
        this.emitterService = emitterService;
        this.idStorageService = idStorageService;
        this.kafkaProducer = kafkaProducer;
    }

    public void startEventConsumers() {

        try {

            var executor = Executors.newCachedThreadPool();

            properties.getEmitters().forEach(
                    emitter ->
                            Arrays.stream(EventType.values()).forEach(eventType -> {
                                logger.info("Starting kafka producer for casper event [{}] emitter: [{}]", eventType, emitter);
                                executor.submit(() -> {
                                    try {
                                        emitterService.emitEvents(emitter, eventType, event -> sendEvent(emitter, event));
                                    } catch (Exception e) {
                                        throw new EmitterStoppedException(e.getMessage());
                                    }
                                });
                            })
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    void sendEvent(final URI emitter, final Event<?> event) {

        var topic = event.getEventType().name().toLowerCase();

        logger.debug("Emitter: [{}] Topic: [{}] - Event : [{}]", emitter, topic, event);

        kafkaProducer.send(new ProducerRecord<>(topic, 0, System.currentTimeMillis(), null, event), (metadata, exception) -> {
            if (exception != null) {
                logger.error("Error producing event - Metadata: [{}]", metadata, exception);
            } else {
                logger.debug("Succesfully sent event to Topic: [{}]  Partition: [{}]  Offset: [{}]", metadata.topic(), metadata.partition(), metadata.offset());
            }
        });

        // Persist the ID of the event for playback
        event.getId().ifPresent(id -> idStorageService.setCurrentEvent(emitter, event.getEventType(), id));

    }


}
