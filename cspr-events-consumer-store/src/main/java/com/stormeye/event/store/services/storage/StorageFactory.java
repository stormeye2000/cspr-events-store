package com.stormeye.event.store.services.storage;

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

    public void register(final Class dataType, final StorageService<?, ?, ?> storageService) {
        storageServiceMap.put(dataType, storageService);
    }


    public <R, T, V> StorageService<R, T, V> getStorageService(final Class dataType) {
        //noinspection unchecked
        return storageServiceMap.get(dataType);
    }


}
