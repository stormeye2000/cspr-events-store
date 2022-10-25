package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ian@meywood.com
 */
@Getter
@Setter
@Entity
public abstract class Reward {
    @Id
    private long eraId;
    private BigInteger amount;
    private Date timestamp;
    @Column
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey publicKey;

    Reward(long eraId, BigInteger amount, Date timestamp, PublicKey publicKey) {
        this.eraId = eraId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.publicKey = publicKey;
    }

    public Reward() {
    }
}
