package com.stormeye.event.store.services.storage.reward.domain;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.store.conveter.PublicKeyConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ian@meywood.com
 */
@Getter
@Setter
@Entity
public class DelegatorReward extends Reward {

    @Column
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey validatorPublicKey;


    public DelegatorReward(long eraId, BigInteger amount, Date timestamp, PublicKey publicKey, PublicKey validatorPublicKey) {
        super(eraId, amount, timestamp, publicKey);
        this.validatorPublicKey = validatorPublicKey;
    }

    public DelegatorReward() {

    }
}
