package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertIndexes;

/**
 * Test that the WITHDRAWAL table and indexes are correctly created.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class WithdrawalMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "WITHDRAWAL",
                "ID",
                "WITHDRAWAL_KEY",
                "DEPLOY_HASH",
                "VALIDATOR_PUBLIC_KEY",
                "UBONDER_PUBLIC_KEY",
                "BONDING_PURSE",
                "AMOUNT",
                "ERA_OF_CREATION",
                "TIMESTAMP",
                "CREATED_AT",
                "UPDATED_AT"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "WITHDRAWAL",
                "ID",
                "VALIDATOR_PUBLIC_KEY",
                "TIMESTAMP"
        );
    }
}
