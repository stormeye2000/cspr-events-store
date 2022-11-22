package com.stormeye.producer.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Unit test for the {@link ServiceProperties} class.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.yml", "classpath:application-test.properties"})
class ServicePropertiesTest {

    @Autowired
    private ServiceProperties serviceProperties;

    @Test
    void getEmitters() throws URISyntaxException {

        var emitters = serviceProperties.getEmitters();
        assertThat(emitters, is(notNullValue()));
        assertThat(emitters, hasSize(1));
        assertThat(emitters.get(0), is(new URI("http://65.21.235.219:9999")));
    }

}
