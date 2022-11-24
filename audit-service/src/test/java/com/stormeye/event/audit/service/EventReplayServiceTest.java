package com.stormeye.event.audit.service;

import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.stormeye.event.utils.MongoUtils.deleteAllDocuments;
import static com.stormeye.event.utils.MongoUtils.deleteAllFiles;
import static com.stormeye.event.utils.ThreadUtils.sleepNoSonarWarnings;
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
    @Autowired
    private GridFsOperations gridFsOperations;
    private int count;

    @BeforeEach
    void setUp() {
        deleteAllDocuments(mongoOperations);
        deleteAllFiles(gridFsOperations);
        count = 0;
    }

    @Test
    void replayAsStream() throws Exception {

        assertThat(eventReplayService, is(notNullValue()));

        // Populate some main events
        populateMainEvents();

        // Assert that the events are obtained
        Thread thread = new Thread(() -> eventReplayService.replayEvents(
                EventType.MAIN,
                0,
                0,
                null,
                event -> {

                    if (count == 0) {
                        assertThat(event, is("data:{\"ApiVersion\":\"1.4.7\"}\n\n"));
                    }
                    count++;

                    System.out.println(event);
                }));
        thread.start();

        // Wait for count to be more the zero
        sleepNoSonarWarnings(5000L);

        //noinspection deprecation
        thread.stop();

        assertThat(count, is(greaterThan(2)));

        //    Thread.sleep(10000L);


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
