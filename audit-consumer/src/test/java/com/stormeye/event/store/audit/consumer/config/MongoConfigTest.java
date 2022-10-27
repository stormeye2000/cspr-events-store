package com.stormeye.event.store.audit.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MongoConfigTest {

    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private MongoConfig mongoConfig;

    /**
     * Unit test to ensure the mongo client is correctly configured.
     */
    @Test
    void mongoClientInjects() {
        assertThat(mongoOperations, is(notNullValue()));
        assertThat(mongoOperations, instanceOf(MongoTemplate.class));
        assertThat(((MongoTemplate) mongoOperations).getDb().getName(), is("casper-events-test"));
    }

    @Test
    void mongoConfigDatabaseName() {
        assertThat(mongoConfig.getDatabaseName(), is("casper-events-test"));
    }

    @Test
    void mongoConfigMongo() {
        assertThat(mongoConfig.mongoClient(), is(notNullValue()));
    }
}