package com.stormeye.event.store.exceptions;

/**
 * @author ian@meywood.com
 */
public class NotFoundException extends RuntimeException{

    public NotFoundException(final String message) {
        super(message);
    }
}
