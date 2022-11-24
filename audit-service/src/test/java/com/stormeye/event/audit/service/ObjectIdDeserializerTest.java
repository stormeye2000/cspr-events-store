package com.stormeye.event.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for the ObjectIdDeserializer and ObjectIdSerializer
 *
 * @author ian@meywood.com
 */
class ObjectIdDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        module.addDeserializer(AuditEventInfo.class, new AuditEventInfoDeserializer());
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());

        objectMapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();
        objectMapper.registerModule(module);
    }

    @Test
    void serialize() throws JsonProcessingException {

        ObjectId objectId = new ObjectId();
        String json = objectMapper.writeValueAsString(objectId);
        assertThat(json, is(quote(objectId.toHexString())));
    }

    @Test
    void deserialize() throws JsonProcessingException {
        ObjectId objectId = new ObjectId();
        ObjectId deserialized = objectMapper.readValue(quote(objectId.toHexString()), ObjectId.class);
        assertThat(deserialized, is(objectId));
    }

    private String quote(final String toQuote) {
        return "\"" + toQuote + "\"";
    }
}
