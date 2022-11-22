package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainUtils.assertIndexes;

/**
 * Test that the DEPLOY table and indexes are correctly created
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DeployMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "DEPLOY",
                "ID",
                "BLOCK_HASH",
                "ACCOUNT",
                "DEPLOY_HASH",
                "COST",
                "ERROR_MESSAGE",
                "TIMESTAMP",
                "EVENT_ID"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "DEPLOY",
                "ID",
                "TIMESTAMP",
                "DEPLOY_HASH",
                "ACCOUNT"
        );
    }
}