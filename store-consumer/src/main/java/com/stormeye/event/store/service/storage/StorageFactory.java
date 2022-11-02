package com.stormeye.event.store.service.storage;

/**
 * The factory class for event/domain storage services.
 *
 * @author ian@meywood.com
 */
public interface StorageFactory {

    void register(final Class<?> dataType, final StorageService<?> storageService);

    <DomainT> StorageService<DomainT> getStorageService(final Class<?> dataType);


}
