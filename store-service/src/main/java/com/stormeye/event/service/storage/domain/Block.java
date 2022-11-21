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
@Table(name = "BLOCK",
        indexes = {
                @Index(columnList = "TIMESTAMP"),
                @Index(columnList = "PROPOSER"),
                @Index(columnList = "BLOCK_HEIGHT"),
                @Index(columnList = "ERA_ID"),
                @Index(name = "UKIDX_EVENT_ID_BLOCK_HASH", columnList = "EVENT_ID, BLOCK_HASH", unique = true)
        })
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Block extends AbstractPersistable<Long> {

    @Convert(converter = DigestConverter.class)
    @Column(name = "BLOCK_HASH")
    private Digest blockHash;
    @Convert(converter = DigestConverter.class)
    @Column(name = "PARENT_HASH")
    private Digest parentHash;
    /** ISO Date */
    @Column(name = "TIMESTAMP")
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    private Date timestamp;
    @Column(name = "STATE")
    @Convert(converter = DigestConverter.class)
    private Digest state;
    @Column(name = "DEPLOY_COUNT")
    private long deployCount;
    @Column(name = "TRANSFER_COUNT")
    private long transferCount;
    @Column(name = "ERA_ID")
    private long eraId;
    @Column(name = "PROPOSER")
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey proposer;
    @Column(name = "BLOCK_HEIGHT")
    private long blockHeight;
    /** The ID of the event that created this block */
    @Column(name = "EVENT_ID")
    private long eventId;
}
