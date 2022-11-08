package com.stormeye.event.store.client;

import com.casper.sdk.service.CasperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Unit tests that the {@link CasperServiceFactory} can create the {@link CasperService} for injection.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CasperServiceFactoryTest {

    @Autowired
    private CasperServiceFactory casperServiceFactory;

    @Test
    void casperServiceCanBeInjected(@Autowired CasperService casperService) {
        assertThat(casperService, is(notNullValue()));
    }

    @Test
    void isSingleton() {
        assertThat(casperServiceFactory.isSingleton(), is(true));
    }

    @Test
    void getObjectType() {
        assertThat(casperServiceFactory.getObjectType(), is(CasperService.class));
    }

    @Test
    void getNodeUri() {
        assertThat(casperServiceFactory.getNodeUri(), is(notNullValue()));
        assertThat(casperServiceFactory.getNodeUri().getHost(), is("localhost"));
        assertThat(casperServiceFactory.getNodeUri().getPort(), is(9999));
    }
}