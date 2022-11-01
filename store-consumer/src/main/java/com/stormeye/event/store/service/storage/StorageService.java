package com.stormeye.event.store.service.storage;

import com.stormeye.event.service.event.EventInfo;

/**
 * The interface that is to be implemented by a class that persists an event
 *
 * @param <DomainT> the type of the domain object being persisted
 * @author ian@meywood.com
 */
public interface StorageService<DomainT> {

    /**
     * Stores a domain object
     *
     * @param eventInfo  the event
     * @return the stored/updated domain object
     */
    DomainT store(final EventInfo eventInfo);
}
