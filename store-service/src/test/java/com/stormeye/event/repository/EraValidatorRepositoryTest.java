package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
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

    }

    @Test
    void findByEraId() {

        assertThat(eraValidator.getId(), is(notNullValue()));

        Page<EraValidator> byEraId = eraValidatorRepository.findByEraId(eraValidator.getEraId(), Pageable.ofSize(1));
        assertThat(byEraId.getTotalElements(), is(1L));
        EraValidator found = byEraId.getContent().get(0);
        assertEraValidator(found);
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

    @Test
    void findByEraIdAndPublicKey() {
    }

    @Test
    void update() {
    }

    @Test
    void updateHasEquivocationAndWasActive() {
    }

    @Test
    void updateWasActive() {
    }
}