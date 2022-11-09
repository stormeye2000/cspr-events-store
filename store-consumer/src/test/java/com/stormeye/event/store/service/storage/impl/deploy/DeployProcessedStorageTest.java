package com.stormeye.event.store.service.storage.impl.deploy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.store.service.storage.EventInfo;

import java.io.IOException;
import java.math.BigInteger;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DeployProcessedStorageTest {

    private static final String DEPLOY_PROCESSED_FAILURE_JSON = "/kafka-data/kafka-single-events-deploy-processed-failure.json";
    private static final String DEPLOY_PROCESSED_SUCCESS_JSON = "/kafka-data/kafka-single-events-deploy-processed-success.json";

    @Autowired
    private DeployProcessedService storageService;

    @Autowired
    private DeployRepository deployRepository;

    @Autowired
    private TransferRepository transferRepository;

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

        var transfer = transferRepository.findByDeployHashAndBlockHash(deploy.getDeployHash(), deploy.getBlockHash());

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

}
