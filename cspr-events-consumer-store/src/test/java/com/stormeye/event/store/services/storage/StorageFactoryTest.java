package com.stormeye.event.store.services.storage;

import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.stormeye.event.store.services.storage.block.domain.Block;
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
        final StorageService<BlockAdded, Void, Block> storageService = storageFactory.getStorageService(BlockAdded.class);
        assertThat(storageService.getClass().getSimpleName(), is("BlockAddedStorageService"));
    }
}