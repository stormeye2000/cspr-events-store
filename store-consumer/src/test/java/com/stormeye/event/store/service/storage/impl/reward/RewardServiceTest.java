package com.stormeye.event.store.service.storage.impl.reward;

import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.repository.ValidatorRewardRepository;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import com.stormeye.event.store.config.CasperServiceConfig;
import com.stormeye.event.store.service.storage.EventInfo;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test for the {@link RewardService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RewardServiceTest {

    private static final String BLOCK_JSON = "/kafka-data/block-added-era-end.json";
    private static final String ERA_INFO_JSON = "/node-rcp-payloads/era-Info-by-switch-bloc.json";

    @Autowired
    private RewardService rewardService;
    @Autowired
    private ValidatorRewardRepository validatorRewardRepository;
    @Autowired
    private DelegatorRewardRepository delegatorRewardRepository;
    @Autowired
    private CasperServiceConfig casperServiceConfig;
    private final MockNode mockNode = new MockNode();

    @BeforeEach
    void setUp() throws IOException {
        delegatorRewardRepository.deleteAll();
        validatorRewardRepository.deleteAll();
        mockNode.start(casperServiceConfig.getNodeUri());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockNode.shutdown();
    }

    @Test
    void createValidatorReward() throws NoSuchAlgorithmException {

        long eraId = 6930L;
        PublicKey publicKey = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");
        BigInteger amount = BigInteger.valueOf(12345);
        Date timestamp = new Date();

        ValidatorReward reward = rewardService.createValidatorReward(eraId, publicKey, amount, timestamp);

        assertThat(reward, is(notNullValue()));
        assertThat(reward.getId(), is(notNullValue()));

        Optional<ValidatorReward> found = rewardService.findValidatorRewardByEraIdAndPublicKey(eraId, publicKey);
        assertThat(found.isPresent(), is(true));
        ValidatorReward foundReward = found.get();
        assertThat(foundReward.getId(), is(reward.getId()));
        assertThat(foundReward.getEraId(), is(eraId));
        assertThat(foundReward.getPublicKey(), is(publicKey));
        assertThat(foundReward.getAmount(), is(amount));
        assertThat(foundReward.getTimestamp().getTime(), is(timestamp.getTime()));
    }

    @Test
    void createDelegatorReward() throws NoSuchAlgorithmException {

        long eraId = 6931L;
        PublicKey publicKey = PublicKey.fromTaggedHexString("01df0e8d79d95a284473e8ea53d5c1f9a569c72e613cac994bfc399f1d9edf8d1c");
        PublicKey validatorPublicKey = PublicKey.fromTaggedHexString("01df0e8d79d95a284473e8ea53d5c1f9a569c72e613cac994bfc399f1d9edf8d1c");
        BigInteger amount = BigInteger.valueOf(12345);
        Date timestamp = new Date();

        DelegatorReward reward = rewardService.createDelegatorReward(eraId, publicKey, validatorPublicKey, amount, timestamp);

        assertThat(reward, is(notNullValue()));
        assertThat(reward.getId(), is(notNullValue()));

        Optional<DelegatorReward> found = rewardService.findDelegatorRewardByEraIdAndPublicKeyAndValidatorPublicKey(eraId, publicKey, validatorPublicKey);
        assertThat(found.isPresent(), is(true));
        DelegatorReward foundReward = found.get();
        assertThat(foundReward.getId(), is(reward.getId()));
        assertThat(foundReward.getEraId(), is(eraId));
        assertThat(foundReward.getPublicKey(), is(publicKey));
        assertThat(foundReward.getValidatorPublicKey(), is(validatorPublicKey));
        assertThat(foundReward.getAmount(), is(amount));
        assertThat(foundReward.getTimestamp().getTime(), is(timestamp.getTime()));
    }

    @Test
    void processRewards() throws IOException, NoSuchAlgorithmException {

        // Serves the Era Info to the Casper SDK
        mockNode.setDispatcher(new Dispatcher() {
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                try {
                    //noinspection ConstantConditions
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody(IOUtils.toString(RewardServiceTest.class.getResource(ERA_INFO_JSON).openStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        EventInfo eventInfo = new ObjectMapper().readValue(RewardServiceTest.class.getResource(BLOCK_JSON), EventInfo.class);
        assertThat(eventInfo, is(notNullValue()));
        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));

        BlockAdded blockAdded = (BlockAdded) eventInfo.getData();
        final long eraId = blockAdded.getBlock().getHeader().getEraId();
        final Date endTimeStamp = DateUtils.fromIso8601("2022-11-08T13:43:09.568Z");
        rewardService.createRewards(blockAdded);

        // Assert the rewards have been added
        assertThat(delegatorRewardRepository.count(), is(8725L));
        assertThat(validatorRewardRepository.count(), is(100L));

        Page<DelegatorReward> delegatorRewards = rewardService.findDelegatorRewardsByEraId(eraId, Pageable.ofSize(10));
        assertThat(delegatorRewards.getNumber(), is(0));
        assertThat(delegatorRewards.getTotalPages(), is(873));
        assertThat(delegatorRewards.getNumberOfElements(), is(10));
        assertThat(delegatorRewards.getTotalElements(), is(8725L));

        DelegatorReward delegatorReward = delegatorRewards.getContent().get(0);
        assertThat(delegatorReward.getEraId(), is(eraId));
        assertThat(delegatorReward.getPublicKey(), is(PublicKey.fromTaggedHexString("014832ab8c1822bca82f75581d0e476ef9833a8536977db8f11324a39544c11dc5")));
        assertThat(delegatorReward.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")));
        assertThat(delegatorReward.getAmount(), is(new BigInteger("52847516")));
        assertThat(delegatorReward.getTimestamp().getTime(), is(endTimeStamp.getTime()));

        // Assert that the validator rewards were added
        Page<ValidatorReward> validatorRewards = rewardService.findValidatorRewardsByEraId(eraId, Pageable.ofSize(10));
        assertThat(validatorRewards.getNumber(), is(0));
        assertThat(validatorRewards.getTotalPages(), is(10));
        assertThat(validatorRewards.getNumberOfElements(), is(10));
        assertThat(validatorRewards.getTotalElements(), is(100L));

        ValidatorReward validatorReward = validatorRewards.getContent().get(0);
        assertThat(validatorReward.getEraId(), is(eraId));
        assertThat(validatorReward.getPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")));
        assertThat(validatorReward.getAmount(), is(new BigInteger("142729414")));
        assertThat(validatorReward.getTimestamp().getTime(), is(endTimeStamp.getTime()));

    }
}
