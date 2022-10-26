package com.stormeye.event.store.service.storage.impl;

import com.casper.sdk.model.event.EventData;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * The factory class for event/domain storage services.
 *
 * @author ian@meywood.com
 */
@Service
class StorageFactoryImpl implements StorageFactory {

    private final Map<Class<?>, StorageService<?,?>> storageServiceMap = new HashMap<>();

    public void register(final Class<?> dataType, final StorageService<?, ?> storageService) {
        storageServiceMap.put(dataType, storageService);
    }

    public <EventT extends EventData, DomainT> StorageService<EventT, DomainT> getStorageService(final Class<EventT> dataType) {
        //noinspection unchecked
        return (StorageService<EventT, DomainT>) storageServiceMap.get(dataType);
    }


}
