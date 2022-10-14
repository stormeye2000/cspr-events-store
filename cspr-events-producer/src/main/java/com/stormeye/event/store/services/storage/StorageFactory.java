package com.stormeye.event.store.services.storage;

import com.casper.sdk.model.event.DataType;
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
    private final Map<DataType, StorageService> storageServiceMap = new HashMap<>();

    public void register(final DataType dataType, final StorageService<?,?> storageService) {
        storageServiceMap.put(dataType, storageService);
    }


    public <T, V> StorageService<T, V> getStorageService(final DataType dataType) {
        //noinspection unchecked
        return storageServiceMap.get(dataType);
    }


}
