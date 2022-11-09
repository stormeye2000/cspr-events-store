package com.stormeye.event.service.storage.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import com.casper.sdk.model.common.Digest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.storage.json.IsoDateTimeSerializer;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(columnList = "deployHash"),
        @Index(columnList = "blockHash"),
        @Index(columnList = "fromAccount"),
        @Index(columnList = "toAccount"),
        @Index(columnList = "transferId")
})
public class Transfer extends AbstractPersistable<Long> {

    @Column
    private BigInteger transferId;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest transferHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest deployHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest fromAccount;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest toAccount;
    @Column
    private String sourcePurse;
    @Column
    private String targetPurse;
    @Column
    private BigInteger amount;
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    @Column
    private Date timestamp;


}
