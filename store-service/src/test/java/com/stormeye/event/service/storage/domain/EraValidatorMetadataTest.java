package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainUtils.assertIndexes;

/**
 * Test that the ERA_VALIDATOR table and indexes are correctly created.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraValidatorMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testFieldNames() {

        assertFieldNames(dataSource,
                "ERA_VALIDATOR",
                "ID",
                "ERA_ID",
                "PUBLIC_KEY",
                "WEIGHT",
                "REWARDS",
                "HAS_EQUIVOCATION",
                "WAS_ACTIVE"
        );
    }

    @Test
    void testIndexes() {

        assertIndexes(dataSource,
                "ERA_VALIDATOR",
                "ID",
                "ERA_ID",
                "PUBLIC_KEY"
        );
    }
}
