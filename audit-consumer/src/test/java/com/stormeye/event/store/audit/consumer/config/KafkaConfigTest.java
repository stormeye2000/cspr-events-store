package com.stormeye.event.store.audit.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
class KafkaConfigTest {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void kafkaConfig() {
        assertThat(kafkaConfig, is(notNullValue()));
    }

    @Test
    void getBootstrapAddress() {
        assertThat(kafkaConfig.getBootstrapAddress(), is("localhost:9198"));
    }

    @Test
    void getClientId() {
        assertThat(kafkaConfig.getClientId(), is("consumer-audit"));
    }

    @Test
    void getGroupId() {
        assertThat(kafkaConfig.getGroupId(), is("consumer-audit"));
    }
}