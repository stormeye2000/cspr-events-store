package com.stormeye.event.store.services.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EmbeddedKafka(topics = {"main", "deploys", "sigs"}, partitions = 1, ports = {9092})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) // FIX ME complains about topics existing without this
class EventsConsumerTest {

    private static final String EVENT_JSON = "/kafka-data/kafka-single-events-main.json";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Test
    void testConfig() {
        assertThat(kafkaBroker, is(notNullValue()));
    }
}