package com.stormeye.event.store.audit.consumer.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * JSON Serializer for Mongo ObjectId.
 *
 * @author ian@meywood.com
 */
public class ObjectIdSerializer extends JsonSerializer<ObjectId> {
    @Override
    public void serialize(final ObjectId value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toHexString());
    }
}
