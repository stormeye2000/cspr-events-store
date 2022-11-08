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
        var foundOptional = deployRepository.findById(deploy.getId());
        assertThat(foundOptional.isPresent(), is(true));
        deploy = foundOptional.get();

        assertThat(deploy.getId(), is(notNullValue()));
        assertThat(deploy.getBlockHash(), is(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100")));
        assertThat(deploy.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(deploy.getAccount(), is(new Digest("01959d01aa68197e8cb91aa06bcc920f8d4a245dff60ea726bb89255349107a565")));
        assertThat(deploy.getErrorMessage(), is(Matchers.nullValue()));
        assertThat(deploy.getCost(), is(BigInteger.valueOf(100000000)));
        assertThat(deploy.getEventId(), is(65028921L));

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
