package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainUtils.assertIndexes;

/**
 * Test that the ERA table and indexes are correctly created
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "ERA",
                "ID",
                "END_BLOCK_HEIGHT",
                "END_TIMESTAMP",
                "PROTOCOL_VERSION"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "ERA",
                "ID",
                "END_TIMESTAMP",
                "END_BLOCK_HEIGHT"
        );
    }
}
