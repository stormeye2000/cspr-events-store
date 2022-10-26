package com.stormeye.event.store.service.storage;

/**
 * The interface that is to be implemented by a class that persists an event
 *
 * @param <DomainT> the type of the domain object being persisted
 * @param <EventT>  the type of event being handled that results in a domain object being persisted
 * @author ian@meywood.com
 */
public interface StorageService<EventT, DomainT> {

    /**
     * Stores a domain object
     *
     * @param source  the source of the event
     * @param toStore the optional additional objects to process to create a domain object for
     * @return the stored/updated domain object
     */
    DomainT store(final String source, final EventT toStore);
}
