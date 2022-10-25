package com.stormeye.event.service.event;

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

/**
 * @author ian@meywood.com
 */
public class EventInfoDeserializer extends JsonDeserializer<EventInfo> {

    private static Class<?> eventRootClass;
    private static Field dataField;

    static {
        init();
    }


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
        final String type = ((TextNode) node.get("type")).textValue();
        final String source = ((TextNode) node.get("source")).textValue();

        final Long id = getId(node);
        final String dataType = ((TextNode) node.get("dataType")).asText();

        EventData data = null;
        try {

            JsonParser innerParser = node.get("data").traverse();
            innerParser.setCodec(p.getCodec());
            Object eventRoot = p.getCodec().readValue(innerParser, eventRootClass);
            if (eventRoot != null) {
                data = (EventData) dataField.get(eventRoot);
            }

        } catch (IllegalAccessException e) {
            throw new EventServiceException(e);
        }

        final TreeNode versionNode = node.get("version");
        final String version;
        if (versionNode instanceof TextNode) {
            version = ((TextNode) versionNode).asText();
        } else {
            version = null;
        }

        return new EventInfo(id, EventType.valueOf(type.toUpperCase()), source, DataType.of(dataType), version, data);
    }

    @Nullable
    private static Long getId(final TreeNode node) {
        final TreeNode idNode = node.get("id");
        final Long id;
        if (idNode instanceof NumericNode) {
            id = ((NumericNode) idNode).asLong();
        } else {
            id = null;
        }
        return id;
    }
}
