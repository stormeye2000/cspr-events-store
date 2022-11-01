package com.stormeye.producer.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class ActuatorTest {

    @Test
    void testActuator() {
        final Health health = new Health.Builder(Status.UP).build();
        assertThat(health, is(notNullValue()));
        assertThat(health.getStatus(), is(Status.UP));
    }


}
