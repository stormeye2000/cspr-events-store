package com.stormeye.event.store.service.storage;

import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.stormeye.event.service.storage.domain.Block;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class StorageFactoryTest {

    @Autowired
    private StorageFactory storageFactory;

    @Test
    void testBlockAddedStorageFactory() {
        final StorageService<Block> storageService = storageFactory.getStorageService(BlockAdded.class);
        assertThat(storageService.getClass().getSimpleName(), is("BlockAddedService"));
    }
}
