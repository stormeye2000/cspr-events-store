package com.stormeye.event.store.conveter;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.store.exceptions.EventConsumerException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.NoSuchAlgorithmException;

/**
 * Database converter for {@link PublicKey} objects.
 *
 * @author ian@meywood.com
 */
@Converter
public class PublicKeyConverter implements AttributeConverter<PublicKey, String> {
    @Override
    public String convertToDatabaseColumn(final PublicKey attribute) {
        return attribute.getAlgoTaggedHex();
    }

    @Override
    public PublicKey convertToEntityAttribute(final String dbData) {
        try {
            return PublicKey.fromTaggedHexString(dbData);
        } catch (NoSuchAlgorithmException e) {
            throw new EventConsumerException(e);
        }
    }
}
