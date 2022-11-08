package com.stormeye.event.audit.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

/**
 * Factory class for creating the object mapper
 *
 * @author ian@meywood.com
 */
class ObjectMapperFactory {
    static ObjectMapper createObjectMapper() {

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        module.addDeserializer(AuditEventInfo.class, new AuditEventInfoDeserializer());
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());

        final ObjectMapper objectMapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
