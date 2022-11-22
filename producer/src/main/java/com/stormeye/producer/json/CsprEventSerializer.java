package com.stormeye.producer.json;

import org.apache.kafka.common.serialization.Serializer;
import com.casper.sdk.model.event.Event;

import java.nio.charset.StandardCharsets;

/**
 * Converts an SDK Event to a JSON String representation of that event for sending to Kafka
 *
 * @author ian@meywood.com
 */
public class CsprEventSerializer implements Serializer<Event<String>> {

    private static final String LINE_END = "\",\n";

    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data typed data
     * @return serialized bytes
     */
    @Override
    public byte[] serialize(final String topic, final Event<String> data) {
        return buildKafkaEvent(data).getBytes(StandardCharsets.UTF_8);
    }

    private String buildKafkaEvent(final Event<String> rawEvent) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder("{\n  \"source\":\"")
                .append(rawEvent.getSource()).append(LINE_END)
                .append("  \"type\":\"")
                .append(rawEvent.getEventType().name().toLowerCase())
                .append(LINE_END)
                .append("  \"dataType\":\"")
                .append(rawEvent.getDataType().getDataTypeName())
                .append(LINE_END)
                .append("  \"version\":\"")
                .append(rawEvent.getVersion())
                .append(LINE_END)
                .append(appendId(rawEvent))
                .append(correctJson(rawEvent.getData()))
                .append("\n}")
                .toString();
    }

    /**
     * The CSPR data event is not valid JSON as the data: is not quoted as a key this method resolved that
     *
     * @param data the data to correct
     * @return the corrected data with the data key quoted
     */
    private String correctJson(final String data) {
        return "\"data\"" + data.substring(data.indexOf(':'));
    }

    private String appendId(final Event<String> rawEvent) {

        var builder = new StringBuilder();

        rawEvent.getId().ifPresent(id -> {
            builder.append("  \"id\":");
            builder.append(id);
            builder.append(",\n");
        });

        return builder.toString();
    }
}
