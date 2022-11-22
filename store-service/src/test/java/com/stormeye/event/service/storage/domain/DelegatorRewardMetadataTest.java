package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainMetadataUtils.assertIndexes;

/**
 * Test that the DELEGATOR_REWARDS table and indexes are correctly created
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DelegatorRewardMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "DELEGATOR_REWARD",
                "ID",
                "ERA_ID",
                "PUBLIC_KEY",
                "AMOUNT",
                "TIMESTAMP",
                "VALIDATOR_PUBLIC_KEY"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "DELEGATOR_REWARD",
                "ID",
                "PUBLIC_KEY",
                "VALIDATOR_PUBLIC_KEY",
                "ERA_ID",
                "TIMESTAMP"
        );
    }
}
