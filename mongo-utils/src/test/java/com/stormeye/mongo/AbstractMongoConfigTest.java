package com.stormeye.mongo;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * Tests for the {@link AbstractMongoConfig}
 *
 * @author ian@meywood.com
 */
class AbstractMongoConfigTest {

    private TestMongoConfig testMongoConfig;

    @BeforeEach
    void setUp() {
        testMongoConfig = new TestMongoConfig("mongodb://localhost:27017/test-casper-events");
    }

    @Test
    void mongoClient() {
        MongoClient mongoClient = testMongoConfig.mongoClient();
        assertThat(mongoClient, is(notNullValue()));
    }

    @Test
    void getHost() {
        assertThat(testMongoConfig.getHost(), is("localhost"));
    }

    @Test
    void getDatabaseName() {
        assertThat(testMongoConfig.getDatabaseName(), is("test-casper-events"));
    }

    @Test
    void invalidUri() {
        TestMongoConfig mongoConfig = new TestMongoConfig("postges://localhost:27017/test-casper-events");
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, mongoConfig::mongoClient);

        assertThat(exception.getMessage(), startsWith("The connection string is invalid."));

        exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestMongoConfig("  ?localhost:27017:/test-casper-events")
        );

        assertThat(exception.getMessage(), startsWith("Invalid connectionString:  ?localhost:27017:/test-casper-events"));
    }

    @Test
    void multiHostUri() {

        TestMongoConfig mongoConfig = new TestMongoConfig(
                "mongodb://localhost1:27017/test-casper-events," +
                        "mongodb://localhost2:27017/test-casper-events");

        assertThat(mongoConfig.getHost(), is("localhost1"));
        assertThat(mongoConfig.getDatabaseName(), is("test-casper-events"));
    }


    @Test
    void missingDatabaseName() {

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestMongoConfig("mongodb://localhost1:27017/")
        );
        assertThat(exception.getMessage(), is("Missing database name in uri: mongodb://localhost1:27017/"));
    }

    @Test
    void mappingMongoConverter() {
        MappingMongoConverter mappingMongoConverter = this.testMongoConfig.mappingMongoConverter(
                new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/test-casper-events"),
                new MongoCustomConversions(new ArrayList<>()),
                new MongoMappingContext());

        assertThat(mappingMongoConverter, is(notNullValue()));
        assertThat(mappingMongoConverter.getTypeMapper(), instanceOf(DefaultMongoTypeMapper.class));
    }


}
