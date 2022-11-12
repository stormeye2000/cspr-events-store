package com.stormeye.event.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.Withdrawals;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class WithdrawalsRepositoryTest {

    @Autowired
    private WithdrawalsRepository withdrawalsRepository;

    @BeforeEach
    void setUp() {
        withdrawalsRepository.deleteAll();
    }

    @Test
    void save() throws NoSuchAlgorithmException {

        var timestamp = new Date();

        final List<Withdrawals> withdrawals = Arrays.asList(
                Withdrawals.builder()
                        .withdrawalKey("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                        .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                        .amount(new BigInteger("121223797933712"))
                        .bondingPurse("uref-96eb5207292608409181c8f8f963f8cdac7efc3ceb11f468300150721cd95fa8-007")
                        .updatedAt(timestamp)
                        .eraOfCreation(BigInteger.valueOf(7042))
                        .createdAt(timestamp)
                        .ubonderPublicKey(PublicKey.fromTaggedHexString("01c574e2bb199bb29eaf13c69ed3cd34312eb2da1b3b14dc88f97ce10e5e38710e"))
                        .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                        .timestamp(timestamp)
                        .build(),

                Withdrawals.builder()
                        .withdrawalKey("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                        .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                        .amount(new BigInteger("43888080800000"))
                        .bondingPurse("uref-992036abe0de025842a4409bd8720980c73c35dda941ff1d8370fd38e6b0d2be-007")
                        .updatedAt(timestamp)
                        .eraOfCreation(BigInteger.valueOf(7038))
                        .createdAt(timestamp)
                        .ubonderPublicKey(PublicKey.fromTaggedHexString("0203e7095eaff349603249b32cd09d9c5413bcf30c4aec9eb01489309a66253ca448"))
                        .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                        .timestamp(timestamp)
                        .build()

        );


        List<Withdrawals> saved = withdrawalsRepository.saveAll(withdrawals);
        assertThat(saved.size(), is(2));

        for (Withdrawals found : saved) {
            if (found.getUbonderPublicKey().equals(PublicKey.fromTaggedHexString("01c574e2bb199bb29eaf13c69ed3cd34312eb2da1b3b14dc88f97ce10e5e38710e"))) {

                assertThat(found.getWithdrawalKey(), is("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
                assertThat(found.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
                assertThat(found.getAmount(), is(new BigInteger("121223797933712")));
                assertThat(found.getBondingPurse(), is("uref-96eb5207292608409181c8f8f963f8cdac7efc3ceb11f468300150721cd95fa8-007"));
                assertThat(found.getUpdatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getEraOfCreation(), is(BigInteger.valueOf(7042)));
                assertThat(found.getCreatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
                assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));

            } else {

                assertThat(found.getWithdrawalKey(), is("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
                assertThat(found.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
                assertThat(found.getAmount(), is(new BigInteger("43888080800000")));
                assertThat(found.getBondingPurse(), is("uref-992036abe0de025842a4409bd8720980c73c35dda941ff1d8370fd38e6b0d2be-007"));
                assertThat(found.getUpdatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getEraOfCreation(), is(BigInteger.valueOf(7038)));
                assertThat(found.getCreatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
                assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));

            }

        }

    }

    @Test
    void findByDeployHash() throws NoSuchAlgorithmException {

        var timestamp = new Date();

        final List<Withdrawals> withdrawals = Arrays.asList(
                Withdrawals.builder()
                        .withdrawalKey("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                        .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                        .amount(new BigInteger("121223797933712"))
                        .bondingPurse("uref-96eb5207292608409181c8f8f963f8cdac7efc3ceb11f468300150721cd95fa8-007")
                        .updatedAt(timestamp)
                        .eraOfCreation(BigInteger.valueOf(7042))
                        .createdAt(timestamp)
                        .ubonderPublicKey(PublicKey.fromTaggedHexString("01c574e2bb199bb29eaf13c69ed3cd34312eb2da1b3b14dc88f97ce10e5e38710e"))
                        .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                        .timestamp(timestamp)
                        .build(),

                Withdrawals.builder()
                        .withdrawalKey("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                        .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                        .amount(new BigInteger("43888080800000"))
                        .bondingPurse("uref-992036abe0de025842a4409bd8720980c73c35dda941ff1d8370fd38e6b0d2be-007")
                        .updatedAt(timestamp)
                        .eraOfCreation(BigInteger.valueOf(7038))
                        .createdAt(timestamp)
                        .ubonderPublicKey(PublicKey.fromTaggedHexString("0203e7095eaff349603249b32cd09d9c5413bcf30c4aec9eb01489309a66253ca448"))
                        .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                        .timestamp(timestamp)
                        .build()

        );


        List<Withdrawals> saved = withdrawalsRepository.saveAll(withdrawals);
        assertThat(saved.size(), is(2));

        Optional<List<Withdrawals>> byFindByDeployHash = withdrawalsRepository.findByDeployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"));
        assertThat(byFindByDeployHash.isPresent(), is(true));

        for (Withdrawals found : byFindByDeployHash.get()) {
            if (found.getUbonderPublicKey().equals(PublicKey.fromTaggedHexString("01c574e2bb199bb29eaf13c69ed3cd34312eb2da1b3b14dc88f97ce10e5e38710e"))) {

                assertThat(found.getWithdrawalKey(), is("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
                assertThat(found.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
                assertThat(found.getAmount(), is(new BigInteger("121223797933712")));
                assertThat(found.getBondingPurse(), is("uref-96eb5207292608409181c8f8f963f8cdac7efc3ceb11f468300150721cd95fa8-007"));
                assertThat(found.getUpdatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getEraOfCreation(), is(BigInteger.valueOf(7042)));
                assertThat(found.getCreatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
                assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));

            } else {

                assertThat(found.getWithdrawalKey(), is("withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
                assertThat(found.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
                assertThat(found.getAmount(), is(new BigInteger("43888080800000")));
                assertThat(found.getBondingPurse(), is("uref-992036abe0de025842a4409bd8720980c73c35dda941ff1d8370fd38e6b0d2be-007"));
                assertThat(found.getUpdatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getEraOfCreation(), is(BigInteger.valueOf(7038)));
                assertThat(found.getCreatedAt().getTime(), is(timestamp.getTime()));
                assertThat(found.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
                assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));

            }

        }



    }



}
