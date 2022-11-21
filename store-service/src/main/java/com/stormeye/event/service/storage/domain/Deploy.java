package com.stormeye.event.service.storage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.conveter.DigestConverter;

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
@Table(name ="DEPLOY",
        indexes = {
        @Index(columnList = "TIMESTAMP"),
        @Index(name = "UKIDXE_DEPLOY_HASH_ACCOUNT", columnList = "DEPLOY_HASH, ACCOUNT", unique = true)
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Deploy extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    @Column(name = "BLOCK_HASH")
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    @Column(name = "ACCOUNT")
    private Digest account;
    @Convert(converter = DigestConverter.class)
    @Column(name = "DEPLOY_HASH")
    private Digest deployHash;
    @Column(name = "COST")
    private BigInteger cost;
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;
    @Column(name = "TIMESTAMP")
    private Date timestamp;
    @Column(name = "EVENT_ID")
    private long eventId;
}
