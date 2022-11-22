package com.stormeye.event.service.storage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

/**
 * The domain object that records the start of a new era
 *
 * @author ian@meywood.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(indexes = {
        @Index(columnList = "endTimestamp"),
        @Index(columnList = "endBlockHeight"),
})
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Era implements Persistable<Long> {

    @Id
    private Long id;
    private long endBlockHeight;
    private Date endTimestamp;
    private String protocolVersion;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Era era = (Era) o;
        return id != null && Objects.equals(id, era.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
