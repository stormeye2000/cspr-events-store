package com.stormeye.event.store.audit.consumer.service;

import com.stormeye.event.audit.service.AuditEventInfo;
import com.stormeye.event.utils.MongoUtils;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for the {@link KafkaEventConsumer}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EmbeddedKafka(topics = {"main", "deploys", "sigs"}, partitions = 1, ports = {9098})
class KafkaEventConsumerTest {


    private static final String EVENT_JSON = "/kafka-data/kafka-single-events-main.json";
    @Autowired
    private MongoOperations mongoOperations;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;


    @BeforeEach
    void setUp() {
        MongoUtils.deleteAllDocuments(mongoOperations);
    }

    @Test
    void registeredTopics() throws Exception {
        Method consumeWithHeaders = KafkaEventConsumer.class.getDeclaredMethod("consumeWithHeaders", String.class, String.class);
        KafkaListener kafkaListener = consumeWithHeaders.getAnnotation(KafkaListener.class);
        assertThat(kafkaListener.topics(), is(new String[]{"main", "deploys", "sigs"}));
    }

    @Test
    void testConsumeEvents() throws Exception {

        var producer = new DummyProducer(kafkaBroker);

        var mainTopic = "main";

        assertThat(mongoOperations.count(new Query(), mainTopic), is(0L));

        // Wait for Kafka to complete config
        Thread.sleep(5000L);

        //noinspection ConstantConditions
        var eventJson = IOUtils.toString(KafkaEventConsumerTest.class.getResourceAsStream(EVENT_JSON), StandardCharsets.UTF_8);

        // Send using a Kafka Produce
        var send = producer.send(mainTopic, (int) System.currentTimeMillis(), eventJson);

        // Wait for the producer to send
        waitSent(send);

        assertThat(send.isDone(), is(true));

        // Assert the KafkaEventConsumer was invoked
        assertThat(mongoOperations.count(new Query(), "main"), is(1L));

        // Assert the event info was persisted in mongo
        var eventInfo = mongoOperations.find(new Query(), AuditEventInfo.class, "main").get(0);
        assertThat(eventInfo.getId(), is(notNullValue()));
        assertThat(eventInfo.getEventId(), is(65027303L));
        assertThat(eventInfo.getEventType(), is(mainTopic));
        assertThat(eventInfo.getSource(), is("http://65.21.235.219:9999"));
        assertThat(eventInfo.getVersion(), is("1.0.0"));
        assertThat(eventInfo.getDataType(), is("BlockAdded"));
    }

    private static void waitSent(final Future<RecordMetadata> send) throws InterruptedException {
        var count = 0;
        while (!send.isDone()) {
            //noinspection BusyWait
            Thread.sleep(1000L);
            if (++count > 5) {
                fail("Should have sent event");
            }
        }
        Thread.sleep(1000L);
    }
}
