package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertIndexes;

/**
 * Test that the VALIDATOR_REWARD table and indexes are correctly created.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ValidatorRewardMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "VALIDATOR_REWARD",
                "ID",
                "ERA_ID",
                "PUBLIC_KEY",
                "AMOUNT",
                "TIMESTAMP"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "VALIDATOR_REWARD",
                "ID",
                "ERA_ID",
                "PUBLIC_KEY",
                "TIMESTAMP"
        );
    }
}
