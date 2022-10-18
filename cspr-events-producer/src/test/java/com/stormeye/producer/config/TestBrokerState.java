package com.stormeye.producer.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application.yml", "classpath:application-test.properties"})
@EmbeddedKafka(topics = {"main", "deploys", "sigs"}, partitions = 1, ports = {9092})
public class TestBrokerState {

    @Autowired
    private BrokerState brokerState;

    @Test
    void testIsNotAvailable(){

        assertThat(brokerState, is(notNullValue()));

        assertThat(brokerState.isAvailable(), is(true));

    }

}