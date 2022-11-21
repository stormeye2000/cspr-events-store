package com.stormeye.event.store.service.storage.impl.era;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.EraValidator;
import com.stormeye.event.common.TransactionalRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@link EraValidatorService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraValidatorServiceTest {

    @Autowired
    private EraValidatorService eraValidatorService;

    @Autowired
    private TransactionalRunner transactionalRunner;


    @BeforeEach
    void setUp(@Autowired EraValidatorRepository eraValidatorRepository) {
        eraValidatorRepository.deleteAll();
    }

    @Test
    void storeAndFindByEraIdAndPublicKey() throws NoSuchAlgorithmException {

        var earId = 6930L;
        var weight = new BigInteger("3709277043188");
        var rewards = new BigInteger("1093289102572");
        var validator = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");

        var eraValidator = eraValidatorService.create(earId, validator, weight, rewards, true, true);

        var found = eraValidatorService.findByEraIdAndPublicKey(eraValidator.getEraId(), validator);

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getEraId(), is(earId));
        assertThat(found.get().getId(), is(notNullValue()));
        assertThat(found.get().getId(), is(not(found.get().getEraId())));
        assertThat(found.get().getWeight(), is(weight));
        assertThat(found.get().getRewards(), is(rewards));
        assertThat(found.get().getPublicKey(), is(validator));
        assertThat(found.get().isHasEquivocation(), is(true));
        assertThat(found.get().isWasActive(), is(true));
    }

    @Test
    void testDuplicateKeys() throws NoSuchAlgorithmException {

        var earId = 6931L;
        var weight = new BigInteger("3709277043188");
        var rewards = new BigInteger("1093289102572");
        var validator = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");

        var eraValidator = eraValidatorService.create(earId, validator, weight, rewards, true, true);
        assertThat(eraValidator, is(notNullValue()));

        try {
            eraValidatorService.create(earId, validator, weight, rewards, true, true);
            fail("Should have thrown a DataIntegrityViolationException");
        } catch (DataIntegrityViolationException e) {
            assertThat(e.getMessage(), containsString("UKIDX_VALIDATOR_ERA_ID"));
        }
    }

    @Test
    void findByEraId() throws NoSuchAlgorithmException {

        var earId = 6932L;

        var eraValidator1 = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"),
                new BigInteger("6097279509220"),
                BigInteger.ZERO,
                false,
                false
        );

        var eraValidator2 = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01d2e1392dbc73de9f896c62a21163f6b3816c3ec1d6c59da1600d882e2a788013"),
                new BigInteger("2399032584777"),
                BigInteger.ZERO,
                false,
                false
        );

        var eraValidator3 = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01d765e47206e14160eaa8a0bf8b1e9b035a0dae3d4698beb42f40208cfc663009"),
                new BigInteger("1720599557141"),
                BigInteger.ZERO,
                false,
                false
        );

        Page<EraValidator> byEraId = eraValidatorService.findByEraId(earId, Pageable.ofSize(10));
        assertThat(byEraId.getTotalElements(), is(3L));

        assertThat(byEraId.toList(), hasItems(eraValidator1, eraValidator2, eraValidator3));
    }


    @Test
    void updateEraValidator() throws Exception {

        var earId = 6933L;

        var eraValidator = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"),
                new BigInteger("6097279509220"),
                BigInteger.ZERO,
                false,
                true
        );

        transactionalRunner.runInTransaction(() -> {
            eraValidatorService.update(
                    eraValidator.getEraId(),
                    eraValidator.getPublicKey(),
                    new BigInteger("7097279509220"),
                    false,
                    true
            );
            return 1;
        });


        Optional<EraValidator> updated = eraValidatorService.findByEraIdAndPublicKey(eraValidator.getEraId(), eraValidator.getPublicKey());

        assertThat(updated.isPresent(), is(true));
        EraValidator actual = updated.get();
        assertThat(actual.getRewards(), is(new BigInteger("7097279509220")));
        assertThat(actual.isHasEquivocation(), is(false));
        assertThat(actual.isWasActive(), is(true));
    }


    @Test
    void updateHasEquivocationAndWasActive() throws Exception {

        var earId = 6934L;

        var eraValidator = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"),
                new BigInteger("6097279509220"),
                BigInteger.ONE,
                false,
                true
        );

        transactionalRunner.runInTransaction(() -> {
            eraValidatorService.updateHasEquivocationAndWasActive(
                    eraValidator.getEraId(),
                    eraValidator.getPublicKey(),
                    false,
                    true
            );
            return 1;
        });


        Optional<EraValidator> updated = eraValidatorService.findByEraIdAndPublicKey(eraValidator.getEraId(), eraValidator.getPublicKey());

        assertThat(updated.isPresent(), is(true));
        EraValidator actual = updated.get();
        assertThat(actual.getWeight(), is(new BigInteger("6097279509220")));
        assertThat(actual.getRewards(), is(BigInteger.ONE));
        assertThat(actual.isHasEquivocation(), is(false));
        assertThat(actual.isWasActive(), is(true));
    }

    @Test
    void updateWasActive() throws Exception {

        var earId = 6935L;

        var eraValidator = eraValidatorService.create(
                earId,
                PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"),
                new BigInteger("6097279509220"),
                BigInteger.TWO,
                false,
                true
        );

        transactionalRunner.runInTransaction(() -> {
            eraValidatorService.updateWasActive(
                    eraValidator.getEraId(),
                    eraValidator.getPublicKey(),
                    false
            );
            return 1;
        });


        Optional<EraValidator> updated = eraValidatorService.findByEraIdAndPublicKey(eraValidator.getEraId(), eraValidator.getPublicKey());

        assertThat(updated.isPresent(), is(true));
        EraValidator actual = updated.get();
        assertThat(actual.getWeight(), is(new BigInteger("6097279509220")));
        assertThat(actual.getRewards(), is(BigInteger.TWO));
        assertThat(actual.isHasEquivocation(), is(false));
        assertThat(actual.isWasActive(), is(false));
    }
}
