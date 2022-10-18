package com.stormeye.producer.service.emitter;

import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.casper.sdk.service.EventService;
import com.stormeye.producer.service.producer.IdStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Simple service to connect to a single event emitter over HTTP connect method will retry n times
 */
@Service
public class EmitterService {

    private final Logger logger = LoggerFactory.getLogger(EmitterService.class);
    private final IdStorageService idStorageService;

    public EmitterService(final IdStorageService idStorageService) {
        this.idStorageService = idStorageService;
    }

    /**
     * Emits raw SSE events from a CSPR node to a kafka stream.
     *
     * @param emitterUri the URL of the casper node to obtain events from
     * @param eventType  the type of the event
     * @param consumer   the consumer of the events that will send to kafka
     */
    @Retryable(maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000L, multiplier = 2, maxDelay = 60000L))
    public void emitEvents(final URI emitterUri, final EventType eventType, final Consumer<Event<?>> consumer) {

        logger.debug("emitterStream for {} {}", emitterUri, eventType);

        EventService.usingPeer(emitterUri).consumeEvents(
                eventType,
                EventTarget.RAW,
                // Start from the last event
                idStorageService.getNextId(emitterUri, eventType),
                consumer
        );
    }

    @SuppressWarnings("unused")
    @Recover
    public void recover(final URI emitter,
                        final EventType eventType,
                        final Consumer<Event<?>> consumer,
                        final Exception e) {
        logger.info("Recovering from ", e);
        emitEvents(emitter, eventType, consumer);
    }

}
