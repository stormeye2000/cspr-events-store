package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * The abstract base class for all rewards
 * @author ian@meywood.com
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public abstract class Reward extends AbstractPersistable<Long> {

    private long eraId;
    @Column
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey publicKey;
    private BigInteger amount;
    private Date timestamp;

    Reward(long eraId, PublicKey publicKey, BigInteger amount, Date timestamp) {
        this.eraId = eraId;
        this.publicKey = publicKey;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
