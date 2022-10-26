package com.stormeye.event.audit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Tests for the the {@link EventBlobStore}
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventBlobStoreTest {

    @Autowired
    private EventBlobStore eventBlobStore;
    @Autowired
    private GridFsOperations gridFsOperations;
    private ObjectMapper mapper;
    private static final String JSON = "/kafka-data/kafka-single-events-main.json";

    @BeforeEach
    void setUp() {
        // Delete all files
        gridFsOperations.delete(new Query());
        mapper = ObjectMapperFactory.createObjectMapper();
    }

    @Test
    void saveEvent() throws IOException {

        //noinspection resource,ConstantConditions
        final byte[] json = EventAuditServiceTest.class.getResourceAsStream(JSON).readAllBytes();
        EventInfo eventInfo = mapper.readValue(json, EventInfo.class);

        eventInfo = eventBlobStore.saveEvent(eventInfo, json);
        final ObjectId id = eventInfo.getId();
        assertThat(id, is(notNullValue()));

        // Assert the file was stored
        GridFSFile gridFSFile = gridFsOperations.findOne(new Query(Criteria.where("_id").is(id)));

        // Assert the metadata was correctly populated
        assertThat(gridFSFile, is(notNullValue()));
        assertThat(gridFSFile.getMetadata(), is(notNullValue()));
        assertThat(gridFSFile.getMetadata().getString("source"), is("http://65.21.235.219:9999"));
        assertThat(gridFSFile.getMetadata().getString("type"), is("main"));
        assertThat(gridFSFile.getMetadata().getString("dataType"), is("BlockAdded"));
        assertThat(gridFSFile.getMetadata().getLong("eventId"), is(65027303L));
        assertThat(gridFSFile.getFilename(), is("/events/main/65027303.json"));
        assertThat(gridFSFile.getMetadata().getString("version"), is("1.0.0"));
    }

    @Test
    void readEvent() throws IOException {

        //noinspection resource,ConstantConditions
        final byte[] json = EventAuditServiceTest.class.getResourceAsStream(JSON).readAllBytes();
        EventInfo eventInfo = mapper.readValue(json, EventInfo.class);
        final byte[] dataBytes = eventInfo.getData().getBytes(StandardCharsets.UTF_8);
        eventInfo = eventBlobStore.saveEvent(eventInfo, dataBytes);
        final ObjectId id = eventInfo.getId();

        final EventStream readJson = eventBlobStore.getEventById(id);
        final byte[] readBytes = IOUtils.toByteArray(readJson);

        assertThat(readJson.getSize(), is((long) readBytes.length));

        // Assert that all bytes are present
        assertThat(readBytes, is(dataBytes));
    }
}
