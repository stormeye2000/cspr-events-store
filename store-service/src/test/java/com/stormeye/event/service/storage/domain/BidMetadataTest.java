package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static com.stormeye.event.service.storage.domain.DomainUtils.assertFieldNames;
import static com.stormeye.event.service.storage.domain.DomainUtils.assertIndexes;

/**
 * Test that the BID table and indexes are correctly created
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BidMetadataTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void bidFieldNames() {

        assertFieldNames(dataSource,
                "BID",
                "ID",
                "BID_KEY",
                "DEPLOY_HASH",
                "VALIDATOR_PUBLIC_KEY",
                "BONDING_PURSE",
                "STAKED_AMOUNT",
                "DELEGATION_RATE",
                "INACTIVE",
                "VESTING_SCHEDULE",
                "DELEGATORS",
                "TIMESTAMP"
        );
    }

    @Test
    void bidIndexes() {

        assertIndexes(dataSource,
                "BID",
                "ID",
                "DEPLOY_HASH",
                "BID_KEY",
                "VALIDATOR_PUBLIC_KEY",
                "TIMESTAMP"
        );
    }
}