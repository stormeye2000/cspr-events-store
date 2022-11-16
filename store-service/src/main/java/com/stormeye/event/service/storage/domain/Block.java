package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stormeye.event.service.conveter.DigestConverter;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import com.stormeye.event.service.storage.json.IsoDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * The domain object for a Block
 *
 * @author ian@meywood.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(indexes = {
        @Index(columnList = "timestamp"),
        @Index(columnList = "proposer"),
        @Index(columnList = "blockHeight"),
        @Index(columnList = "eraId"),
        @Index(name = "UKIDX_EVENT_ID_BLOCK_HASH", columnList = "eventId, blockHash", unique = true)
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Block extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    @Column
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    @Column
    private Digest parentHash;
    /** ISO Date */
    @Column
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date timestamp;
    @Column
    @Convert(converter = DigestConverter.class)
    private Digest state;
    @Column
    private long deployCount;
    @Column
    private long transferCount;
    @Column
    private long eraId;
    @Column
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey proposer;
    @Column
    private long blockHeight;
    /** The ID of the event that created this block */
    @Column
    private long eventId;
}
