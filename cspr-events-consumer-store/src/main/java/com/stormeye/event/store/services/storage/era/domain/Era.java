package com.stormeye.event.store.services.storage.era.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author ian@meywood.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(columnList = "endTimestamp"),
        @Index(columnList = "endBlockHeight"),
})
public class Era {

    @Id
    private long id;
    private long endBlockHeight;
    private Date endTimestamp;
    private String protocolVersion;
}
