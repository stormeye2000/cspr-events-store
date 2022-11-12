package com.stormeye.event.service.storage.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import com.stormeye.event.service.storage.json.IsoDateTimeSerializer;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.*;

/**
 * Domain object for Withdrawals
 * Saved as part of the DeployProcessed service
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table( indexes = {
        @Index(columnList = "validatorPublicKey")
})
public class Withdrawals extends AbstractPersistable<Long> {

    private String withdrawalKey;
    @Convert(converter = DigestConverter.class)
    private Digest deployHash;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey validatorPublicKey;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey ubonderPublicKey;
    private String bondingPurse;
    private BigInteger amount;
    private BigInteger eraOfCreation;
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date timestamp;
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date createdAt;
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date updatedAt;

}
