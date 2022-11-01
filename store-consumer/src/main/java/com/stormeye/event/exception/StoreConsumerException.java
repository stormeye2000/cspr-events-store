package com.stormeye.event.exception;

/**
 * @author ian@meywood.com
 */
public class StoreConsumerException extends RuntimeException {

    public StoreConsumerException(Throwable cause) {
        super(cause);
    }

    public static RuntimeException getRuntimeException(final Exception e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new StoreConsumerException(e);
    }
}
