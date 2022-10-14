package com.stormeye.event.store.services.storage.block;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.store.services.storage.block.repository.BlockRepository;
import com.stormeye.event.store.services.event.EventInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Tests for the {@link BlockAddedStorageService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BlockAddedStorageServiceTest {

    private static final String BLOCK_ADDED_JSON = "/kafka-data/kafka-single-events-main.json";

    @Autowired
    private BlockAddedStorageService storageService;

    @Autowired
    private BlockRepository blockRepository;

    @Test
    void storeBlock() throws IOException, NoSuchAlgorithmException {

        var in = BlockAddedStorageServiceTest.class.getResourceAsStream(BLOCK_ADDED_JSON);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));

        // Save the block added as a block
        var block = storageService.store((BlockAdded) eventInfo.getData());
        assertThat(block, is(notNullValue()));
        assertThat(block.getId(), is(notNullValue()));

        // Load the block from the database
        var foundOptional = blockRepository.findById(block.getId());
        assertThat(foundOptional.isPresent(), is(true));
        block = foundOptional.get();

        // Assert that all fields have been correctly persisted
        assertThat(block.getId(), is(notNullValue()));
        assertThat(block.getBlockHash(), is(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e")));
        assertThat(block.getParentHash(), is(new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47b")));
        assertThat(block.getState(), is(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae")));
        assertThat(block.getEraId(), is(5849L));
        assertThat(block.getProposer(), is(PublicKey.fromTaggedHexString("017d96b9a63abcb61c870a4f55187a0a7ac24096bdb5fc585c12a686a4d892009e")));
        assertThat(block.getDeployCount(), is(0L));
        assertThat(block.getTransferCount(), is(0L));
    }
}