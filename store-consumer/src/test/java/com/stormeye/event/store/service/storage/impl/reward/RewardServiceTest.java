package com.stormeye.event.store.service.storage.impl.reward;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RewardServiceTest {

    @Autowired
    private RewardService rewardService;


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
}