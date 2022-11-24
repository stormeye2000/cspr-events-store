package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigInteger;

/**
 * The Era Validator domain object.
 *
 * @author ian@meywood.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(indexes = {
        @Index(name = "UKIDX_VALIDATOR_ERA_ID", columnList = "publicKey, eraId", unique = true)
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
@SuppressWarnings("java:S2160") // Suppress: Override the "equals" method in this class.
public class EraValidator extends AbstractPersistable<Long> {

    private long eraId;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey publicKey;
    private BigInteger weight;
    private BigInteger rewards;
    private boolean hasEquivocation;
    private boolean wasActive;
}
