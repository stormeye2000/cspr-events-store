package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.ValidatorReward;
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

/**
 * Tests the ValidatorRewardRepository.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ValidatorRewardRepositoryTest {

    @Autowired
    private ValidatorRewardRepository validatorRewardRepository;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

        publicKey = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");

        validatorRewardRepository.save(new ValidatorReward(1234,
                publicKey,
                new BigInteger("10"),
                new Date()
        ));

        validatorRewardRepository.save(new ValidatorReward(1235,
                publicKey,
                new BigInteger("100"),
                new Date()
        ));

        validatorRewardRepository.save(new ValidatorReward(1236,
                publicKey,
                new BigInteger("3"),
                new Date()
        ));

        validatorRewardRepository.save(new ValidatorReward(1236,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716"),
                new BigInteger("1000"),
                new Date()
        ));
    }

    @AfterEach
    void tearDown() {
        validatorRewardRepository.deleteAll();
    }

    @Test
    void findByEraIdAndPublicKey() {
        Optional<ValidatorReward> byEraIdAndPublicKey = validatorRewardRepository.findByEraIdAndPublicKey(1234L, publicKey);
        assertThat(byEraIdAndPublicKey.isPresent(), is(true));
        assertThat(byEraIdAndPublicKey.get().getEraId(), is(1234L));
    }

    @Test
    void findByEraId() throws NoSuchAlgorithmException {
        Page<ValidatorReward> byEraId = validatorRewardRepository.findByEraId(1236L, Pageable.ofSize(2));
        assertThat(byEraId.getTotalPages(), is(1));
        assertThat(byEraId.getNumberOfElements(), is(2));
        assertThat(byEraId.getNumber(), is(0));
        assertThat(byEraId.getContent().get(1).getPublicKey(), is(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716")));
    }

    @Test
    void findByPublicKey() {
        Page<ValidatorReward> byPublicKey = validatorRewardRepository.findByPublicKey(publicKey, Pageable.ofSize(2));
        assertThat(byPublicKey.getTotalPages(), is(2));
        assertThat(byPublicKey.getNumberOfElements(), is(2));
        assertThat(byPublicKey.getNumber(), is(0));
        assertThat(byPublicKey.getContent().get(0).getAmount(), is(BigInteger.valueOf(10L)));
        assertThat(byPublicKey.getContent().get(1).getAmount(), is(BigInteger.valueOf(100L)));

    }

    @Test
    void getTotalRewards() {
        assertThat(validatorRewardRepository.getTotalRewards(publicKey), is(113L));
    }
}