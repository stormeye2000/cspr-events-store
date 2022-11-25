package com.stormeye.event.store.consumer;

import com.casper.sdk.model.common.Digest;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.service.storage.domain.Block;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.stormeye.event.utils.ThreadUtils.sleepNoSonarWarnings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests for the events consumer
 *
 * @author ian@meywood.com
 */
//@Disabled
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventsConsumerTest {

    private static final String EVENT_JSON = "/kafka-data/kafka-single-events-main.json";

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private EventsConsumer eventsConsumer;

    @Test
    void testTopics() throws NoSuchMethodException {
        // Assert that topics exist
        Method consumeWithHeaders = EventsConsumer.class.getDeclaredMethod("consumeWithHeaders", String.class, String.class);
        KafkaListener kafkaListener = consumeWithHeaders.getAnnotation(KafkaListener.class);
        assertThat(kafkaListener.topics(), is(new String[]{"main", "deploys", "sigs"}));
    }

    @Test
    void consumeEvents() throws IOException {

        long count = blockRepository.count();

        // Wait for Kafka to complete config
        sleepNoSonarWarnings(5000L);

        //noinspection ConstantConditions
        var eventJson = IOUtils.toString(EventsConsumerTest.class.getResourceAsStream(EVENT_JSON), StandardCharsets.UTF_8);

        eventsConsumer.consumeWithHeaders("main", eventJson);

        // Assert that a block was added by the consumer
        assertThat(blockRepository.count(), is(count + 1));

        Optional<Block> byBlockHash = blockRepository.findByBlockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"));
        assertThat(byBlockHash.isPresent(), is(true));
        Block block = byBlockHash.get();
        assertThat(block.getEventId(), is(65027303L));
        assertThat(block.getEraId(), is(5849L));
        assertThat(block.getBlockHeight(), is(1002230L));
    }
}
