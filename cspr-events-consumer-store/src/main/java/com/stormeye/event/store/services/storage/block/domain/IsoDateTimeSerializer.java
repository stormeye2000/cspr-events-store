package com.stormeye.event.store.services.storage.block.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

/**
 * Json serializer to write a date time as and ISO date time
 *
 * @author ian@meywood.com
 */
public class IsoDateTimeSerializer extends JsonSerializer<Date> {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(formatter.format(value.toInstant()));
    }
}
