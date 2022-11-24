package com.stormeye.event.store.service.storage;

/**
 * The interface that is to be implemented by a class that persists an event
 *
 * @param <T> the type of the domain object being persisted
 * @author ian@meywood.com
 */
public interface StorageService<T> {

    /**
     * Stores a domain object
     *
     * @param eventInfo  the event
     * @return the stored/updated domain object
     */
    T store(final EventInfo eventInfo);
}
