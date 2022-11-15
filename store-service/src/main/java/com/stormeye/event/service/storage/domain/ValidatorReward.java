package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * Domain object of a reward for a validator persisted and an end of an era.
 *
 * @author ian@meywood.com
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "UKIDX_VALIDATOR_ERA_ID_TYPE", columnList = "publicKey, eraId", unique = true)
})
@DiscriminatorValue("VALIDATOR")
public class ValidatorReward extends Reward {

    public ValidatorReward(final long eraId, final PublicKey publicKey, final BigInteger amount, final Date timestamp) {
        super(eraId, publicKey, amount, timestamp);
    }
}
