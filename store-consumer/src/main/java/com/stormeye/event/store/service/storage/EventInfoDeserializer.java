package com.stormeye.event.store.service.storage;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventData;
import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.stormeye.event.exception.EventServiceException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.stormeye.event.common.EventConstants.*;

/**
 * The JSON Deserializer for EventInfo objects
 *
 * @author ian@meywood.com
 */
public class EventInfoDeserializer extends JsonDeserializer<EventInfo> {

    private static Class<?> eventRootClass;
    private static Field dataField;

    static {
        init();
    }

    @SuppressWarnings("squid:SEC05-J")
    private static void init() {
        try {
            eventRootClass = Class.forName("com.casper.sdk.service.impl.event.EventRoot");
            dataField = eventRootClass.getDeclaredField("data");
            dataField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new EventServiceException(e);
        }
    }

    @Override
    public EventInfo deserialize(final JsonParser p, final DeserializationContext context) throws IOException {

        final TreeNode node = p.getCodec().readTree(p);
        final String type = ((TextNode) node.get(TYPE)).textValue();
        final String source = ((TextNode) node.get(SOURCE)).textValue();
        final Long id = getId(node);
        final String dataType = ((TextNode) node.get(DATA_TYPE)).asText();
        final TreeNode dataNode = node.get(DATA);
        final EventData data = getEventData(dataNode, p);
        final String version = getVersion(node);

        return new EventInfo(
                id,
                EventType.valueOf(type.toUpperCase()),
                source,
                DataType.of(dataType),
                version,
                data,
                dataNode
        );
    }

    @Nullable
    private static Long getId(final TreeNode node) {
        final TreeNode idNode = node.get(ID);
        final Long id;
        if (idNode instanceof NumericNode numericNode) {
            id = numericNode.asLong();
        } else {
            id = null;
        }
        return id;
    }

    @Nullable
    private EventData getEventData(final TreeNode dataNode, final JsonParser p) {
        try {
            final EventData data;
            final JsonParser innerParser = dataNode.traverse();
            innerParser.setCodec(p.getCodec());
            final Object eventRoot = p.getCodec().readValue(innerParser, eventRootClass);
            if (eventRoot != null) {
                data = (EventData) dataField.get(eventRoot);
            } else {
                data = null;
            }
            return data;

        } catch (IllegalAccessException | IOException e) {
            throw new EventServiceException(e);
        }
    }

    @Nullable
    private static String getVersion(TreeNode node) {
        final TreeNode versionNode = node.get(VERSION);
        final String version;
        if (versionNode instanceof TextNode textNode) {
            version = textNode.asText();
        } else {
            version = null;
        }
        return version;
    }

}
