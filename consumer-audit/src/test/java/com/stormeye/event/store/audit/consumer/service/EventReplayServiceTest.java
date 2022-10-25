package com.stormeye.event.store.audit.consumer.service;

import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Unit tests for the {@link EventReplayService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventReplayServiceTest {

    private static final String MAIN_EVENTS_JSON = "/kafka-data/kafka-events-main.json";
    @Autowired
    private EventReplayService eventReplayService;
    @Autowired
    private EventAuditService eventAuditService;
    @Autowired
    private MongoOperations mongoOperations;
    private int count;

    @BeforeEach
    void setUp() {
        ((MongoTemplate) mongoOperations).getDb().drop();
        count = 0;
    }

    @Test
    void replayAsStream() throws Exception {

        assertThat(eventReplayService, is(notNullValue()));

        // Populate some main events
        populateMainEvents();

        // Assert that the events are obtained
        new Thread(() -> {
            var stream = eventReplayService.replayAsStream(EventType.MAIN, 0, 0, null);
            stream.forEach(event -> {

                count++;
                System.out.println(event);

            });
        }).start();

        // Wait for count to be more the zero
        Thread.sleep(15000L);

        assertThat(count, is(greaterThan(0)));

        Thread.sleep(10000L);


        // Assert that the ':' character is sent to keep the stream open

        // Populate more main events in the database

        // Assert that the events are obtained

        // Assert that the ':' character is sent to keep the stream open
    }

    private void populateMainEvents() throws IOException {

        var jsonNode = new ObjectMapper().readTree(EventAuditServiceTest.class.getResourceAsStream(MAIN_EVENTS_JSON));
        jsonNode.forEach(child -> eventAuditService.save(child.toPrettyString()));

    }
}
