package com.stormeye.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsUpload;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author ian@meywood.com
 */
class MongoUtilsTest {

    private static final String JSON = "/blocks.json";
    public static final String TEST_MONGO_DB = "test-mongo-db";
    private MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://localhost:27017/test-mongo-db"))
                        .build()
        );
    }

    @Test
    void deleteAllDocuments() {

        final MongoCollection<Document> documents = mongoClient.getDatabase(TEST_MONGO_DB).getCollection("documents");
        final MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,
                TEST_MONGO_DB);

        documents.insertOne(new Document().append("name", "one"));
        documents.insertOne(new Document().append("name", "two"));
        documents.insertOne(new Document().append("name", "three"));

        assertThat(documents.countDocuments(), is(3L));
        MongoUtils.deleteAllDocuments(mongoTemplate);

        assertThat(documents.countDocuments(), is(0L));
    }

    @Test
    void deleteAllFiles() {

        final SimpleMongoClientDatabaseFactory dbFactory = new SimpleMongoClientDatabaseFactory(
                "mongodb://localhost:27017/test-grid-fs"
        );

        final TestMongoConfig testMongoConfig = new TestMongoConfig("mongodb://localhost:27017/test-casper-events");

        final MappingMongoConverter mappingMongoConverter = testMongoConfig.mappingMongoConverter(
                dbFactory,
                new MongoCustomConversions(new ArrayList<>()),
                new MongoMappingContext()
        );

        final GridFsOperations gridFsOperations = new GridFsTemplate(
                dbFactory,
                mappingMongoConverter,
                "test-grid-fs"
        );

        assertThat(gridFsOperations, is(notNullValue()));

        for (int i = 0; i < 3; i++) {

            //noinspection ConstantConditions
            gridFsOperations.store(GridFsUpload.fromStream(MongoUtilsTest.class.getResourceAsStream(JSON))
                    .filename("blocks" + i + ".json")
                    .contentType("application/json")
                    .build());

            // Assert file created
            assertThat(gridFsOperations.getResource("block" + i + ".json"), is(notNullValue()));
        }

        // Assert 1st file exists
        //noinspection ConstantConditions
        assertThat(gridFsOperations.find(new Query()).first().getFilename(), is("blocks0.json"));

        // Delete all files
        MongoUtils.deleteAllFiles(gridFsOperations);
        assertThat(gridFsOperations.find(new Query()).first(), is(nullValue()));
    }
}
