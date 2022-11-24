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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = {AppConfig.class, ServiceProperties.class})
@EmbeddedKafka(topics = "main", partitions = 1, ports = 9101, brokerProperties = "message.max.bytes=268435456")
class SendMegaEventSuccessTest extends SendMethods {

    private KafkaProducer<Integer, Event<?>> kafkaProducer;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;


    @BeforeEach
    void setUp() {
        kafkaProducer = new KafkaProducer<>(producerConfigs(MB256, "9101"));
    }

    @AfterEach
    void tearDown() {
        kafkaProducer.close();
        kafkaBroker.destroy();
    }

    /**
     * Sends a large event with the broker message.max.bytes set to 256mb and the producer's
     * 'max.request.size' and 'buffer.memory' set to 256mb.
     * These config changes allow large messages to be sent
     * A pass is the metadata containing the topic
     * A fail is an exception
     */
    @Test
    void testSendEvent() throws Exception {

        final Event<?> event = super.buildEvent(super.getEventFile("step-large.event"));

        final ProducerRecord<Integer, Event<?>> producerRecord = new ProducerRecord<>(TOPIC, event);

        final Future<RecordMetadata> send = kafkaProducer.send(producerRecord, null);

        final RecordMetadata meta = send.get(5, TimeUnit.SECONDS);

        assertThat(meta.topic(), is(TOPIC));
    }
}
