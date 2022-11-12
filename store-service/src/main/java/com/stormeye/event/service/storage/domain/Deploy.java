package com.stormeye.event.service.storage.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.common.Digest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.storage.json.IsoDateTimeSerializer;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import lombok.*;

/**
 * Domain object for Deploys
 * Saved as part of the DeployProcessed service
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(indexes = {
        @Index(columnList = "deployHash"),
        @Index(columnList = "account"),
        @Index(name = "UKIDXE_DEPLOY_HASH_ACCOUNT", columnList = "deployHash, account", unique = true)
})
public class Deploy extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    @Column
    private Digest deployHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest account;
    @Column
    private BigInteger cost;
    @Column
    private String errorMessage;
    @Column
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date timestamp;
    @Column
    private long eventId;

}
