package com.stormeye.event.audit.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * JSON Deserializer for Mongo ObjectId.
 *
 * @author ian@meywood.com
 */
public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(final JsonParser p, final DeserializationContext context) throws IOException {
        return new ObjectId(p.getValueAsString());
    }
}
