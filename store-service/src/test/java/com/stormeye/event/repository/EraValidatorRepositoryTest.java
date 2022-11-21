package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.common.TransactionalRunner;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraValidatorRepositoryTest {

    @Autowired
    private EraValidatorRepository eraValidatorRepository;
    private PublicKey validator;
    private EraValidator eraValidator;
    private PublicKey validatorTwo;
    private PublicKey validatorThree;

    @Autowired
    private TransactionalRunner transactionalRunner;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        eraValidatorRepository.deleteAll();

        validator = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");

        eraValidator = eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(validator)
                .weight(BigInteger.valueOf(45678))
                .rewards(BigInteger.TEN)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        validatorTwo = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716");

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(validatorTwo)
                .weight(BigInteger.valueOf(45678))
                .rewards(BigInteger.TWO)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        validatorThree = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717");

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(validatorThree)
                .weight(BigInteger.valueOf(45678))
                .rewards(BigInteger.valueOf(3))
                .hasEquivocation(false)
                .wasActive(true)
                .build());
    }

    @Test
    void findByEraId() {

        assertThat(eraValidator.getId(), is(notNullValue()));

        Page<EraValidator> byEraId = eraValidatorRepository.findByEraId(eraValidator.getEraId(), Pageable.ofSize(2));
        assertThat(byEraId.getTotalElements(), is(3L));
        assertThat(byEraId.getTotalPages(), is(2));
        assertThat(byEraId.getNumber(), is(0));
        assertThat(byEraId.getNumberOfElements(), is(2));
        EraValidator found = byEraId.getContent().get(0);
        assertEraValidator(found);

        EraValidator eraValidator2 = byEraId.getContent().get(1);
        assertThat(eraValidator2.getPublicKey(), is(validatorTwo));
    }

    @Test
    void findByEraIdAndPublicKey() throws NoSuchAlgorithmException {

        PublicKey key = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716");

        Optional<EraValidator> byEraIdAndPublicKey = eraValidatorRepository.findByEraIdAndPublicKey(
                1234L,
                key
        );

        assertThat(byEraIdAndPublicKey.isPresent(), is(true));
        EraValidator validator = byEraIdAndPublicKey.get();
        assertThat(validator.getEraId(), is(1234L));
        assertThat(validator.getPublicKey(), is(key));
    }

    @Test
    void update() throws Exception {

        int count = transactionalRunner.runInTransaction(() ->
                eraValidatorRepository.update(1234L, validatorThree, BigInteger.valueOf(456780L), true, false)
        );

        // Assert that one record was updated
        assertThat(count, is(1));

        Optional<EraValidator> byEraIdAndPublicKey = eraValidatorRepository.findByEraIdAndPublicKey(
                1234L,
                validatorThree
        );

        assertThat(byEraIdAndPublicKey.isPresent(), is(true));
        EraValidator updated = byEraIdAndPublicKey.get();
        assertThat(updated.getRewards(), is(BigInteger.valueOf(456780L)));
        assertThat(updated.getWeight(), is(BigInteger.valueOf(45678)));
        assertThat(updated.isHasEquivocation(), is(true));
        assertThat(updated.isWasActive(), is(false));
    }

    @Test
    void updateHasEquivocationAndWasActive() throws Exception {

        int count = transactionalRunner.runInTransaction(() ->
                eraValidatorRepository.updateHasEquivocationAndWasActive(1234L, validatorThree, true, false)
        );

        // Assert that one record was updated
        assertThat(count, is(1));

        Optional<EraValidator> byEraIdAndPublicKey = eraValidatorRepository.findByEraIdAndPublicKey(
                1234L,
                validatorThree
        );

        assertThat(byEraIdAndPublicKey.isPresent(), is(true));
        EraValidator updated = byEraIdAndPublicKey.get();
        assertThat(updated.getWeight(), is(BigInteger.valueOf(45678)));
        assertThat(updated.getRewards(), is(BigInteger.valueOf(3)));
        assertThat(updated.isHasEquivocation(), is(true));
        assertThat(updated.isWasActive(), is(false));
    }

    @Test
    void updateWasActive() throws Exception {
        int count = transactionalRunner.runInTransaction(() ->
                eraValidatorRepository.updateWasActive(1234L, validatorThree, false)
        );

        // Assert that one record was updated
        assertThat(count, is(1));

        Optional<EraValidator> byEraIdAndPublicKey = eraValidatorRepository.findByEraIdAndPublicKey(
                1234L,
                validatorThree
        );

        assertThat(byEraIdAndPublicKey.isPresent(), is(true));
        EraValidator updated = byEraIdAndPublicKey.get();
        assertThat(updated.getWeight(), is(BigInteger.valueOf(45678)));
        assertThat(updated.getRewards(), is(BigInteger.valueOf(3)));
        assertThat(updated.isHasEquivocation(), is(false));
        assertThat(updated.isWasActive(), is(false));
    }

    private void assertEraValidator(final EraValidator actual) {
        assertThat(actual.getId(), is(eraValidator.getId()));
        assertThat(actual.getEraId(), is(1234L));
        assertThat(actual.getPublicKey(), is(validator));
        assertThat(actual.getWeight(), is(BigInteger.valueOf(45678)));
        assertThat(actual.getRewards(), is(BigInteger.TEN));
        assertThat(actual.isHasEquivocation(), is(true));
        assertThat(actual.isWasActive(), is(true));
    }
}