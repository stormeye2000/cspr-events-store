package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import com.mongodb.client.ListIndexesIterable;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Unit tests for the {@link IdStorageService}.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class IdStorageServiceTest {

    @Autowired
    private IdStorageService idStorageService;

    @Autowired
    private MongoOperations mongoOperations;

    @AfterEach
    void teardown() {
        ((MongoTemplate) mongoOperations).getDb().drop();
    }

    @Test
    void createService() {
        assertThat(idStorageService, is(notNullValue()));
        assertThat(((MongoTemplate) mongoOperations).getDb().getName(), is("test-casper-producer"));
    }

    @Test
    void indexExist() {

        // Ensure indexes are created
        new IdStorageService(mongoOperations);

        final ListIndexesIterable<Document> indexesIterable = mongoOperations.getCollection("eventIds").listIndexes();
        assertThat(indexesIterable, is(notNullValue()));
        final List<Document> indexes = new ArrayList<>();

        //noinspection resource
        indexesIterable.iterator().forEachRemaining(indexes::add);
        assertThat(indexes, hasSize(2));

        assertThat(indexes.get(0).get("name"), is("_id_"));
        assertThat(indexes.get(1).get("name"), is("source_1_type_1"));
        assertThat(indexes.get(1).get("key", Document.class).size(), is(2));
        assertThat(indexes.get(1).get("key", Document.class).size(), is(2));
        assertThat(indexes.get(1).get("key", Document.class).get("source"), is(1));
        assertThat(indexes.get(1).get("key", Document.class).get("type"), is(1));
    }

    @Test
    void getCurrentIdAndGetNextId() throws URISyntaxException {

        mongoOperations.getCollection("eventIds").drop();

        final URI source = new URI("http://localhost:9999");

        assertThat(idStorageService.getCurrentId(source, EventType.MAIN), is(0L));
        assertThat(idStorageService.getNextId(source, EventType.MAIN), is(0L));

        idStorageService.setCurrentEvent(source, EventType.MAIN, 2L);
        assertThat(idStorageService.getNextId(source, EventType.MAIN), is(3L));

        assertThat(idStorageService.getCurrentId(source, EventType.MAIN), is(2L));

        idStorageService.setCurrentEvent(source, EventType.MAIN, 4L);

        assertThat(idStorageService.getCurrentId(source, EventType.MAIN), is(4L));
        assertThat(idStorageService.getNextId(source, EventType.MAIN), is(5L));
    }
}
