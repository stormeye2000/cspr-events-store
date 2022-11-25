package com.stormeye.event.service.storage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * Domain object for Transfers
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
        @Index(columnList = "blockHash"),
        @Index(columnList = "fromAccount"),
        @Index(columnList = "toAccount"),
        @Index(columnList = "timestamp"),
        @Index(columnList = "transferId")
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
@SuppressWarnings("java:S2160") // Suppress: Override the "equals" method in this class.
public class Transfer extends AbstractPersistable<Long>{

    private BigInteger transferId;
    @Convert(converter = DigestConverter.class)
    private Digest transferHash;
    @Convert(converter = DigestConverter.class)
    private Digest deployHash;
    @Convert(converter = DigestConverter.class)
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    private Digest fromAccount;
    @Convert(converter = DigestConverter.class)
    private Digest toAccount;
    private String sourcePurse;
    private String targetPurse;
    private BigInteger amount;
    private Date timestamp;


}
