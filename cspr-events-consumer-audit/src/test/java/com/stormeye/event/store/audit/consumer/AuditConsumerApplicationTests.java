package com.stormeye.event.store.audit.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AuditConsumerApplicationTests {

    @Value("${spring.data.mongodb.database:missing-database-name}")
    private String database;
    @Value("${spring.data.mongodb.port:0}")
    private int port;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // If this test passes the application context successfully loaded.
        assertThat(applicationContext, is(notNullValue()));
    }

    @Test
    void mongoProperties() {
        assertThat(database, is("casper-events-test"));
        assertThat(port, is(27017));
    }
}
