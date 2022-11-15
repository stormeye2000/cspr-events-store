package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test the DelegatorRewardRepository.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DelegatorRewardRepositoryTest {

    @Autowired
    private DelegatorRewardRepository delegatorRewardRepository;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

        publicKey = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");
        delegatorRewardRepository.save(new DelegatorReward(12345,
                publicKey,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"),
                BigInteger.ONE,
                new Date())
        );

        delegatorRewardRepository.save(new DelegatorReward(12346,
                publicKey,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"),
                BigInteger.TWO,
                new Date())
        );

        delegatorRewardRepository.save(new DelegatorReward(12347,
                publicKey,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"),
                BigInteger.TEN,
                new Date())
        );

        delegatorRewardRepository.save(new DelegatorReward(12346,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e718"),
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"),
                BigInteger.TEN,
                new Date())
        );
    }

    @AfterEach
    void tearDown() {
        delegatorRewardRepository.deleteAll();
    }

    @Test
    void findByEraIdAndPublicKey() throws NoSuchAlgorithmException {

        Page<DelegatorReward> byEraIdAndPublicKey = delegatorRewardRepository.findByEraIdAndPublicKey(12345, publicKey, Pageable.ofSize(2));
        assertThat(byEraIdAndPublicKey.getTotalElements(), is(1L));
        assertThat(byEraIdAndPublicKey.getTotalPages(), is(1));
        assertThat(byEraIdAndPublicKey.getNumber(), is(0));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getAmount(), is(BigInteger.ONE));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717")));
    }

    @Test
    void findByEraIdAndPublicKeyAndValidatorPublicKey() throws NoSuchAlgorithmException {
        PublicKey validatorPublicKey = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717");
        Optional<DelegatorReward> byEraIdAndPublicKeyAndValidatorPublicKey = delegatorRewardRepository.findByEraIdAndPublicKeyAndValidatorPublicKey(12346, publicKey, validatorPublicKey);
        assertThat(byEraIdAndPublicKeyAndValidatorPublicKey.isPresent(), is(true));
        DelegatorReward delegatorReward = byEraIdAndPublicKeyAndValidatorPublicKey.get();
        assertThat(delegatorReward.getValidatorPublicKey(), is(validatorPublicKey));
        assertThat(delegatorReward.getPublicKey(), is(publicKey));
        assertThat(delegatorReward.getEraId(), is(12346L));
        assertThat(delegatorReward.getAmount(), is(BigInteger.TWO));
        assertThat(delegatorReward.getTimestamp(), is(notNullValue()));
    }

    @Test
    void findByEraId() throws NoSuchAlgorithmException {
        Page<DelegatorReward> byEraIdAndPublicKey = delegatorRewardRepository.findByEraId(12345, Pageable.ofSize(2));
        assertThat(byEraIdAndPublicKey.getTotalElements(), is(1L));
        assertThat(byEraIdAndPublicKey.getTotalPages(), is(1));
        assertThat(byEraIdAndPublicKey.getNumber(), is(0));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getAmount(), is(BigInteger.ONE));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717")));
    }

    @Test
    void findByPublicKey() throws NoSuchAlgorithmException {
        Page<DelegatorReward> byEraIdAndPublicKey = delegatorRewardRepository.findByPublicKey(publicKey, Pageable.ofSize(2));
        assertThat(byEraIdAndPublicKey.getTotalElements(), is(3L));
        assertThat(byEraIdAndPublicKey.getTotalPages(), is(2));
        assertThat(byEraIdAndPublicKey.getNumber(), is(0));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getAmount(), is(BigInteger.ONE));
        assertThat(byEraIdAndPublicKey.getContent().get(0).getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717")));
    }

    @Test
    void getTotalRewards() {
        assertThat(delegatorRewardRepository.getTotalRewards(publicKey), is(BigInteger.valueOf(13L)));
    }
}