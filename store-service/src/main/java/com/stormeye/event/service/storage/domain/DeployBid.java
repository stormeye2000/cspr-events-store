package com.stormeye.event.service.storage.domain;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.bid.VestingSchedule;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import com.vladmihalcea.hibernate.type.json.JsonType;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity( name = "bid")
@Table( indexes = {
        @Index(columnList = "validatorPublicKey"),
        @Index(columnList = "deployHash"),
        @Index(columnList = "key"),
        @Index(name = "UKIDXE_VALIDATOR_DEPLOY_HASH_KEY", columnList = "validatorPublicKey, deployHash, key", unique = true)
})
@TypeDef(name = "json", typeClass = JsonType.class)
public class DeployBid extends AbstractPersistable<Long> {

    @Column
    private String key;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest deployHash;
    @Convert(converter = PublicKeyConverter.class)
    @Column
    private PublicKey validatorPublicKey;
    @Column
    private String bondingPurse;
    @Column
    private BigInteger stakedAmount;
    @Column
    private int delegationRate;
    @Column
    private boolean inactive;
    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private VestingSchedule vestingSchedule;
    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private String delegators;
    @Column
    private Date timestamp;
}
