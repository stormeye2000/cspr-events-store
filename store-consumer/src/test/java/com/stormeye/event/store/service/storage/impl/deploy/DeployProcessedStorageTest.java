package com.stormeye.event.store.service.storage.impl.deploy;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.BidRepository;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.repository.WithdrawalRepository;
import com.stormeye.event.service.storage.domain.Withdrawal;
import com.stormeye.event.store.service.storage.EventInfo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DeployProcessedStorageTest {

    private static final String DEPLOY_PROCESSED_FAILURE_JSON = "/kafka-data/kafka-single-events-deploy-processed-failure.json";
    private static final String DEPLOY_PROCESSED_SUCCESS_JSON = "/kafka-data/kafka-single-events-deploy-processed-success.json";
    private static final String DEPLOY_PROCESSED_SUCCESS_WITH_BIDS = "/kafka-data/kafka-single-events-deploy-withdrawal-bids.json";
    private static final String DEPLOY_PROCESSED_SUCCESS_WITH_TRANSFER = "/kafka-data/kafka-single-events-deploy-transfer.json";

    @Autowired
    private DeployProcessedService storageService;

    @Autowired
    private DeployRepository deployRepository;

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @BeforeEach
    void setUp() {
        deployRepository.deleteAll();
    }

    @Test
    void storeDeployFailure() throws IOException {

        var in = DeployProcessedStorageTest.class.getResourceAsStream(DEPLOY_PROCESSED_FAILURE_JSON);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(DeployProcessed.class));
        eventInfo.setSource("http://localhost:9999");

        var deploy = storageService.store(eventInfo);

        assertThat(deploy.getId(), is(notNullValue()));

        // Load the block from the database
        var foundOptional = deployRepository.findById(deploy.getId());
        assertThat(foundOptional.isPresent(), is(true));
        deploy = foundOptional.get();

        // Assert that all fields have been correctly persisted
        assertThat(deploy.getId(), is(notNullValue()));
        assertThat(deploy.getBlockHash(), is(new Digest("497405e6d478a1c1778b1d2cd1547437d41d5b36607221c3c3be05bcef757752")));
        assertThat(deploy.getDeployHash(), is(new Digest("c7d0840f2275a18efcd716f425c06691f2ca1a0e6d7d7ecff49cab06c2428ee8")));
        assertThat(deploy.getAccount(), is(new Digest("015c3b6de747e61d6cf9e2ea6593f68f34947277219c3cb5813e79634d300c0a4f")));
        assertThat(deploy.getErrorMessage(), is("ApiError::InvalidArgument [3]"));
        assertThat(deploy.getCost(), is(BigInteger.valueOf(1389380)));
        assertThat(deploy.getEventId(), is(65028921L));

    }

    @Test
    void storeDeploySuccess() throws IOException {

        var in = DeployProcessedStorageTest.class.getResourceAsStream(DEPLOY_PROCESSED_SUCCESS_JSON);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(DeployProcessed.class));
        eventInfo.setSource("http://localhost:9999");

        var deploy = storageService.store(eventInfo);

        assertThat(deploy.getId(), is(notNullValue()));

        // Load the block from the database
        var foundOptionalDeploy = deployRepository.findById(deploy.getId());
        assertThat(foundOptionalDeploy.isPresent(), is(true));
        deploy = foundOptionalDeploy.get();

        assertThat(deploy.getId(), is(notNullValue()));
        assertThat(deploy.getBlockHash(), is(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100")));
        assertThat(deploy.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(deploy.getAccount(), is(new Digest("01959d01aa68197e8cb91aa06bcc920f8d4a245dff60ea726bb89255349107a565")));
        assertThat(deploy.getErrorMessage(), is(Matchers.nullValue()));
        assertThat(deploy.getCost(), is(BigInteger.valueOf(100000000)));
        assertThat(deploy.getEventId(), is(65028921L));

        var foundOptionalTransfer = transferRepository.findByDeployHash(deploy.getDeployHash(), Pageable.ofSize(1));
        assertThat(foundOptionalTransfer.getTotalElements(), is(1L));
        var transfer = foundOptionalTransfer.getContent().get(0);

        assertThat(transfer.getId(), is(notNullValue()));
        assertThat(transfer.getBlockHash(), is(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100")));
        assertThat(transfer.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(transfer.getFromAccount(), is(new Digest("59cbc880e6d1f7407f18c36393c33d47ae51d5a54258f94a837ff996bf25a34d")));
        assertThat(transfer.getToAccount(), is(new Digest("a6cdb6f049363f6ab119be0c961c36e4a3c09319589341dd861f405d9836fc67")));
        assertThat(transfer.getSourcePurse(), is("uref-0eeb2bd99ae07173be21a5fc86db7a2ea7fae0abdfb5e81350bf52f22a66ea80-007"));
        assertThat(transfer.getTargetPurse(), is("uref-05f54a84872c75f7f05c8e8aaf9338ec848fa1a5b4f07202e371955c982f7f60-004"));
        assertThat(transfer.getAmount(), is(new BigInteger("2500000000")));
        assertThat(transfer.getTransferId(), is(BigInteger.valueOf(1)));

    }


    @Test
    void storeDeployTransfer() throws IOException {

        var in = DeployProcessedStorageTest.class.getResourceAsStream(DEPLOY_PROCESSED_SUCCESS_WITH_TRANSFER);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(DeployProcessed.class));
        eventInfo.setSource("http://localhost:9999");

        var deploy = storageService.store(eventInfo);

        assertThat(deploy.getId(), is(notNullValue()));

        // Load the block from the database
        var foundOptionalDeploy = deployRepository.findById(deploy.getId());
        assertThat(foundOptionalDeploy.isPresent(), is(true));
        deploy = foundOptionalDeploy.get();

        assertThat(deploy.getId(), is(notNullValue()));
        assertThat(deploy.getBlockHash(), is(new Digest("d99a71ad63708b0f0d5cd58add988e5d0fd192fa3830272631e4964d857d8a46")));
        assertThat(deploy.getDeployHash(), is(new Digest("fe7a6df71130f67a57bce91b8e21fb9df5b9b90629c9d30d7c6a500f7c47cec5")));
        assertThat(deploy.getAccount(), is(new Digest("018afa98ca4be12d613617f7339a2d576950a2f9a92102ca4d6508ee31b54d2c02")));
        assertThat(deploy.getErrorMessage(), is(Matchers.nullValue()));

        var foundOptionalTransfer = transferRepository.findByDeployHash(deploy.getDeployHash(), Pageable.ofSize(1));
        assertThat(foundOptionalTransfer.getTotalElements(), is(1L));
        var transfer = foundOptionalTransfer.getContent().get(0);

        assertThat(transfer.getId(), is(notNullValue()));
        assertThat(transfer.getDeployHash(), is(new Digest("fe7a6df71130f67a57bce91b8e21fb9df5b9b90629c9d30d7c6a500f7c47cec5")));
        assertThat(transfer.getFromAccount(), is(new Digest("b383c7cc23d18bc1b42406a1b2d29fc8dba86425197b6f553d7fd61375b5e446")));
        assertThat(transfer.getToAccount(), is(new Digest("03f447795e9a598b0d80be9162c91cbbe1b278f3c48f9e18d4bbfe6427e9cebd")));
        assertThat(transfer.getSourcePurse(), is("uref-b06a1ab0cfb52b5d4f9a08b68a5dbe78e999de0b0484c03e64f5c03897cf637b-007"));
        assertThat(transfer.getTargetPurse(), is("uref-c419ad2d20fda62e1abd905d0f362e6e62745f9e227187774b666a69d14026c6-004"));
        assertThat(transfer.getAmount(), is(new BigInteger("1000000000000")));
        assertThat(transfer.getTransferId(), is(nullValue()));
        assertThat(transfer.getTimestamp(), is(Matchers.notNullValue()));

    }

    @Test
    void storeDeploySuccessWithBids() throws IOException, NoSuchAlgorithmException {

        var in = DeployProcessedStorageTest.class.getResourceAsStream(DEPLOY_PROCESSED_SUCCESS_WITH_BIDS);

        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getData(), instanceOf(DeployProcessed.class));
        eventInfo.setSource("http://localhost:9999");

        var deploy = storageService.store(eventInfo);

        assertThat(deploy.getId(), is(notNullValue()));

        // Load the block from the database
        var foundOptionalDeploy = deployRepository.findById(deploy.getId());
        assertThat(foundOptionalDeploy.isPresent(), is(true));
        deploy = foundOptionalDeploy.get();

        assertThat(deploy.getId(), is(notNullValue()));

        var bids = bidRepository.findByDeployHash(deploy.getDeployHash());
        assertThat(bids.isEmpty(), is(false));

        assertThat(bids, is(notNullValue()));

        assertThat(bids.get(0).getBidKey(), is("bid-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
        assertThat(bids.get(0).getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(bids.get(0).getBondingPurse(), is("uref-ec2c8b244abb16efd48fcb25740863bfd305ed4b8be1ee9466fff998b93e3a9c-007"));
        assertThat(bids.get(0).getDelegators(), is(Matchers.notNullValue()));
        assertThat(bids.get(0).getDelegationRate(), is(5));
        assertThat(bids.get(0).getStakedAmount(), is(new BigInteger("12005548678314")));
        assertThat(bids.get(0).getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
        assertThat(bids.get(0).getVestingSchedule(), is("null"));
        assertThat(bids.get(0).isInactive(), is(false));
        assertThat(bids.get(0).getTimestamp(), is(Matchers.notNullValue()));

        var withdrawals = withdrawalRepository.findByDeployHash(deploy.getDeployHash());
        assertThat(withdrawals.isEmpty(), is(false));

        assertThat(withdrawals.size(), is(2));

        assertWithdrawal(
                withdrawals.get(0),
                "withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63",
                PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"),
                "uref-96eb5207292608409181c8f8f963f8cdac7efc3ceb11f468300150721cd95fa8-007",
                new BigInteger("121223797933712"),
                PublicKey.fromTaggedHexString("01c574e2bb199bb29eaf13c69ed3cd34312eb2da1b3b14dc88f97ce10e5e38710e")
        );

        assertWithdrawal(
                withdrawals.get(1),
                "withdraw-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63",
                PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"),
                "uref-992036abe0de025842a4409bd8720980c73c35dda941ff1d8370fd38e6b0d2be-007",
                new BigInteger("43888080800000"),
                PublicKey.fromTaggedHexString("0203e7095eaff349603249b32cd09d9c5413bcf30c4aec9eb01489309a66253ca448")
        );
    }

    @Test
    void gracefullyHandleDuplicateEvent() throws IOException {

        var in = DeployProcessedStorageTest.class.getResourceAsStream(DEPLOY_PROCESSED_SUCCESS_JSON);
        var eventInfo = new ObjectMapper().readValue(in, EventInfo.class);
        assertThat(eventInfo.getId(), is(notNullValue()));
        assertThat(eventInfo.getData(), instanceOf(DeployProcessed.class));
        eventInfo.setSource("http://localhost:9999");

        // Save the block added as a block
        var deploy = storageService.store(eventInfo);

        var originalId = deploy.getId();

        // store the same block again
        deploy = storageService.store(eventInfo);

        // Assert that the block has not been duplicated
        assertThat(deploy.getId(), is(originalId));
    }

    private void assertWithdrawal(final Withdrawal withdrawal,
                                  final String expectedWithdrawalKey,
                                  final PublicKey expectedValidatorPublicKey,
                                  final String expectedBondingPurse,
                                  final BigInteger expectedAmount,
                                  final PublicKey expectedUbonderPublicKey) {
        assertThat(withdrawal.getWithdrawalKey(), is(expectedWithdrawalKey));
        assertThat(withdrawal.getValidatorPublicKey(), is(expectedValidatorPublicKey));
        assertThat(withdrawal.getBondingPurse(), is(expectedBondingPurse));
        assertThat(withdrawal.getAmount(), is(expectedAmount));
        assertThat(withdrawal.getTimestamp(), is(Matchers.notNullValue()));
        assertThat(withdrawal.getCreatedAt(), is(Matchers.notNullValue()));
        assertThat(withdrawal.getUpdatedAt(), is(Matchers.notNullValue()));
        assertThat(withdrawal.getUbonderPublicKey(), is(expectedUbonderPublicKey));
    }
}
