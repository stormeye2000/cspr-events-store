package com.stormeye.event.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Unit tests the StoreConsumerException
 *
 * @author ian@meywood.com
 */
class StoreConsumerExceptionTest {

    @Test
    void getRunTimeException() {

        RuntimeException ouch = StoreConsumerException.getRuntimeException(new IOException("Ouch"));
        assertThat(ouch, instanceOf(StoreConsumerException.class));
        assertThat(ouch.getCause(), instanceOf(IOException.class));
        assertThat(ouch.getCause().getMessage(), is("Ouch"));

        RuntimeException runtime = StoreConsumerException.getRuntimeException(new RuntimeException("Runtime"));
        assertThat(runtime, instanceOf(RuntimeException.class));
        assertThat(runtime, not(instanceOf(StoreConsumerException.class)));
        assertThat(runtime.getMessage(), is("Runtime"));
    }
}
