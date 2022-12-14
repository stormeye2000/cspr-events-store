package com.stormeye.event.audit.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.stormeye.event.common.EventConstants;

import java.io.IOException;

/**
 * The JSON Deserializer for AuditEventInfo objects
 *
 * @author ian@meywood.com
 */
public class AuditEventInfoDeserializer extends JsonDeserializer<AuditEventInfo> {
    @Override
    public AuditEventInfo deserialize(final JsonParser p, final DeserializationContext context) throws IOException {

        final TreeNode node = p.getCodec().readTree(p);
        final String type = ((TextNode) node.get(EventConstants.TYPE)).textValue();
        final String source = ((TextNode) node.get(EventConstants.SOURCE)).textValue();

        final TreeNode idNode = node.get(EventConstants.ID);
        final Long eventId;
        if (idNode instanceof NumericNode numericNode) {
            eventId = numericNode.asLong();
        } else {
            eventId = null;
        }
        final String dataType = ((TextNode) node.get(EventConstants.DATA_TYPE)).asText();
        final String data = node.get(EventConstants.DATA).toString();

        final TreeNode versionNode = node.get(EventConstants.VERSION);
        final String version;
        if (versionNode instanceof TextNode textNode) {
            version = textNode.asText();
        } else {
            version = null;
        }

        return new AuditEventInfo(null, type, source, dataType, eventId, version, data, data.length());
    }
}
