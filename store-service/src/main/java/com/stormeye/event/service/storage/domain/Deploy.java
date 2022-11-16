package com.stormeye.event.service.storage.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.conveter.DigestConverter;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
        @Index(columnList = "timestamp"),
        @Index(name = "UKIDXE_DEPLOY_HASH_ACCOUNT", columnList = "deployHash, account", unique = true)
})
public class Deploy extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    private Digest deployHash;
    @Convert(converter = DigestConverter.class)
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    private Digest account;
    private BigInteger cost;
    private String errorMessage;
    private Date timestamp;
    private long eventId;

}
