package com.stormeye.event.audit.service;


import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.stormeye.event.utils.MongoUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.stormeye.event.common.EventConstants.API_VERSION;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test for the EventAuditService.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventAuditServiceTest {

    private static final String MAIN_EVENTS_JSON = "/kafka-data/kafka-events-main.json";
    private static final String MAIN_EVENTS_DIFF_VERSIONS_JSON = "/kafka-data/kafka-events-main-different-versions.json";
    private static final String MAIN_EVENTS_SINGLE_JSON = "/kafka-data/kafka-single-events-main.json";
    @Autowired
    private EventAuditService eventAuditService;
    @Autowired
    private GridFsOperations gridFsOperations;
    @Autowired
    private MongoOperations mongoOperations;
    private JsonNode jsonNode;

    @BeforeEach
    void setUp() throws IOException {
        MongoUtils.deleteAllDocuments(mongoOperations);
        MongoUtils.deleteAllFiles(gridFsOperations);
        // Ensure index are recreated one database is dropped
        eventAuditService.createIndexes();
        jsonNode = new ObjectMapper().readTree(EventAuditServiceTest.class.getResourceAsStream(MAIN_EVENTS_JSON));
    }

    @AfterEach
    void tearDown() {
        // Ensure database is dropped after every test
        MongoUtils.deleteAllDocuments(mongoOperations);
        MongoUtils.deleteAllFiles(gridFsOperations);
    }

    @Test
    void eventAuditServiceCreation() {
        assertThat(eventAuditService, is(notNullValue()));
    }

    @Test
    void doesNotSaveAndFindApiVersionMainEvent() {

        var eventInfo = eventAuditService.save(jsonNode.get(0).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(eventInfo, is(notNullValue()));

        var id = eventInfo.getId();
        // Object ID will be null as version are not saved
        assertThat(id, is(nullValue()));
        assertThat(eventInfo.getEventId(), is(nullValue()));

        assertApiVersion(eventInfo, "1.4.7");
    }

    @Test
    void saveBlockAddedMainEvent() {

        var eventInfo = eventAuditService.save(jsonNode.get(1).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(eventInfo, is(notNullValue()));

        var id = eventInfo.getId();
        assertThat(id, is(notNullValue()));

        assertThat(eventInfo.getEventType(), is("main"));
        assertThat(eventInfo.getEventId(), is(65027303L));
        assertThat(eventInfo.getDataType(), is("BlockAdded"));
    }

    @Test
    void saveDeployProcessedMainEvent() {

        final AuditEventInfo eventInfo = eventAuditService.save(jsonNode.get(2).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(eventInfo, is(notNullValue()));

        final ObjectId id = eventInfo.getId();
        assertThat(id, is(notNullValue()));

        assertThat(eventInfo.getEventType(), is("main"));
        assertThat(eventInfo.getEventId(), is(65028921L));

        assertThat(eventInfo.getDataType(), is("DeployProcessed"));
    }

    @Test
    void findByEventId() {

        // Save all JSON  events in the test data file to mongo
        loadAllData();

        var optionalEvent = eventAuditService.findByEventId(65027303L, EventType.MAIN);

        assertThat(optionalEvent.isPresent(), is(true));
        var event = optionalEvent.get();

        assertThat(event.getEventId(), is(65027303L));
        assertThat(event.getDataType(), is("BlockAdded"));
        assertThat(event.getSource(), is("http://65.21.235.219:9999"));
    }

    @Test
    void getApiVersion() throws IOException {

        // Load test data and save in Mongo
        var jsonIn = EventAuditServiceTest.class.getResourceAsStream(MAIN_EVENTS_DIFF_VERSIONS_JSON);
        jsonNode = new ObjectMapper().readTree(jsonIn);
        loadAllData();

        // Obtain the first events version
        var apiVersion = eventAuditService.getApiVersion(65027303, EventType.MAIN);
        assertThat(apiVersion.isPresent(), is(true));
        assertThat(apiVersion.get(), is("1.4.5"));

        // Obtain the last events version
        apiVersion = eventAuditService.getApiVersion(65028921, EventType.MAIN);
        assertThat(apiVersion.isPresent(), is(true));
        assertThat(apiVersion.get(), is("1.4.7"));
    }

    @Test
    void findAllSince() throws IOException {

        loadAllData();

        var page = eventAuditService.findAllSince(0, EventType.MAIN, null, Pageable.ofSize(3));
        assertThat(page.isFirst(), is(true));
        assertPage(page, 0);

        // 1st element in the page is a version
        assertThat(page.toList().get(0).getVersion(), is("1.4.7"));
        assertThat(page.toList().get(0).getDataType(), is(DataType.BLOCK_ADDED.getDataTypeName()));

        // Assert 2nd
        var deployProcessed = page.toList().get(1);

        Optional<EventStream> eventStream = eventAuditService.findEventStreamById(deployProcessed.getId());
        assertThat(eventStream.isPresent(), is(true));

        var expectedHash = "c7d0840f2275a18efcd716f425c06691f2ca1a0e6d7d7ecff49cab06c2428ee8";
        var storedJson = IOUtils.toString(eventStream.get(), StandardCharsets.UTF_8);

        assertThat(
                storedJson,
                hasJsonPath("$.DeployProcessed.deploy_hash", is(expectedHash))
        );

        // Load all pages
        for (int pageNumber = 1; pageNumber < page.getTotalPages(); pageNumber++) {
            page = eventAuditService.findAllSince(0, EventType.MAIN, null, page.nextPageable());
            assertPage(page, pageNumber);
            assertThat(page.isFirst(), is(false));
        }

        // Assert past pages
        assertThat(page.isLast(), is(true));
        var lastEvent = page.toList().get(page.getNumberOfElements() - 1);
        eventStream = eventAuditService.findEventStreamById(lastEvent.getId());
        assertThat(eventStream.isPresent(), is(true));
        storedJson = IOUtils.toString(eventStream.get(), StandardCharsets.UTF_8);
        expectedHash = "18060d1b9798dbed10a9394dbba69a02b406f531df4353067b3225d972c2c542";

        assertThat(
                storedJson,
                hasJsonPath("$.BlockAdded.block_hash", is(expectedHash))
        );
    }


    /**
     * Tests that an event cannot be saved more than once
     */
    @Test
    void testDuplicateEventIsNotPermitted() throws IOException {

        String json = IOUtils.toString(
                Objects.requireNonNull(EventAuditServiceTest.class.getResourceAsStream(MAIN_EVENTS_SINGLE_JSON)),
                StandardCharsets.UTF_8
        );

        var save = eventAuditService.save(json);

        assertThat(save, is(notNullValue()));
        assertThat(save.getId(), is(notNullValue()));
        assertOnlyOneGridFsFile(save);

        // Save the same event again
        var resaved = eventAuditService.save(json);

        // Asser the 2nd save does not result in a new document in mongo
        assertThat(save.getId(), is(resaved.getId()));

        // assert that only one GridFS file exists for the event
        assertOnlyOneGridFsFile(save);
    }

    private void assertOnlyOneGridFsFile(final AuditEventInfo save) {

        var filenameQuery = new Query(Criteria.where("filename").is("/events/" + save.getEventType() + "/" + save.getEventId() + ".json"));
        GridFSFindIterable gridFsFile = gridFsOperations.find(filenameQuery);
        //noinspection resource
        MongoCursor<GridFSFile> iterator = gridFsFile.iterator();
        assertThat(iterator.hasNext(), is(true));
        iterator.next();
        // Assert that there are no other
        assertThat(iterator.hasNext(), is(false));
    }

    private void assertPage(final Page<?> page, final int pageNumber) {
        assertThat(page.getSize(), is(3));
        assertThat(page.getTotalPages(), is(4));
        assertThat(page.getTotalElements(), is(12L));
        assertThat(page.getNumber(), is(pageNumber));
    }

    private void loadAllData() {
        jsonNode.forEach(child -> eventAuditService.save(child.toPrettyString()));
    }

    private void assertApiVersion(final AuditEventInfo eventInfo, final String expectedVersionNumber) {
        assertThat(eventInfo.getEventType(), is("main"));
        assertThat(eventInfo.getVersion(), is(expectedVersionNumber));
        assertThat(eventInfo.getDataType(), is(API_VERSION));
        assertThat(eventInfo.getEventId(), is(nullValue()));
    }
}
