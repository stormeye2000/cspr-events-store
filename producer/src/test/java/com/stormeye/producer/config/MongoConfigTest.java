package com.stormeye.producer.config;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test the mongo configuration is correct
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MongoConfigTest {

    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private MongoConfig mongoConfig;
    @Autowired
    private GridFsOperations gridFsOperations;

    @Test
    void mongoClientInjects() {
        assertThat(mongoOperations, is(notNullValue()));
        assertThat(mongoOperations, instanceOf(MongoTemplate.class));
        assertThat(((MongoTemplate) mongoOperations).getDb().getName(), is("test-casper-producer"));
        assertThat(gridFsOperations, is(instanceOf(GridFsTemplate.class)));
    }

    @Test
    void getDatabaseName() {
        assertThat(mongoConfig.getDatabaseName(), is("test-casper-producer"));
    }

    @Test
    void mongoClient() {
        MongoClient mongoClient = mongoConfig.mongoClient();
        assertThat(mongoClient, is(notNullValue()));
    }
}
