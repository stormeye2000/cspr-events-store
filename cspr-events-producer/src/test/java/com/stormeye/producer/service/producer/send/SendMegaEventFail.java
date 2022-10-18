package com.stormeye.producer.service.producer.send;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.casper.sdk.model.event.Event;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {AppConfig.class, ServiceProperties.class})
@EmbeddedKafka(topics = {"main"}, partitions = 1, ports = {9093})
public class SendMegaEventFail extends SendMethods {

    @Autowired
    private KafkaProducer<Integer, Event<?>> kafkaProducer;

    /**
     * Tests that a broker with default message.max.bytes set to 1mb and its producer's
     * max.request.size and buffer.memory are set to default 1mb will fail when a large
     * message is produced.
     * The error thrown will be a java ExecutionException with the kafka error class
     * embedded in the exception message.
     * A pass is 'RecordTooLargeException' in the exception message
     * A fail is anything else
     */
    @Test
    void testSendEvent() throws Exception {

        final Event<?> event = super.buildEvent(super.getEventFile("step-large.event"));

        kafkaProducer = new KafkaProducer<>(super.producerConfigs(MB1, "9093"));

        final ProducerRecord<Integer, Event<?>> producerRecord = new ProducerRecord<>(TOPIC, event);

        Future<RecordMetadata> send = kafkaProducer.send(producerRecord, null);

        try {
            send.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e){
            assert(e.getMessage().contains("RecordTooLargeException"));
        }

    }

}
