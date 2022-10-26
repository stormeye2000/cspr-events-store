package com.stormeye.event.store.service.storage;

import com.casper.sdk.model.event.EventData;

/**
 * The factory class for event/domain storage services.
 *
 * @author ian@meywood.com
 */
public interface StorageFactory {

    void register(final Class<?> dataType, final StorageService<?, ?> storageService);

    <EventT extends EventData, DomainT> StorageService<EventT, DomainT> getStorageService(final Class<EventT> dataType);


}
