package com.stormeye.event.store.conveter;

import com.casper.sdk.model.common.Digest;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Database attribute converter for {@link Digest} objects.
 *
 * @author ian@meywood.com
 */
@Converter
public class DigestConverter implements AttributeConverter<Digest, String> {
    @Override
    public String convertToDatabaseColumn(final Digest attribute) {
        return attribute.toString();
    }

    @Override
    public Digest convertToEntityAttribute(final String dbData) {
        return new Digest(dbData);
    }
}
