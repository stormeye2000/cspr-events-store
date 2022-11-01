package com.stormeye.event.store.service.storage.impl;

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

    private final Map<Class<?>, StorageService<?>> storageServiceMap = new HashMap<>();

    public void register(final Class<?> dataType, final StorageService<?> storageService) {
        storageServiceMap.put(dataType, storageService);
    }

    public <DomainT> StorageService<DomainT> getStorageService(final Class<?> dataType) {
        //noinspection unchecked
        return (StorageService<DomainT>) storageServiceMap.get(dataType);
    }


}
