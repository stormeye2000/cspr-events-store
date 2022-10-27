package com.stormeye.event.audit.service;


import com.casper.sdk.model.event.EventType;
import com.stormeye.event.audit.execption.AuditServiceException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

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
     * @return the stream to the requested events
     */
    public Stream<String> replayAsStream(final EventType eventType, final long startFrom, long maxEvents, Map<String, String> queryMap) {

        var replayContext = new EventReplayContext(eventType, (int) startFrom, maxEvents, queryMap, eventAuditService);

        return Stream.generate(() -> {

            final String line;

            replayContext.throwIfMaxEventsExceeded();

            if (replayContext.hasNext()) {
                line = buildEvent(replayContext.next());
            } else {
                // If there are no events send a colon to the caller and wait for 10 seconds
                line = "\n:\n";
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            return line;
        });
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
