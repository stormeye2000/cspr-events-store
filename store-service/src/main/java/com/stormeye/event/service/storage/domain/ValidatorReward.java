package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author ian@meywood.com
 */
@Getter
@Setter
public class ValidatorReward extends Reward {

    public ValidatorReward(final long eraId, final BigInteger amount, final Date timestamp, final PublicKey publicKey) {
        super(eraId, amount, timestamp, publicKey);
    }


    public ValidatorReward() {

    }
}
