package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Domain object for a Bid
 * Saved as part of the DeployProcessed service
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "BID",
        indexes = {
                @Index(columnList = "VALIDATOR_PUBLIC_KEY"),
                @Index(columnList = "DEPLOY_HASH"),
                @Index(columnList = "TIMESTAMP"),
                @Index(columnList = "BID_KEY")
        })
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Bid extends AbstractPersistable<Long> {

    @Column(name = "BID_KEY")
    private String bidKey;
    @Convert(converter = DigestConverter.class)
    @Column(name = "DEPLOY_HASH")
    private Digest deployHash;
    @Convert(converter = PublicKeyConverter.class)
    @Column(name = "VALIDATOR_PUBLIC_KEY")
    private PublicKey validatorPublicKey;
    @Column(name = "BONDING_PURSE")
    private String bondingPurse;
    @Column(name = "STAKED_AMOUNT")
    private BigInteger stakedAmount;
    @Column(name = "DELEGATION_RATE")
    private int delegationRate;
    private boolean inactive;
    @Column(name = "VESTING_SCHEDULE", columnDefinition = "text")
    private String vestingSchedule;
    @Column(name = "DELEGATORS", columnDefinition = "text")
    private String delegators;
    @Column(name = "TIMESTAMP")
    private Date timestamp;
}
