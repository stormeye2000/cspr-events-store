package com.stormeye.event.store.config;

import com.casper.sdk.service.CasperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Unit tests that the {@link CasperServiceConfig} can create the {@link CasperService} for injection.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CasperServiceConfigTest {

    @Autowired
    private CasperServiceConfig casperServiceConfig;

    @Test
    void casperServiceCanBeInjected(@Autowired CasperService casperService) {
        assertThat(casperService, is(notNullValue()));
    }

    @Test
    void getNodeUri() {
        assertThat(casperServiceConfig.getNodeUri(), is(notNullValue()));
        assertThat(casperServiceConfig.getNodeUri().getHost(), is("localhost"));
        assertThat(casperServiceConfig.getNodeUri().getPort(), is(9999));
    }
}
