package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainUtils.assertIndexes;

/**
 * Test that the BLOCK table and indexes are correctly created
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BlockMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "BLOCK",
                "ID",
                "BLOCK_HASH",
                "PARENT_HASH",
                "TIMESTAMP",
                "STATE",
                "DEPLOY_COUNT",
                "TRANSFER_COUNT",
                "ERA_ID",
                "PROPOSER",
                "BLOCK_HEIGHT",
                "EVENT_ID");
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "BLOCK",
                "ID",
                "TIMESTAMP",
                "PROPOSER",
                "BLOCK_HEIGHT",
                "ERA_ID",
                "EVENT_ID",
                "BLOCK_HASH"
        );
    }
}
