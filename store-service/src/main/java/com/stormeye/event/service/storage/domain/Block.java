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

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
@SuppressWarnings("java:S2160") // Suppress: Override the "equals" method in this class.
public class Block extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    private Digest parentHash;
    /** ISO Date */
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date timestamp;
    @Convert(converter = DigestConverter.class)
    private Digest state;
    private long deployCount;
    private long transferCount;
    private long eraId;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey proposer;
    private long blockHeight;
    /** The ID of the event that created this block */
    private long eventId;
}
