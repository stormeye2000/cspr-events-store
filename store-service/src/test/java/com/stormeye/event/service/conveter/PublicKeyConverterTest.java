package com.stormeye.event.service.conveter;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.exception.EventServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the  {@link PublicKeyConverter}
 *
 * @author ian@meywood.com
 */
class PublicKeyConverterTest {

    public static final String KEY = "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715";
    private PublicKeyConverter publicKeyConverter;

    @BeforeEach
    void setUp() {
        publicKeyConverter = new PublicKeyConverter();
    }

    @Test
    void convertToDatabaseColumn() throws NoSuchAlgorithmException {
        String converted = publicKeyConverter.convertToDatabaseColumn(PublicKey.fromTaggedHexString(KEY));
        assertThat(converted, is(KEY));
    }

    @Test
    void convertToEntityAttribute() throws NoSuchAlgorithmException {
        PublicKey publicKey = publicKeyConverter.convertToEntityAttribute(KEY);
        assertThat(publicKey, is(PublicKey.fromTaggedHexString(KEY)));
    }

    @Test
    void convertToEntityAttributeWithInvalidLengthKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                publicKeyConverter.convertToEntityAttribute("0" + KEY)
        );

        assertThat(exception.getMessage(), is("hexBinary needs to be even-length: 001018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"));
    }

    @Test
    void convertToEntityAttributeWithInvalidProtocolKey() {
        EventServiceException exception = assertThrows(EventServiceException.class, () ->
                publicKeyConverter.convertToEntityAttribute("91018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")
        );

        assertThat(exception.getMessage(), is("java.security.NoSuchAlgorithmException"));
        assertThat(exception.getCause(), instanceOf(NoSuchAlgorithmException.class));
    }
}
