package com.stormeye.event.store.service.storage.impl.era;

import com.casper.sdk.model.key.PublicKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraValidatorServiceTest {

    @Autowired
    private EraValidatorService eraValidatorService;


    @Test
    void storeAndFindByEraId() throws NoSuchAlgorithmException {

        var earId = 6930L;
        var weight = new BigInteger("3709277043188");
        var rewards = new BigInteger("1093289102572");
        var validator = PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715");

        var eraValidator = eraValidatorService.create(earId, validator, weight, rewards, 1, 2);

        var found = eraValidatorService.findByEraId(eraValidator.getEraId());

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getEraId(), is(earId));
        assertThat(found.get().getId(), is(earId));
        assertThat(found.get().getWeight(), is(weight));
        assertThat(found.get().getRewards(), is(rewards));
        assertThat(found.get().getValidator(), is(validator));
        assertThat(found.get().getHasEquivocation(), is(1));
        assertThat(found.get().getWasActive(), is(2));
    }
}