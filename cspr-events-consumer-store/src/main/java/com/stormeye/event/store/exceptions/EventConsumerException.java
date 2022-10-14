package com.stormeye.event.store.exceptions;

/**
 * @author ian@meywood.com
 */
public class EventConsumerException extends RuntimeException {
    public EventConsumerException(Throwable cause) {
        super(cause);
    }
}
