package com.stormeye.event.store.service.storage.impl.block;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.repository.EraRepository;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.Era;
import com.stormeye.event.service.storage.domain.EraValidator;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Tests for the {@link BlockAddedService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BlockAddedStorageTest {

    private static final String BLOCK_ADDED_JSON = "/kafka-data/kafka-single-events-main.json";
    private static final String BLOCK_ADDED_ERA_END_JSON = "/kafka-data/block-added-era-end.json";

    @Autowired
    private BlockAddedService storageService;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private EraRepository eraRepository;

    @Autowired
    private EraValidatorRepository eraValidatorRepository;

    @BeforeEach
    void setUp() {
        blockRepository.deleteAll();
    }

    @Test
    void storeBlock() throws IOException, NoSuchAlgorithmException {

        var in = BlockAddedStorageTest.class.getResourceAsStream(BLOCK_ADDED_JSON);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));
        eventInfo.setSource("http://localhost:9999");

        // Save the block added as a block
        var block = storageService.store(eventInfo);
        //assertThat(block, is(notNullValue()));
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


    @Test
    void gracefullyHandleDuplicateEvent() throws IOException {

        var in = BlockAddedStorageTest.class.getResourceAsStream(BLOCK_ADDED_JSON);
        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getId(), is(notNullValue()));
        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));
        eventInfo.setSource("http://localhost:9999");

        // Save the block added as a block
        var block = storageService.store(eventInfo);

        var originalId = block.getId();

        // store the same block again
        block = storageService.store(eventInfo);

        // Assert that the block has not been duplicated
        assertThat(block.getId(), is(originalId));
    }

    @Test
    void eraEndBlockAdded() throws IOException, NoSuchAlgorithmException {

        var in = BlockAddedStorageTest.class.getResourceAsStream(BLOCK_ADDED_ERA_END_JSON);
        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);

        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));
        eventInfo.setSource("http://localhost:9999");

        //CasperService casperService = CasperService.usingPeer("65.21.235.219", 7777);
        // var eraInfo = casperService.getEraInfoBySwitchBlock(new HeightBlockIdentifier(1239489L));

        // Save the block added as a block
        var block = storageService.store(eventInfo);

        final Date endTimeStamp = DateUtils.fromIso8601("2022-11-08T13:43:09.568Z");

        // Assert that the era has been added
        Optional<Era> eraOptional = eraRepository.findById(block.getEraId());

        assertThat(eraOptional.isPresent(), is(true));

        Era era = eraOptional.get();
        assertThat(era.getId(), is(6930L));
        assertThat(era.getEndBlockHeight(), is(1239489L));
        assertThat(era.getProtocolVersion(), is("1.4.8"));
        assertThat(era.getEndTimestamp().getTime(), is(endTimeStamp.getTime()));

        // Assert the Next Era Validators have been added
        Page<EraValidator> byEraId = eraValidatorRepository.findByEraId(era.getId() + 1, Pageable.ofSize(10));

        assertThat(byEraId.getTotalElements(), is(100L));
        assertThat(byEraId.getNumber(), is(0));
        assertThat(byEraId.getTotalPages(), is(10));

        List<EraValidator> content = byEraId.getContent();
        EraValidator firstEraValidator = content.get(0);
        assertThat(firstEraValidator.getEraId(), is(era.getId() + 1));
        assertThat(firstEraValidator.getPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")));
        assertThat(firstEraValidator.getWeight(), is(new BigInteger("3709277043188")));
        assertThat(firstEraValidator.getRewards(), is(BigInteger.ZERO));
        assertThat(firstEraValidator.getWasActive(), is(0));

        EraValidator lastEraValidator = content.get(9);
        assertThat(lastEraValidator.getEraId(), is(era.getId() + 1));
        assertThat(lastEraValidator.getPublicKey(), is(PublicKey.fromTaggedHexString("010a6b601408889363dc003943c9234e1bcd9ac074da385c45ff2cd4aa2c9283ce")));
        assertThat(lastEraValidator.getWeight(), is(new BigInteger("8590603094027")));
        assertThat(lastEraValidator.getRewards(), is(BigInteger.ZERO));
        assertThat(lastEraValidator.getWasActive(), is(0));

    }
}
