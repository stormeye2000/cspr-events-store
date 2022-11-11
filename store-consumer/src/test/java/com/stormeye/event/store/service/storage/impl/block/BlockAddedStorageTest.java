package com.stormeye.event.store.service.storage.impl.block;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.*;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import com.stormeye.event.service.storage.domain.Era;
import com.stormeye.event.service.storage.domain.EraValidator;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import com.stormeye.event.store.config.CasperServiceConfig;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.impl.reward.RewardService;
import com.stormeye.event.utils.DateUtils;
import com.stormeye.node.MockNode;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
    private static final String ERA_INFO_JSON = "/node-rcp-payloads/era-Info-by-switch-bloc.json";

    @Autowired
    private BlockAddedService storageService;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private EraRepository eraRepository;
    @Autowired
    private CasperServiceConfig casperServiceConfig;
    @Autowired
    private EraValidatorRepository eraValidatorRepository;
    @Autowired
    private ValidatorRewardRepository validatorRewardRepository;
    @Autowired
    private DelegatorRewardRepository delegatorRewardRepository;
    @Autowired
    private RewardService rewardService;
    private final MockNode mockNode = new MockNode();

    @BeforeEach
    void setUp() throws IOException {
        blockRepository.deleteAll();
        eraValidatorRepository.deleteAll();
        delegatorRewardRepository.deleteAll();
        validatorRewardRepository.deleteAll();

        mockNode.start(casperServiceConfig.getNodeUri());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockNode.shutdown();
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

        // Serves the Era Info to the Casper SDK
        mockNode.setDispatcher(new Dispatcher() {
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                try {
                    //noinspection ConstantConditions
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody(IOUtils.toString(BlockAddedStorageTest.class.getResource(ERA_INFO_JSON).openStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var in = BlockAddedStorageTest.class.getResourceAsStream(BLOCK_ADDED_ERA_END_JSON);
        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);

        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));
        eventInfo.setSource("http://localhost:9999");

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
        assertThat(firstEraValidator.isWasActive(), is(false));

        EraValidator lastEraValidator = content.get(9);
        assertThat(lastEraValidator.getEraId(), is(era.getId() + 1));
        assertThat(lastEraValidator.getPublicKey(), is(PublicKey.fromTaggedHexString("010a6b601408889363dc003943c9234e1bcd9ac074da385c45ff2cd4aa2c9283ce")));
        assertThat(lastEraValidator.getWeight(), is(new BigInteger("8590603094027")));
        assertThat(lastEraValidator.getRewards(), is(BigInteger.ZERO));
        assertThat(lastEraValidator.isWasActive(), is(false));

        // Assert that the validator rewards were added
        Page<ValidatorReward> validatorRewards = rewardService.findValidatorRewardsByEraId(era.getId(), Pageable.ofSize(10));
        assertThat(validatorRewards.getNumber(), is(0));
        assertThat(validatorRewards.getTotalPages(), is(10));
        assertThat(validatorRewards.getTotalElements(), is(100L));

        ValidatorReward validatorReward = validatorRewards.getContent().get(0);
        assertThat(validatorReward.getEraId(), is(era.getId()));
        assertThat(validatorReward.getPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")));
        assertThat(validatorReward.getAmount(), is(new BigInteger("142729414")));
        assertThat(validatorReward.getTimestamp().getTime(), is(endTimeStamp.getTime()));

        // Assert that the delegator rewards were added
        Page<DelegatorReward> delegatorRewards = rewardService.findDelegatorRewardsByEraId(era.getId(), Pageable.ofSize(10));
        assertThat(delegatorRewards.getNumber(), is(0));
        assertThat(delegatorRewards.getTotalPages(), is(873));
        assertThat(delegatorRewards.getTotalElements(), is(8725L));

        DelegatorReward delegatorReward = delegatorRewards.getContent().get(0);
        assertThat(delegatorReward.getEraId(), is(era.getId()));
        assertThat(delegatorReward.getPublicKey(), is(PublicKey.fromTaggedHexString("014832ab8c1822bca82f75581d0e476ef9833a8536977db8f11324a39544c11dc5")));
        assertThat(delegatorReward.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")));
        assertThat(delegatorReward.getAmount(), is(new BigInteger("52847516")));
        assertThat(delegatorReward.getTimestamp().getTime(), is(endTimeStamp.getTime()));
    }
}
