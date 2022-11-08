package com.stormeye.event.audit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Unit tests for loading an {@link AuditEventInfo} from JSON.
 *
 * @author ian@meywood.com
 */
class EventInfoTest {

    private static final String JSON = "/kafka-data/kafka-single-events-main.json";
    private AuditEventInfo eventInfo;

    @BeforeEach
    void setUp() throws IOException {
        eventInfo = ObjectMapperFactory.createObjectMapper().readValue(EventInfoTest.class.getResourceAsStream(JSON), AuditEventInfo.class);
    }

    @Test
    void getSource() {
        assertThat(eventInfo.getSource(), is("http://65.21.235.219:9999"));
    }

    @Test
    void getEventType() {
        assertThat(eventInfo.getEventType(), is("main"));
    }

    @Test
    void getDataType() {
        assertThat(eventInfo.getDataType(), is("BlockAdded"));
    }

    @Test
    void getId() {
        assertThat(eventInfo.getEventId(), is(65027303L));
    }

    @Test
    void getVersion() {
        assertThat(eventInfo.getVersion(), is("1.0.0"));
    }

    @Test
    void getData() {
        assertThat(eventInfo.getData(), containsString("BlockAdded"));
    }

    @Test
    void getBytes() {
        assertThat(eventInfo.getBytes(), is(eventInfo.getData().getBytes(StandardCharsets.UTF_8).length));
    }
}
