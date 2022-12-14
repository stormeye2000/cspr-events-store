package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * Domain object of a reward for a delegator persisted and an end of an era.
 *
 * @author ian@meywood.com
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "UKIDX_DELEGATOR_ERA_ID_TYPE", columnList = "publicKey, validatorPublicKey, eraId", unique = true),
        @Index(columnList = "TIMESTAMP")
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
@SuppressWarnings("java:S2160") // Suppress: Override the "equals" method in this class.
public class DelegatorReward extends Reward {

    @Convert(converter = PublicKeyConverter.class)
    private PublicKey validatorPublicKey;

    public DelegatorReward(final long eraId,
                           final PublicKey publicKey,
                           final PublicKey validatorPublicKey,
                           final BigInteger amount,
                           final Date timestamp) {
        super(eraId, publicKey, amount, timestamp);
        this.validatorPublicKey = validatorPublicKey;
    }
}
