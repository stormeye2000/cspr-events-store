package com.stormeye.producer.service.producer.send;

import com.casper.sdk.model.event.Event;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

@SpringBootTest(classes = {AppConfig.class, ServiceProperties.class})
@EmbeddedKafka(topics = "main", partitions = 1, ports = 9100)
class SendMegaEventFailTest extends SendMethods {

    private KafkaProducer<Integer, Event<String>> kafkaProducer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @BeforeEach
    void setUp() {
        kafkaProducer = new KafkaProducer<>(producerConfigs(MB256, "9100"));
    }

    @AfterEach
    void tearDown() {
        kafkaProducer.close();
        kafkaBroker.destroy();
    }

    /**
     * Tests that a broker with default message.max.bytes set to 1mb and its producer's
     * 'max.request.size' and 'buffer.memory' are set to default 1mb will fail when a large
     * message is produced.
     * The error thrown will be a java ExecutionException with the kafka error class
     * embedded in the exception message.
     * A pass is 'RecordTooLargeException' in the exception message
     * A fail is anything else
     */
    @Test
    void testSendEvent() throws Exception {

        final Event<String> event = super.buildEvent(super.getEventFile("step-large.event"));

        final ProducerRecord<Integer, Event<String>> producerRecord = new ProducerRecord<>(TOPIC, event);

        final Future<RecordMetadata> send = kafkaProducer.send(producerRecord, null);

        try {
            send.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            assertThat(e.getMessage(), containsString("RecordTooLargeException"));
        }
    }
}
