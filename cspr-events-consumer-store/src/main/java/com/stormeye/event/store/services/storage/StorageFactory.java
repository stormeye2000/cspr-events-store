package com.stormeye.event.store.services.storage;

import com.casper.sdk.model.event.EventData;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * The factory class for event/domain storage services.
 *
 * @author ian@meywood.com
 */
@Service
public class StorageFactory {

    @SuppressWarnings("rawtypes")
    private final Map<Class, StorageService> storageServiceMap = new HashMap<>();

    public void register(final Class dataType, final StorageService<?, ?> storageService) {
        storageServiceMap.put(dataType, storageService);
    }


    public <EventT extends EventData, DomainT> StorageService<EventT, DomainT> getStorageService(final Class<EventT> dataType) {
        //noinspection unchecked
        return storageServiceMap.get(dataType);
    }


}
