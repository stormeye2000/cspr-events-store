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
@Table(indexes = {
        @Index(columnList = "validatorPublicKey"),
        @Index(columnList = "deployHash"),
        @Index(columnList = "timestamp"),
        @Index(columnList = "bidKey")
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
@SuppressWarnings("java:S2160") // Suppress: Override the "equals" method in this class.
public class Bid extends AbstractPersistable<Long> {

    private String bidKey;
    @Convert(converter = DigestConverter.class)
    private Digest deployHash;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey validatorPublicKey;
    private String bondingPurse;
    private BigInteger stakedAmount;
    private int delegationRate;
    private boolean inactive;
    @Column(columnDefinition = "text")
    private String vestingSchedule;
    @Column(columnDefinition = "text")
    private String delegators;
    private Date timestamp;
}
