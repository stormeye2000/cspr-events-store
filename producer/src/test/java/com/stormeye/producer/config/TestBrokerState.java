package com.stormeye.producer.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
@EmbeddedKafka(topics = {"main", "deploys", "sigs"}, partitions = 1, ports = {9199})
class TestBrokerState {

    @Autowired
    private BrokerState brokerState;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;
    private static EmbeddedKafkaBroker toDestroy;

    @BeforeEach
    void setUp() {
        toDestroy = kafkaBroker;
    }

    @AfterAll
    static void afterAll() {
        toDestroy.destroy();
    }

    @Test
    void testIsNotAvailable() {

        assertThat(brokerState, is(notNullValue()));

        assertThat(brokerState.isAvailable(), is(true));
    }
}
