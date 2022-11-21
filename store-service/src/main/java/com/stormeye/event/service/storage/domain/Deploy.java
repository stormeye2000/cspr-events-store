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
@Table(indexes = {
        @Index(columnList = "timestamp"),
        @Index(name = "UKIDXE_DEPLOY_HASH_ACCOUNT", columnList = "deployHash, account", unique = true)
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Deploy extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    private Digest account;
    @Convert(converter = DigestConverter.class)
    private Digest deployHash;
    private BigInteger cost;
    private String errorMessage;
    private Date timestamp;
    private long eventId;
}
