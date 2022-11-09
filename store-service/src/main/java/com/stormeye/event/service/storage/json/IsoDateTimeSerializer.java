package com.stormeye.event.service.storage.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.stormeye.event.utils.DateUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Json serializer to write a date time as and ISO date time
 *
 * @author ian@meywood.com
 */
public class IsoDateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(final Date value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(DateUtils.toIso8601(value));
    }
}
