package com.stormeye.event.service.storage.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

/**
 * Tests for the Era domain object
 *
 * @author ian@meywood.com
 */
class EraTest {

    @Test
    void isNew() {

        Era era = Era.builder().build();
        assertThat(era.isNew(), is(true));

        era.setId(1L);
        assertThat(era.isNew(), is(false));
    }

    @Test
    void testEquals() {
        Era one = Era.builder().id(1L).build();
        assertThat(one, is(one));
        assertThat(Era.builder().id(1L).build(), is(Era.builder().id(1L).build()));
        assertThat(Era.builder().id(1L).build(), not(is(Era.builder().id(2L).build())));
    }
}
