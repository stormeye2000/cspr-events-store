package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertIndexes;

/**
 * Test that the TRANSFER table and indexes are correctly created.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TransferMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "TRANSFER",
                "ID",
                "TRANSFER_ID",
                "TRANSFER_HASH",
                "DEPLOY_HASH",
                "BLOCK_HASH",
                "FROM_ACCOUNT",
                "TO_ACCOUNT",
                "SOURCE_PURSE",
                "TARGET_PURSE",
                "AMOUNT",
                "TIMESTAMP"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "TRANSFER",
                "ID",
                "TRANSFER_ID",
                "DEPLOY_HASH",
                "BLOCK_HASH",
                "FROM_ACCOUNT",
                "TO_ACCOUNT",
                "TIMESTAMP"
        );
    }
}
