package com.stormeye.event.audit.service;


import com.casper.sdk.model.event.EventType;
import com.stormeye.event.audit.execption.AuditServiceException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The service that replays events from the mongo database
 *
 * @author ian@meywood.com
 */
@Service
public class EventReplayService {


    private final EventAuditService eventAuditService;

    public EventReplayService(final EventAuditService eventAuditService) {
        this.eventAuditService = eventAuditService;
    }

    /**
     * Replays the events as a stream from the specified startFrom event
     *
     * @param eventType the type of events to stream
     * @param startFrom the id of the first event to stream
     * @param maxEvents the maximum number of events to stream, if less than one no maximum is used
     * @param queryMap  the optional map of equality query parameters that match keys and values in a mongo document
     * @param consumer  the consumer of the requested events
     */
    public void replayEvents(final EventType eventType,
                             final long startFrom,
                             long maxEvents,
                             final Map<String, String> queryMap,
                             final Consumer<String> consumer) {

        var replayContext = new EventReplayContext(eventType, (int) startFrom, maxEvents, queryMap, eventAuditService);

        var currentVersion = "1.0.0";
        var writtenVersion = false;

        //noinspection InfiniteLoopStatement
        while (true) {

            final String line;

            replayContext.throwIfMaxEventsExceeded();

            if (replayContext.hasNext()) {

                var next = replayContext.next();

                if (!writtenVersion || !currentVersion.equals(next.getVersion())) {
                    if (next.getVersion() != null) {
                        currentVersion = next.getVersion();
                        consumer.accept(buildVersion(currentVersion));
                        writtenVersion = true;
                    }
                }

                line = buildEvent(next);
            } else {
                // If there are no events send a colon to the caller and wait for 10 seconds
                line = "\n:\n";
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            consumer.accept(line);
        }
    }

    private String buildVersion(final String version) {
        return String.format("data:{\"ApiVersion\":\"%s\"}\n\n", version);
    }

    private String buildEvent(final EventInfo event) {

        var builder = new StringBuilder();
        var eventStream = eventAuditService.findEventStreamById(event.getId());

        if (eventStream.isPresent()) {

            builder.append("data:");
            try {
                builder.append(IOUtils.toString(eventStream.get(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new AuditServiceException("Unable to read JSON from storage", e);
            }

            var id = event.getEventId();
            if (id != null) {
                builder.append("id:");
                builder.append(id);
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }
}
