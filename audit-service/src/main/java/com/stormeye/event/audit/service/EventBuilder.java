package com.stormeye.event.audit.service;

import com.stormeye.event.common.EventConstants;

/**
 * Converts an AuditEventInfo and Event Data read from GridFS into an SSE line
 *
 * @author ian@meywood.com
 */
class EventBuilder {

    public static final String EMPTY_EVENT = "\n:\n";

    @SuppressWarnings("java:S3457") // Suppress CERT, FIO47-C. - Use valid format strings
    String buildEvent(final AuditEventInfo event, final String eventData) {

        var builder = new StringBuilder()
                .append(EventConstants.DATA)
                .append(':')
                .append(eventData);

        var id = event.getEventId();
        if (id != null) {
            builder.append(EventConstants.ID)
                    .append(':')
                    .append(id)
                    .append("\n\n");
        }

        return builder.toString();
    }

    String buildVersionEvent(final String version) {
        return String.format("data:{\"ApiVersion\":\"%s\"}\n\n", version);
    }

    String buildEmptyEvent() {
        return EMPTY_EVENT;
    }
}
