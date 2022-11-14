package com.stormeye.event.store.service.storage.impl.era;

import com.stormeye.event.service.storage.domain.Era;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for the {@link EraService}
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraServiceTest {

    @Test
    void storeAndFindById(@Autowired EraService eraService) {

        var timestamp = new Date();
        var earId = 6930L;
        var endBlockHeight = 1239489L;
        var protocolVersion = "1.4.8";

        var era = eraService.store(new Era(earId, endBlockHeight, timestamp, protocolVersion));
        var found = eraService.findById(era.getId());

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getProtocolVersion(), is(protocolVersion));
        assertThat(found.get().getEndBlockHeight(), is(endBlockHeight));
        assertThat(found.get().getEndTimestamp().getTime(), is(timestamp.getTime()));
    }
}
